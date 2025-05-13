package com.sba301.online_ticket_sales.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sba301.online_ticket_sales.enums.Gender;
import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    // Userid from keycloak
    String userId;
    String fullName;
    String email;
    @Enumerated(EnumType.STRING)
    Gender gender;
    String password;
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate birthDate;

}
