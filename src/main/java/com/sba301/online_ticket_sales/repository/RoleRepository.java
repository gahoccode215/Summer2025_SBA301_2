package com.sba301.online_ticket_sales.repository;

import com.sba301.online_ticket_sales.entity.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
  Optional<Role> findByName(String name);
}
