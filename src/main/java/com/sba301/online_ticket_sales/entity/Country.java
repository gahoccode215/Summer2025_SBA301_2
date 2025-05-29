package com.sba301.online_ticket_sales.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Entity
@Table(name = "countries")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Country extends AbstractEntity<Integer> {

    @Column(nullable = false, unique = true)
    String name;
}
