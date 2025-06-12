package com.sba301.online_ticket_sales.repository;

import com.sba301.online_ticket_sales.entity.Role;
import com.sba301.online_ticket_sales.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleRepository extends JpaRepository<Role, Integer> {
  Optional<Role> findByName(String name);

  List<Role> findByNameIn(List<String> names);

  boolean existsByName(String name);

  @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
  Optional<User> findByUsernameOrEmail(@Param("identifier") String identifier);
}
