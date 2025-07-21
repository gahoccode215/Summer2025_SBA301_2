package com.sba301.online_ticket_sales.entity;

import com.sba301.online_ticket_sales.enums.AgeRestriction;
import com.sba301.online_ticket_sales.enums.MovieFormat;
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
  @Column(nullable = false)
  String title;

  @Column(columnDefinition = "TEXT")
  String description;

  @Column(nullable = false)
  Integer duration;

  @Column(name = "release_date")
  LocalDate releaseDate; // Ngày phát hành chính thức

  @Column(name = "premiere_date")
  LocalDate premiereDate; // Ngày công chiếu tại rạp

  @Column(name = "end_date")
  LocalDate endDate; // Ngày kết thúc chiếu

  @Column(name = "thumbnail_url")
  String thumbnailUrl;

  @Column(name = "trailer_url")
  String trailerUrl;

  @Enumerated(EnumType.STRING)
  @Column(name = "movie_status", nullable = false)
  MovieStatus movieStatus;

  @Enumerated(EnumType.STRING)
  @Column(name = "age_restriction", nullable = false)
  AgeRestriction ageRestriction;

  @ElementCollection(targetClass = MovieFormat.class)
  @Enumerated(EnumType.STRING)
  @CollectionTable(name = "movie_formats", joinColumns = @JoinColumn(name = "movie_id"))
  @Column(name = "format")
  List<MovieFormat> availableFormats = new ArrayList<>();

  @Column(name = "is_deleted")
  @Builder.Default
  Boolean isDeleted = false;

  @Column(name = "is_published")
  @Builder.Default
  Boolean isPublished = true;

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
