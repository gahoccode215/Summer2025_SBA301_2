package com.sba301.online_ticket_sales.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role extends AbstractEntity<Integer> {
  @Column(name = "name")
  String name;

  @Column(name = "description")
  String description;

  @ManyToMany(mappedBy = "roles")
  List<User> users;
}
