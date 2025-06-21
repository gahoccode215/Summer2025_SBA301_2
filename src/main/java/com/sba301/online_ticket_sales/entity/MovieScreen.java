package com.sba301.online_ticket_sales.entity;

import com.sba301.online_ticket_sales.enums.MovieScreenStatus;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "movie_screens")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieScreen extends AbstractEntity<Long> implements Serializable {

  @Column(name = "ticket_price")
  BigDecimal ticketPrice;

  @Column(name = "showtime", nullable = false)
  LocalDateTime showtime;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "movie_id")
  Movie movie;

  @ManyToOne
  @JoinColumn(name = "room_id", nullable = false)
  private Room room;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  MovieScreenStatus status;
}
