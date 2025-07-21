package com.sba301.online_ticket_sales.entity;

import com.sba301.online_ticket_sales.enums.PaymentStatus;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "ticket_orders")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TicketOrder extends AbstractEntity<Long> implements Serializable {
  @Column(name = "ticket_code", nullable = false, unique = true, length = 20)
  private String ticketCode;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "movie_showtimes_id", nullable = false)
  private MovieScreen movieScreen;

  @Column(name = "payment_status", nullable = false, length = 50)
  @Enumerated(EnumType.STRING)
  private PaymentStatus paymentStatus;

  @Column(name = "total_amount", nullable = false)
  private BigDecimal totalAmount;

  @Column(name = "is_printed", nullable = false)
  @Builder.Default
  private boolean isPrinted = false;

  @OneToMany(mappedBy = "ticketOrder", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<TicketOrderDetail> ticketDetails = new ArrayList<>();
}
