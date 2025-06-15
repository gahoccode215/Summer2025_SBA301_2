package com.sba301.online_ticket_sales.config;

import com.sba301.online_ticket_sales.constant.PredefinedRole;
import com.sba301.online_ticket_sales.entity.Country;
import com.sba301.online_ticket_sales.entity.Genre;
import com.sba301.online_ticket_sales.entity.Role;
import com.sba301.online_ticket_sales.entity.User;
import com.sba301.online_ticket_sales.enums.UserStatus;
import com.sba301.online_ticket_sales.repository.CountryRepository;
import com.sba301.online_ticket_sales.repository.GenreRepository;
import com.sba301.online_ticket_sales.repository.RoleRepository;
import com.sba301.online_ticket_sales.repository.UserRepository;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

  CountryRepository countryRepository;
  GenreRepository genreRepository;
  RoleRepository roleRepository;
  PasswordEncoder passwordEncoder;
  UserRepository userRepository;

  @Bean
  ApplicationRunner applicationRunner() {
    log.info("Initializing application.....");
    return args -> {
      if (countryRepository.count() == 0) {
        countryRepository.save(Country.builder().name("Việt Nam").build());
        countryRepository.save(Country.builder().name("Mỹ").build());
        countryRepository.save(Country.builder().name("Hàn Quốc").build());
        countryRepository.save(Country.builder().name("Nhật Bản").build());
        countryRepository.save(Country.builder().name("Trung Quốc").build());
      }
      if (genreRepository.count() == 0) {
        genreRepository.save(Genre.builder().name("Hành động").build());
        genreRepository.save(Genre.builder().name("Kinh dị").build());
        genreRepository.save(Genre.builder().name("Tình cảm").build());
        genreRepository.save(Genre.builder().name("Hoạt hình").build());
        genreRepository.save(Genre.builder().name("Viễn tưởng").build());
        genreRepository.save(Genre.builder().name("Hài").build());
      }
      if (roleRepository.count() == 0) {
        List<String> roles =
            List.of(
                PredefinedRole.CUSTOMER_ROLE,
                PredefinedRole.ADMIN_ROLE,
                PredefinedRole.MANAGER_ROLE,
                PredefinedRole.STAFF_ROLE);
        for (String role : roles) {
          roleRepository.save(Role.builder().name(role).build());
        }
      }

      initializeAdminAccounts();
      log.info("Application initialization completed .....");
    };
  }

  private void initializeAdminAccounts() {
    log.info("Initializing admin accounts...");

    // Lấy roles từ database
    Role adminRole =
        roleRepository
            .findByName(PredefinedRole.ADMIN_ROLE)
            .orElseThrow(() -> new RuntimeException("ADMIN role not found"));
    Role managerRole =
        roleRepository
            .findByName(PredefinedRole.MANAGER_ROLE)
            .orElseThrow(() -> new RuntimeException("MANAGER role not found"));
    Role staffRole =
        roleRepository
            .findByName(PredefinedRole.STAFF_ROLE)
            .orElseThrow(() -> new RuntimeException("STAFF_ROLE role not found"));

    // Danh sách tài khoản ADMIN
    List<String> adminUsernames = List.of("minhadmin", "phuocadmin", "thanhadmin");
    createAccountsForRole(adminUsernames, adminRole, "ADMIN");

    // Danh sách tài khoản MANAGER
    List<String> managerUsernames = List.of("minhmanager", "phuocmanager", "thanhmanager");
    createAccountsForRole(managerUsernames, managerRole, "MANAGER");

    // Danh sách tài khoản STAFF_ROLE
    List<String> staffUsernames = List.of("minhstaff", "phuocstaff", "thanhstaff");
    createAccountsForRole(staffUsernames, staffRole, "STAFF_ROLE");

    log.info("Admin accounts initialization completed");
  }

  /**
   * Tạo tài khoản cho một role cụ thể
   *
   * @param usernames danh sách username
   * @param role role entity
   * @param roleName tên role để log
   */
  private void createAccountsForRole(List<String> usernames, Role role, String roleName) {
    for (String username : usernames) {
      // Kiểm tra tài khoản đã tồn tại chưa
      if (userRepository.existsByUsername(username)) {
        log.info("{} account '{}' already exists, skipping...", roleName, username);
        continue;
      }

      // Tạo user entity
      User user =
          User.builder()
              .username(username)
              .password(passwordEncoder.encode(username)) // Password giống username
              .fullName(generateFullName(username, roleName))
              .status(UserStatus.ACTIVE)
              .roles(List.of(role))
              .build();

      // Lưu user vào database
      userRepository.save(user);
      log.info("Created {} account: username='{}', password='{}'", roleName, username, username);
    }
  }

  /**
   * Tạo full name từ username và role
   *
   * @param username username
   * @param roleName role name
   * @return full name
   */
  private String generateFullName(String username, String roleName) {
    // Extract tên từ username (loại bỏ role suffix)
    String name = username.replaceAll("(admin|manager|staff)$", "");

    // Viết hoa chữ cái đầu
    String capitalizedName = name.substring(0, 1).toUpperCase() + name.substring(1);

    // Kết hợp với role
    return capitalizedName + " " + roleName;
  }
}
