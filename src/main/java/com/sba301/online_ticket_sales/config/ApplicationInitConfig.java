package com.sba301.online_ticket_sales.config;

import static org.springframework.amqp.core.QueueBuilder.LeaderLocator.random;

import com.sba301.online_ticket_sales.constant.PredefinedRole;
import com.sba301.online_ticket_sales.entity.*;
import com.sba301.online_ticket_sales.enums.UserStatus;
import com.sba301.online_ticket_sales.repository.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
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
  MovieRepository movieRepository;
  RoomRepository roomRepository;
  MovieScreenRepository movieScreenRepository;

  private final Random random = new Random();

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
      //      seedMovieScreens(7);
      log.info("Application initialization completed .....");
    };
  }

  public void seedMovieScreens(int totalDays) {
    List<Movie> movies = movieRepository.findAll();
    List<Room> rooms = roomRepository.findAll();

    LocalDateTime now = LocalDateTime.now();

    for (Room room : rooms) {
      for (int i = 0; i < totalDays; i++) {
        LocalDateTime dayStart = now.plusDays(i).withHour(8).withMinute(0);

        int screensToday = random.nextInt(3) + 3; // 3–5 suất/ngày
        for (int j = 0; j < screensToday; j++) {
          Optional<MovieScreen> created = tryCreateNonConflictMovieScreen(dayStart, room, movies);
          created.ifPresent(movieScreenRepository::save);
        }
      }
    }
  }

  private Optional<MovieScreen> tryCreateNonConflictMovieScreen(
      LocalDateTime dayStart, Room room, List<Movie> movies) {

    int retry = 0;
    while (retry < 10) {
      Movie movie = movies.get(random.nextInt(movies.size()));
      int startHour = 8 + random.nextInt(10);
      int startMinute = random.nextInt(2) * 30;

      LocalDateTime startTime = dayStart.withHour(startHour).withMinute(startMinute);
      LocalDateTime endTime = startTime.plusMinutes(movie.getDuration());

      int conflict = movieScreenRepository.hasConflictSchedule(room.getId(), startTime, endTime);
      if (conflict == 0) {
        BigDecimal ticketPrice = BigDecimal.valueOf(60 + random.nextInt(41));
        MovieScreen screen =
            MovieScreen.builder()
                .room(room)
                .movie(movie)
                .showtime(startTime)
                .ticketPrice(ticketPrice)
                .build();
        return Optional.of(screen);
      }
      retry++;
    }
    return Optional.empty();
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
              .email("" + username + "@example.com") // Email giả định
              .password(passwordEncoder.encode(username)) // Password giống username
              .fullName(generateFullName(username, roleName))
              .status(UserStatus.ACTIVE)
              .roles(List.of(role))
              .build();
      role.getUsers().add(user);
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
