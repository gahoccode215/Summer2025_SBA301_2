package com.sba301.online_ticket_sales.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "ticket_order_details")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Setter
public class TicketOrderDetail extends AbstractEntity<Long> implements Serializable {
  @Column(name = "seat_code", nullable = false)
  private String seatCode;

  @Column(name = "price", nullable = false)
  private BigDecimal price;

  @ManyToOne
  @JoinColumn(name = "ticket_order_id", nullable = false)
  private TicketOrder ticketOrder;
}
