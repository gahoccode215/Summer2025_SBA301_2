package com.sba301.online_ticket_sales.repository;

import com.sba301.online_ticket_sales.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  @Query(value = "select r.name from Role r inner join User u on r.id = u.id where u.id= :userId")
  List<String> findAllRolesByUserId(Long userId);
}
