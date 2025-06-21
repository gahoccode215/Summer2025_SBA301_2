package com.sba301.online_ticket_sales.service.impl;

import com.sba301.online_ticket_sales.dto.booking.request.VnPayCallbackParamRequest;
import com.sba301.online_ticket_sales.dto.booking.response.TicketOrderDTO;
import com.sba301.online_ticket_sales.entity.MovieScreen;
import com.sba301.online_ticket_sales.entity.TicketOrder;
import com.sba301.online_ticket_sales.entity.TicketOrderDetail;
import com.sba301.online_ticket_sales.entity.User;
import com.sba301.online_ticket_sales.enums.ErrorCode;
import com.sba301.online_ticket_sales.enums.PaymentStatus;
import com.sba301.online_ticket_sales.exception.AppException;
import com.sba301.online_ticket_sales.repository.*;
import com.sba301.online_ticket_sales.service.PaymentService;
import com.sba301.online_ticket_sales.service.PaymentStrategy;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {
  private final PaymentStrategy paymentStrategy;
  private final TicketOrderRedisRepository ticketOrderRedisRepository;
  private final UserRepository userRepository;
  private final MovieScreenRepository movieScreenRepository;
  private final TicketOrderRepository ticketOrderRepository;
  private final OrderSeatRedisRepository orderSeatRedisRepository;

  @Override
  public String createPayment(String orderCode, HttpServletRequest httpServletRequest) {
    try {
      TicketOrderDTO order =
          ticketOrderRedisRepository
              .findById(orderCode)
              .orElseThrow(() -> new AppException(ErrorCode.TICKET_ORDER_NOT_FOUND_CACHE));

      Long amount = order.getTotalPrice().longValue() * 100L;
      var vnpParams =
          paymentStrategy.buildVnPayParams(amount, order.getTicketCode(), httpServletRequest);
      List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
      Collections.sort(fieldNames);
      StringBuilder hashData = new StringBuilder();
      StringBuilder query = new StringBuilder();
      Iterator itr = fieldNames.iterator();

      while (itr.hasNext()) {
        String fieldName = (String) itr.next();
        String fieldValue = vnpParams.get(fieldName);
        if ((fieldValue != null) && (!fieldValue.isEmpty())) {

          hashData.append(fieldName);
          hashData.append('=');
          hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

          query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
          query.append('=');
          query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
          if (itr.hasNext()) {
            query.append('&');
            hashData.append('&');
          }
        }
      }
      String queryUrl = query.toString();
      String vnpSecureHash = paymentStrategy.hmacSHA512(hashData.toString());
      queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
      String paymentUrl = paymentStrategy.getPaymentUrl(queryUrl);
      return paymentUrl;
    } catch (Exception e) {
      log.error("Error creating payment: {}", e.getMessage());
      throw new AppException(ErrorCode.PAYMENT_ERROR);
    }
  }

  @Override
  @Transactional
  public void handlePaymentCallback(VnPayCallbackParamRequest request) {
    TicketOrderDTO order =
        ticketOrderRedisRepository
            .findById(request.getVnp_TxnRef())
            .orElseThrow(() -> new AppException(ErrorCode.TICKET_ORDER_NOT_FOUND_CACHE));

    PaymentStatus paymentStatus = mapTransactionStatus(request.getVnp_TransactionStatus());
    if (paymentStatus == PaymentStatus.SUCCESS) {
      TicketOrder ticketOrder = new TicketOrder();
      ticketOrder.setTicketCode(order.getTicketCode());
      ticketOrder.setTotalAmount(order.getTotalPrice());

      User user =
          userRepository
              .findById(order.getUserId())
              .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
      MovieScreen movieScreen =
          movieScreenRepository
              .findById(order.getShowtimeId())
              .orElseThrow(() -> new AppException(ErrorCode.MOVIESCREEN_NOT_WORKING));

      List<String> seatCodes = order.getSeatCode();
      List<TicketOrderDetail> ticketOrderDetails = new ArrayList<>();
      List<String> releasedSeats = new ArrayList<>();
      BigDecimal ticketPrice = order.getTotalPrice().divide(new BigDecimal(seatCodes.size()));
      for (String seatCode : seatCodes) {
        TicketOrderDetail ticketOrderDetail = new TicketOrderDetail();
        ticketOrderDetail.setSeatCode(seatCode);
        ticketOrderDetail.setPrice(ticketPrice);
        ticketOrderDetails.add(ticketOrderDetail);
        ticketOrderDetail.setTicketOrder(ticketOrder);
        releasedSeats.add(seatCode);
      }

      ticketOrder.setUser(user);
      ticketOrder.setMovieScreen(movieScreen);
      ticketOrder.setTicketDetails(ticketOrderDetails);
      ticketOrder.setPaymentStatus(PaymentStatus.SUCCESS);

      Long result = ticketOrderRepository.save(ticketOrder).getId();

      if (result == null || result <= 0) {
        log.error("Failed to save ticket order for orderId: {}", order.getTicketCode());
        throw new AppException(ErrorCode.FAILED_TO_CREATE_ORDER);
      }
      log.info("Payment successful for order: {}", order.getTicketCode());

      ticketOrderRedisRepository.deleteById(order.getTicketCode());
      log.info("Cleared cached ticket order for orderId: {}", order.getTicketCode());
      orderSeatRedisRepository.releaseSeat(order.getShowtimeId(), releasedSeats);
      log.info("Released seats for order: {}", order.getTicketCode());

    } else if (paymentStatus == PaymentStatus.CANCELLED || paymentStatus == PaymentStatus.EXPIRED) {
      log.info("Payment cancelled or expired for order: {}", order.getTicketCode());
      throw new AppException(ErrorCode.PAYMENT_FAILED_OR_EXPIRED);
    } else {
      throw new AppException(ErrorCode.PAYMENT_ERROR);
    }
  }

  private PaymentStatus mapTransactionStatus(String vnpayStatus) {
    return switch (vnpayStatus) {
      case "00" -> PaymentStatus.SUCCESS;
      case "24" -> PaymentStatus.CANCELLED;
      case "02" -> PaymentStatus.EXPIRED;
      default -> PaymentStatus.FAILED;
    };
  }
}
