package com.sba301.online_ticket_sales.entity;

import com.sba301.online_ticket_sales.enums.Country;
import com.sba301.online_ticket_sales.enums.MovieStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "movies")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Movie extends AbstractEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String title;
    String description;
    Integer duration;
    LocalDate releaseDate;
    String trailerUrl;
    String director;
    @Enumerated(EnumType.STRING)
    Country country;
    @Enumerated(EnumType.STRING)
    MovieStatus movieStatus;

}
