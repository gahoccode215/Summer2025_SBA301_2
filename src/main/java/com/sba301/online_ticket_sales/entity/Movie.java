package com.sba301.online_ticket_sales.entity;

import com.sba301.online_ticket_sales.enums.Country;
import com.sba301.online_ticket_sales.enums.MovieStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToMany
    @JoinTable(
            name = "movie_directors",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id")
    )
    List<Person> directors = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "movie_actors",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id")
    )
    List<Person> actors = new ArrayList<>();

}
