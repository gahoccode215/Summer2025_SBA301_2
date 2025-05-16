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
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends AbstractEntity<String>{
    String fullName;
    String email;
    @Enumerated(EnumType.STRING)
    Gender gender;
    String password;
    LocalDate birthDate;
}
