package com.sba301.online_ticket_sales.specification;

import com.sba301.online_ticket_sales.entity.Cinema;
import com.sba301.online_ticket_sales.entity.Role;
import com.sba301.online_ticket_sales.entity.User;
import com.sba301.online_ticket_sales.enums.UserStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
  public static Specification<User> hasKeyword(String keyword) {
    return (root, query, cb) -> {
      if (keyword == null || keyword.isBlank()) {
        return cb.conjunction();
      }

      String searchPattern = "%" + keyword.toLowerCase().trim() + "%";

      Predicate fullNamePredicate = cb.like(cb.lower(root.get("fullName")), searchPattern);
      Predicate usernamePredicate = cb.like(cb.lower(root.get("username")), searchPattern);
      Predicate emailPredicate = cb.like(cb.lower(root.get("email")), searchPattern);

      return cb.or(fullNamePredicate, usernamePredicate, emailPredicate);
    };
  }

  public static Specification<User> hasStatus(UserStatus status) {
    return (root, query, cb) -> {
      if (status == null) {
        return cb.conjunction();
      }
      return cb.equal(root.get("status"), status);
    };
  }

  public static Specification<User> hasRole(String roleName) {
    return (root, query, cb) -> {
      if (roleName == null) {
        return cb.conjunction();
      }

      Join<User, Role> roleJoin = root.join("roles");
      return cb.equal(roleJoin.get("name"), roleName);
    };
  }

  public static Specification<User> belongsToCinema(Long cinemaId) {
    return (root, query, cb) -> {
      if (cinemaId == null) {
        return cb.conjunction();
      }

      Join<User, Cinema> cinemaJoin = root.join("managedCinemas");
      return cb.equal(cinemaJoin.get("id"), cinemaId);
    };
  }

  public static Specification<User> belongsToCinemas(List<Long> cinemaIds) {
    return (root, query, cb) -> {
      if (cinemaIds == null || cinemaIds.isEmpty()) {
        return cb.conjunction();
      }

      Join<User, Cinema> cinemaJoin = root.join("managedCinemas");
      return cinemaJoin.get("id").in(cinemaIds);
    };
  }

  public static Specification<User> createdBetween(LocalDateTime from, LocalDateTime to) {
    return (root, query, cb) -> {
      if (from == null && to == null) {
        return cb.conjunction();
      }

      if (from != null && to != null) {
        return cb.between(root.get("createdAt"), from, to);
      } else if (from != null) {
        return cb.greaterThanOrEqualTo(root.get("createdAt"), from);
      } else {
        return cb.lessThanOrEqualTo(root.get("createdAt"), to);
      }
    };
  }

  public static Specification<User> inProvince(String province) {
    return (root, query, cb) -> {
      if (province == null || province.isBlank()) {
        return cb.conjunction();
      }

      Join<User, Cinema> cinemaJoin = root.join("managedCinemas");
      return cb.equal(cinemaJoin.get("province"), province);
    };
  }
}
