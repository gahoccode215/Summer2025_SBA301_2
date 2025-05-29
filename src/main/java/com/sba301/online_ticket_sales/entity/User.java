package com.sba301.online_ticket_sales.entity;

import com.sba301.online_ticket_sales.enums.Gender;
import com.sba301.online_ticket_sales.enums.UserStatus;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends AbstractEntity<Long> implements UserDetails, Serializable {
  @Column(name = "full_name")
  String fullName;

  @Column(name = "email", unique = true, nullable = false)
  String email;

  @Enumerated(EnumType.STRING)
  Gender gender;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  UserStatus status;

  @Column(name = "password", nullable = false)
  String password;

  @Column(name = "birth_date")
  LocalDate birthDate;

  @Column(name = "phone")
  String phone;

  @ManyToMany()
  @JoinTable(
      name = "user_has_role",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  List<Role> roles;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of();
  }

  @Override
  public String getUsername() {
    return this.email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return UserDetails.super.isAccountNonExpired();
  }

  @Override
  public boolean isAccountNonLocked() {
    return UserDetails.super.isAccountNonLocked();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return UserDetails.super.isCredentialsNonExpired();
  }

  @Override
  public boolean isEnabled() {
    return UserStatus.ACTIVE.equals(status);
  }

  @PrePersist
  protected void onCreate() {
    if (status == null) {
      status = UserStatus.ACTIVE;
    }
  }
}
