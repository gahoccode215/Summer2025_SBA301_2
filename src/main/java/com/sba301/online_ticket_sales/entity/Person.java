package com.sba301.online_ticket_sales.entity;

import com.sba301.online_ticket_sales.enums.Occupation;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "persons")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Person extends AbstractEntity<Integer> implements Serializable {

  String name;
  String description;
  LocalDate birthDate;
  Double height;

  @Enumerated(EnumType.STRING)
  Occupation occupation;

  String biography;
  boolean isDeleted;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "country_id")
  Country country;

  @ElementCollection
  @CollectionTable(name = "person_images", joinColumns = @JoinColumn(name = "person_id"))
  @Column(name = "image_url")
  List<String> images = new ArrayList<>();

  @ManyToMany(mappedBy = "directors")
  List<Movie> directedMovies = new ArrayList<>();

  @ManyToMany(mappedBy = "actors")
  List<Movie> actedMovies = new ArrayList<>();
}
