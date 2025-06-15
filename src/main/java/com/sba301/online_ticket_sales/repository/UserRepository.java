package com.sba301.online_ticket_sales.repository;

import com.sba301.online_ticket_sales.entity.User;
import com.sba301.online_ticket_sales.enums.UserStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
  Optional<User> findByEmail(String email);

  Optional<User> findByUsername(String username);

  @Query(value = "select r.name from Role r inner join User u on r.id = u.id where u.id= :userId")
  List<String> findAllRolesByUserId(Long userId);

  boolean existsByUsername(String username);

  @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
  Optional<User> findByUsernameOrEmail(@Param("identifier") String identifier);

  boolean existsByEmail(String email);

  // ADMIN: Lấy tất cả users với filter
  @Query(
      "SELECT DISTINCT u FROM User u "
          + "LEFT JOIN FETCH u.roles r "
          + "LEFT JOIN FETCH u.managedCinemas c "
          + "WHERE (:keyword IS NULL OR "
          + "      LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "      LOWER(COALESCE(u.username, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "      LOWER(COALESCE(u.email, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
          + "AND (:status IS NULL OR u.status = :status) "
          + "AND (:roleName IS NULL OR r.name = :roleName) "
          + "AND (:cinemaId IS NULL OR c.id = :cinemaId) "
          + "AND (:province IS NULL OR c.province = :province) "
          + "AND (:createdFrom IS NULL OR u.createdAt >= :createdFrom) "
          + "AND (:createdTo IS NULL OR u.createdAt <= :createdTo)")
  Page<User> findUsersWithFilters(
      @Param("keyword") String keyword,
      @Param("status") UserStatus status,
      @Param("roleName") String roleName,
      @Param("cinemaId") Long cinemaId,
      @Param("province") String province,
      @Param("createdFrom") LocalDateTime createdFrom,
      @Param("createdTo") LocalDateTime createdTo,
      Pageable pageable);

  // MANAGER: Chỉ lấy users trong rạp mình quản lý
  @Query(
      "SELECT DISTINCT u FROM User u "
          + "JOIN FETCH u.roles r "
          + "JOIN FETCH u.managedCinemas c "
          + "WHERE c.id IN :cinemaIds "
          + "AND r.name IN ('STAFF_ROLE', 'CUSTOMER') "
          + "AND (:keyword IS NULL OR "
          + "      LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "      LOWER(COALESCE(u.username, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "      LOWER(COALESCE(u.email, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
          + "AND (:status IS NULL OR u.status = :status) "
          + "AND (:roleName IS NULL OR r.name = :roleName)")
  Page<User> findUsersByCinemaIds(
      @Param("cinemaIds") List<Long> cinemaIds,
      @Param("keyword") String keyword,
      @Param("status") UserStatus status,
      @Param("roleName") String roleName,
      Pageable pageable);
}
