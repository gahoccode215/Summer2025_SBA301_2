package com.sba301.online_ticket_sales.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "permissions")
public class Permission extends AbstractEntity<Integer> {
  private String name;
  private String description;
}
