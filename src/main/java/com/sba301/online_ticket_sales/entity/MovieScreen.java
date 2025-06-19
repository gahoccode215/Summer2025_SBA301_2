package com.sba301.online_ticket_sales.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
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
  @Column(nullable = false)
  String title;

  @Column(name = "ticket_price")
  BigDecimal ticketPrice;

  @Column(name = "screen_date")
  LocalDate screenDate;

  @Column(name = "screen_time")
  LocalTime screenTime;

  // Nếu có relationship với Movie và Cinema
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "movie_id")
  Movie movie;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cinema_id")
  Cinema cinema;

  @Column(name = "total_seats")
  Integer totalSeats;

  @Column(name = "available_seats")
  Integer availableSeats;

  @Column(name = "status")
  String status; // ACTIVE, INACTIVE, CANCELLED
}
