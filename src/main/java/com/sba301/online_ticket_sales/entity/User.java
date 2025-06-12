package com.sba301.online_ticket_sales.entity;

import com.sba301.online_ticket_sales.constant.PredefinedRole;
import com.sba301.online_ticket_sales.enums.Gender;
import com.sba301.online_ticket_sales.enums.UserStatus;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

  @Column(name = "email", unique = true)
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

  @Column(name = "username", unique = true)
  String username;

  @Column(name = "address")
  String address;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "user_has_role",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  List<Role> roles;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "user_manage_cinema",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "cinema_id"))
  private List<Cinema> managedCinemas;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles.stream()
        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
        .collect(Collectors.toList());
  }

  @Override
  public String getUsername() {
    // Nếu có username thì dùng username (tài khoản quản trị)
    // Nếu không có username thì dùng email (customer)
    return this.username != null ? this.username : this.email;
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

  public boolean hasRole(String roleName) {
    return roles.stream().anyMatch(role -> role.getName().equals(roleName));
  }

  public boolean isAdmin() {
    return hasRole(PredefinedRole.ADMIN_ROLE);
  }

  public boolean isManager() {
    return hasRole(PredefinedRole.MANAGER_ROLE);
  }

  public boolean isStaff() {
    return hasRole(PredefinedRole.STAFF);
  }

  public boolean isAdminAccount() {
    return this.username != null && (isAdmin() || isManager() || isStaff());
  }

  public boolean isCustomerAccount() {
    return this.username == null || (!isAdmin() && !isManager() && !isStaff());
  }
}
