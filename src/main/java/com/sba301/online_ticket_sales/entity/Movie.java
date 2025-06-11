package com.sba301.online_ticket_sales.entity;

import com.sba301.online_ticket_sales.enums.AgeRestriction;
import com.sba301.online_ticket_sales.enums.MovieStatus;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "movies")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Movie extends AbstractEntity<Long> implements Serializable {
  String title;
  String description;
  Integer duration;
  LocalDate releaseDate;
  String trailerUrl;
  String image;

  @Enumerated(EnumType.STRING)
  MovieStatus movieStatus;

  @Enumerated(EnumType.STRING)
  AgeRestriction ageRestriction;

  boolean isDeleted;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "country_id")
  Country country;

  @ManyToMany
  @JoinTable(
      name = "movie_genres",
      joinColumns = @JoinColumn(name = "movie_id"),
      inverseJoinColumns = @JoinColumn(name = "genre_id"))
  List<Genre> genres = new ArrayList<>();

  @ManyToMany
  @JoinTable(
      name = "movie_directors",
      joinColumns = @JoinColumn(name = "movie_id"),
      inverseJoinColumns = @JoinColumn(name = "person_id"))
  List<Person> directors = new ArrayList<>();

  @ManyToMany
  @JoinTable(
      name = "movie_actors",
      joinColumns = @JoinColumn(name = "movie_id"),
      inverseJoinColumns = @JoinColumn(name = "person_id"))
  List<Person> actors = new ArrayList<>();
}
