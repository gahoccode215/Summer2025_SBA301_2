package com.sba301.online_ticket_sales.entity;

import com.sba301.online_ticket_sales.enums.PaymentMethod;
import com.sba301.online_ticket_sales.enums.PaymentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "payments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment extends AbstractEntity<Long> {

  @Column(name = "transaction_ref", unique = true, nullable = false)
  String transactionRef; // vnp_TxnRef

  @Column(name = "vnpay_transaction_no")
  String vnpayTransactionNo; // vnp_TransactionNo

  @Column(name = "amount", nullable = false, precision = 15, scale = 2)
  BigDecimal amount;

  @Column(name = "order_info", nullable = false)
  String orderInfo;

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_method", nullable = false)
  PaymentMethod paymentMethod;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  PaymentStatus status;

  @Column(name = "bank_code")
  String bankCode;

  @Column(name = "bank_tran_no")
  String bankTranNo;

  @Column(name = "pay_date")
  LocalDateTime payDate;

  @Column(name = "response_code")
  String responseCode;

  @Column(name = "secure_hash")
  String secureHash;

  //    @OneToOne(fetch = FetchType.LAZY)
  //    @JoinColumn(name = "booking_id")
  //    Booking booking;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  User user;
}
