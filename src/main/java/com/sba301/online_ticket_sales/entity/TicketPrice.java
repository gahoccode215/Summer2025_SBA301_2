package com.sba301.online_ticket_sales.entity;

import com.sba301.online_ticket_sales.enums.DateType;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "ticket_prices")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Setter
public class TicketPrice extends AbstractEntity<Long> implements Serializable {
  @ManyToOne
  @JoinColumn(name = "cinema_id", nullable = false)
  private Cinema cinema;

  @Column(name = "date_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private DateType dateType;

  @Column(name = "price", nullable = false)
  private BigDecimal price;

  @Column(name = "is_active", nullable = false)
  @Builder.Default
  private boolean isActive = true;

  public void assignCinema(Cinema cinema) {
    this.cinema = cinema;
  }
}
