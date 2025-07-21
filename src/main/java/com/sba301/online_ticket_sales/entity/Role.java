package com.sba301.online_ticket_sales.entity;

import jakarta.persistence.*;
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

  @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
  List<User> users;
}
