package com.sba301.online_ticket_sales.repository;

import com.sba301.online_ticket_sales.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  Optional<User> findByUsername(String username);

  @Query(value = "select r.name from Role r inner join User u on r.id = u.id where u.id= :userId")
  List<String> findAllRolesByUserId(Long userId);

  boolean existsByUsername(String username);

  @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
  Optional<User> findByUsernameOrEmail(@Param("identifier") String identifier);

  boolean existsByEmail(String email);
}
