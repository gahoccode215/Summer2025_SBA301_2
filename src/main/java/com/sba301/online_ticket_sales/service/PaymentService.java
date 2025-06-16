package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.dto.payment.request.PaymentRequest;
import com.sba301.online_ticket_sales.dto.payment.request.PaymentVerificationRequest;
import com.sba301.online_ticket_sales.dto.payment.response.PaymentResult;
import com.sba301.online_ticket_sales.dto.payment.response.PaymentVerificationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService  {

    private final Map<String, PaymentStrategy> paymentStrategies;

    public PaymentResult createPayment(String paymentMethod, PaymentRequest request) {
        PaymentStrategy strategy = getPaymentStrategy(paymentMethod);

        log.info("Creating payment for booking {} using {}",
                request.getBookingId(), paymentMethod);

        return strategy.createPayment(request);
    }

    public PaymentVerificationResult verifyPayment(String paymentMethod,
                                                   PaymentVerificationRequest request) {
        PaymentStrategy strategy = getPaymentStrategy(paymentMethod);

        log.info("Verifying payment for transaction {}", request.getTransactionId());

        return strategy.verifyPayment(request);
    }

    private PaymentStrategy getPaymentStrategy(String paymentMethod) {
        PaymentStrategy strategy = paymentStrategies.get(paymentMethod);
        if (strategy == null) {
//            throw new UnsupportedPaymentMethodException(
//                    "Payment method not supported: " + paymentMethod);
        }
        return strategy;
    }
}
