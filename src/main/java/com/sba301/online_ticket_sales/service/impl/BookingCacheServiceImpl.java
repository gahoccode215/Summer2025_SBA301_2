package com.sba301.online_ticket_sales.service.impl;

import com.sba301.online_ticket_sales.dto.booking.response.TicketOrderDTO;
import com.sba301.online_ticket_sales.enums.ErrorCode;
import com.sba301.online_ticket_sales.exception.AppException;
import com.sba301.online_ticket_sales.repository.*;
import com.sba301.online_ticket_sales.service.BookingCacheService;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookingCacheServiceImpl implements BookingCacheService {
  private final TicketOrderRedisRepository ticketOrderRedisRepository;
  private final OrderSeatRedisRepository orderSeatRedisRepository;

  private static final String SEAT_HOLD_KEY_PREFIX = "seatHold_showtimeId_";

  @Override
  public void holdSeat(Long showtimeId, List<String> seatCodes, String ticketCode) {
    for (String seat : seatCodes) {
      String key = SEAT_HOLD_KEY_PREFIX + showtimeId + ":" + seat;
      orderSeatRedisRepository.save(key, ticketCode, Duration.ofMinutes(15));
    }
  }

  @Override
  public void releaseSeat(Long showtimeId, List<String> seatCode) {
    List<String> keys =
        seatCode.stream().map(seat -> SEAT_HOLD_KEY_PREFIX + showtimeId + ":" + seat).toList();
    orderSeatRedisRepository.deleteList(keys);
  }

  @Override
  public List<String> getHeldSeats(Long showtimeId) {
    String prefix = SEAT_HOLD_KEY_PREFIX + showtimeId + ":";
    String pattern = prefix + "*";

    Set<String> keys = orderSeatRedisRepository.getKeysByPattern(pattern);
    if (keys == null || keys.isEmpty()) return List.of();

    return keys.stream().map(key -> key.substring(prefix.length())).toList();
  }

  @Override
  public boolean isSeatHeld(Long showtimeId, String seatCode) {
    String key = SEAT_HOLD_KEY_PREFIX + showtimeId + ":" + seatCode;
    return orderSeatRedisRepository.exists(key);
  }

  @Override
  public void saveTicketOrder(TicketOrderDTO ticketOrderDTO) {
    String ticketCode = ticketOrderDTO.getTicketCode();
    if (ticketOrderRedisRepository.existsById(ticketCode)) {
      throw new IllegalArgumentException(
          "Ticket order with code " + ticketCode + " already exists.");
    }
    ticketOrderRedisRepository.save(ticketOrderDTO);
  }

  @Override
  public TicketOrderDTO getTicketOrder(String ticketCode) {
    return ticketOrderRedisRepository
        .findById(ticketCode)
        .orElseThrow(() -> new AppException(ErrorCode.TICKET_ORDER_NOT_FOUND_CACHE));
  }

  @Override
  public void deleteTicketOrder(String ticketCode) {
    if (!ticketOrderRedisRepository.existsById(ticketCode)) {
      throw new AppException(ErrorCode.TICKET_ORDER_NOT_FOUND_CACHE);
    }
    ticketOrderRedisRepository.deleteById(ticketCode);
  }
}
