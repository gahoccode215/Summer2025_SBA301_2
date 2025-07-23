package com.sba301.online_ticket_sales.config;

import com.sba301.online_ticket_sales.constant.PredefinedRole;
import com.sba301.online_ticket_sales.entity.*;
import com.sba301.online_ticket_sales.enums.*;
import com.sba301.online_ticket_sales.repository.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
  PersonRepository personRepository;
  CinemaRepository cinemaRepository;

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
      initializePersons();
      initializeMovies();
      initializeCinemas();
      seedMovieScreens(7);
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

        int screensToday = random.nextInt(3) + 3;
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
                    .status(MovieScreenStatus.ACTIVE)
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

  private void initializePersons() {
    log.info("Initializing persons...");

    if (personRepository.count() > 0) {
      log.info("Persons already exist, skipping initialization...");
      return;
    }

    // Lấy countries từ database
    Country vietnam = countryRepository.findByName("Việt Nam").orElse(null);
    Country usa = countryRepository.findByName("Mỹ").orElse(null);
    Country korea = countryRepository.findByName("Hàn Quốc").orElse(null);
    Country japan = countryRepository.findByName("Nhật Bản").orElse(null);
    Country china = countryRepository.findByName("Trung Quốc").orElse(null);

    // Danh sách đạo diễn
    List<PersonData> directors =
        List.of(
            new PersonData(
                "Christopher Nolan",
                "Đạo diễn nổi tiếng với các phim khoa học viễn tưởng",
                LocalDate.of(1970, 7, 30),
                1.81,
                usa,
                "Christopher Nolan là một đạo diễn, nhà sản xuất và biên kịch người Anh-Mỹ. Ông nổi tiếng với những bộ phim có cấu trúc phức tạp, chủ đề triết học và kỹ thuật quay phim đột phá."),
            new PersonData(
                "Quentin Tarantino",
                "Đạo diễn phim hành động và tội phạm",
                LocalDate.of(1963, 3, 27),
                1.85,
                usa,
                "Quentin Tarantino là đạo diễn, nhà biên kịch và diễn viên người Mỹ. Ông được biết đến với phong cách làm phim độc đáo, đối thoại sắc bén và việc sử dụng bạo lực một cách nghệ thuật."),
            new PersonData(
                "Bong Joon-ho",
                "Đạo diễn Hàn Quốc đoạt giải Oscar",
                LocalDate.of(1969, 9, 14),
                1.75,
                korea,
                "Bong Joon-ho là đạo diễn và biên kịch Hàn Quốc. Ông nổi tiếng với bộ phim 'Parasite' đoạt giải Oscar Phim hay nhất năm 2020."),
            new PersonData(
                "Akira Kurosawa",
                "Huyền thoại điện ảnh Nhật Bản",
                LocalDate.of(1910, 3, 23),
                1.70,
                japan,
                "Akira Kurosawa là một đạo diễn, nhà biên kịch và nhà sản xuất phim Nhật Bản. Ông được coi là một trong những đạo diễn có ảnh hưởng nhất trong lịch sử điện ảnh."),
            new PersonData(
                "Trần Anh Hùng",
                "Đạo diễn gốc Việt tại Pháp",
                LocalDate.of(1962, 12, 23),
                1.72,
                vietnam,
                "Trần Anh Hùng là đạo diễn gốc Việt sinh sống tại Pháp. Ông nổi tiếng với những bộ phim mang đậm chất thơ và văn hóa Việt Nam."));

    // Danh sách diễn viên
    List<PersonData> actors =
        List.of(
            new PersonData(
                "Leonardo DiCaprio",
                "Diễn viên Hollywood hạng A",
                LocalDate.of(1974, 11, 11),
                1.83,
                usa,
                "Leonardo DiCaprio là một diễn viên và nhà sản xuất phim người Mỹ. Ông đã giành được giải Oscar cho Nam diễn viên chính xuất sắc nhất cho vai diễn trong 'The Revenant'."),
            new PersonData(
                "Song Kang-ho",
                "Diễn viên hàng đầu Hàn Quốc",
                LocalDate.of(1967, 1, 17),
                1.80,
                korea,
                "Song Kang-ho là một diễn viên Hàn Quốc nổi tiếng. Ông được biết đến qua nhiều bộ phim của đạo diễn Bong Joon-ho như 'Parasite', 'The Host'."),
            new PersonData(
                "Toshiro Mifune",
                "Huyền thoại diễn viên Nhật Bản",
                LocalDate.of(1920, 4, 1),
                1.74,
                japan,
                "Toshiro Mifune là một diễn viên Nhật Bản nổi tiếng, thường xuyên hợp tác với đạo diễn Akira Kurosawa trong nhiều tác phẩm kinh điển."),
            new PersonData(
                "Châu Tinh Trì",
                "Vua hài Hong Kong",
                LocalDate.of(1962, 6, 22),
                1.74,
                china,
                "Châu Tinh Trì là diễn viên, đạo diễn và nhà sản xuất phim Hong Kong. Ông nổi tiếng với phong cách hài độc đáo và những bộ phim võ thuật hài hước."),
            new PersonData(
                "Ngô Thanh Vân",
                "Diễn viên hành động Việt Nam",
                LocalDate.of(1979, 2, 26),
                1.65,
                vietnam,
                "Ngô Thanh Vân là diễn viên, đạo diễn và nhà sản xuất phim Việt Nam. Cô được biết đến với những vai diễn hành động và là gương mặt đại diện của điện ảnh Việt Nam."));

    // Tạo và lưu directors
    for (PersonData data : directors) {
      Person person =
          Person.builder()
              .name(data.name)
              .description(data.description)
              .birthDate(data.birthDate)
              .height(data.height)
              .occupation(Occupation.DIRECTOR)
              .biography(data.biography)
              .country(data.country)
              .isDeleted(false)
              .build();
      personRepository.save(person);
      log.info("Created director: {}", data.name);
    }

    // Tạo và lưu actors
    for (PersonData data : actors) {
      Person person =
          Person.builder()
              .name(data.name)
              .description(data.description)
              .birthDate(data.birthDate)
              .height(data.height)
              .occupation(Occupation.ACTOR)
              .biography(data.biography)
              .country(data.country)
              .isDeleted(false)
              .build();
      personRepository.save(person);
      log.info("Created actor: {}", data.name);
    }

    log.info(
        "Persons initialization completed - Created {} directors and {} actors",
        directors.size(),
        actors.size());
  }

  // Helper class để lưu trữ dữ liệu person
  private static class PersonData {
    final String name;
    final String description;
    final LocalDate birthDate;
    final Double height;
    final Country country;
    final String biography;

    PersonData(
        String name,
        String description,
        LocalDate birthDate,
        Double height,
        Country country,
        String biography) {
      this.name = name;
      this.description = description;
      this.birthDate = birthDate;
      this.height = height;
      this.country = country;
      this.biography = biography;
    }
  }

  private void initializeMovies() {
    log.info("Initializing movies...");

    if (movieRepository.count() > 0) {
      log.info("Movies already exist, skipping initialization...");
      return;
    }

    // Lấy dữ liệu reference từ database
    List<Country> countries = countryRepository.findAll();
    List<Genre> genres = genreRepository.findAll();
    List<Person> directors = personRepository.findByOccupation(Occupation.DIRECTOR);
    List<Person> actors = personRepository.findByOccupation(Occupation.ACTOR);

    if (countries.isEmpty() || genres.isEmpty() || directors.isEmpty() || actors.isEmpty()) {
      log.warn("Missing reference data for movies initialization");
      return;
    }

    // Danh sách 10 bộ phim
    List<MovieData> moviesData =
        List.of(
            new MovieData(
                "Inception",
                "Một tác phẩm khoa học viễn tưởng về thế giới của những giấc mơ và thực tại đan xen.",
                148,
                LocalDate.of(2010, 7, 16),
                LocalDate.of(2010, 7, 20),
                LocalDate.of(2010, 12, 31),
                "https://example.com/inception.jpg",
                "https://example.com/inception-trailer.mp4",
                MovieStatus.ENDED,
                AgeRestriction.T13,
                List.of(MovieFormat.TWO_D, MovieFormat.IMAX),
                "Mỹ",
                List.of("Viễn tưởng", "Hành động"),
                List.of("Christopher Nolan"),
                List.of("Leonardo DiCaprio")),
            new MovieData(
                "Parasite",
                "Bộ phim đoạt giải Oscar về sự chênh lệch giai cấp trong xã hội Hàn Quốc.",
                132,
                LocalDate.of(2019, 5, 30),
                LocalDate.of(2019, 6, 15),
                LocalDate.of(2020, 3, 31),
                "https://example.com/parasite.jpg",
                "https://example.com/parasite-trailer.mp4",
                MovieStatus.ENDED,
                AgeRestriction.T16,
                List.of(MovieFormat.TWO_D),
                "Hàn Quốc",
                List.of("Tình cảm", "Hành động"),
                List.of("Bong Joon-ho"),
                List.of("Song Kang-ho")),
            new MovieData(
                "Seven Samurai",
                "Kiệt tác điện ảnh Nhật Bản về bảy samurai bảo vệ ngôi làng khỏi bọn cướp.",
                207,
                LocalDate.of(1954, 4, 26),
                LocalDate.of(1954, 5, 1),
                LocalDate.of(1954, 12, 31),
                "https://example.com/seven-samurai.jpg",
                "https://example.com/seven-samurai-trailer.mp4",
                MovieStatus.ENDED,
                AgeRestriction.T13,
                List.of(MovieFormat.TWO_D),
                "Nhật Bản",
                List.of("Hành động"),
                List.of("Akira Kurosawa"),
                List.of("Toshiro Mifune")),
            new MovieData(
                "Kung Fu Hustle",
                "Phim hài võ thuật kinh điển của Châu Tinh Trì với những pha hành động hài hước.",
                99,
                LocalDate.of(2004, 12, 23),
                LocalDate.of(2005, 1, 15),
                LocalDate.of(2005, 6, 30),
                "https://example.com/kung-fu-hustle.jpg",
                "https://example.com/kung-fu-hustle-trailer.mp4",
                MovieStatus.ENDED,
                AgeRestriction.T13,
                List.of(MovieFormat.TWO_D),
                "Trung Quốc",
                List.of("Hài", "Hành động"),
                List.of("Châu Tinh Trì"),
                List.of("Châu Tinh Trì")),
            new MovieData(
                "Furie",
                "Bộ phim hành động Việt Nam với sự tham gia của Ngô Thanh Vân.",
                98,
                LocalDate.of(2019, 2, 22),
                LocalDate.of(2019, 3, 1),
                LocalDate.of(2019, 8, 31),
                "https://example.com/furie.jpg",
                "https://example.com/furie-trailer.mp4",
                MovieStatus.ENDED,
                AgeRestriction.T16,
                List.of(MovieFormat.TWO_D),
                "Việt Nam",
                List.of("Hành động"),
                List.of("Trần Anh Hùng"),
                List.of("Ngô Thanh Vân")),
            new MovieData(
                "Avengers: Endgame",
                "Trận chiến cuối cùng của các siêu anh hùng Marvel chống lại Thanos.",
                181,
                LocalDate.of(2019, 4, 26),
                LocalDate.of(2019, 4, 26),
                LocalDate.of(2019, 12, 31),
                "https://example.com/avengers-endgame.jpg",
                "https://example.com/avengers-endgame-trailer.mp4",
                MovieStatus.ENDED,
                AgeRestriction.T13,
                List.of(MovieFormat.TWO_D, MovieFormat.THREE_D, MovieFormat.IMAX),
                "Mỹ",
                List.of("Hành động", "Viễn tưởng"),
                List.of("Christopher Nolan"),
                List.of("Leonardo DiCaprio")),
            new MovieData(
                "Spirited Away",
                "Tác phẩm hoạt hình kinh điển của Studio Ghibli về cuộc phiêu lưu của cô bé Chihiro.",
                125,
                LocalDate.of(2001, 7, 20),
                LocalDate.of(2001, 8, 1),
                LocalDate.of(2002, 3, 31),
                "https://example.com/spirited-away.jpg",
                "https://example.com/spirited-away-trailer.mp4",
                MovieStatus.ENDED,
                AgeRestriction.T0,
                List.of(MovieFormat.TWO_D),
                "Nhật Bản",
                List.of("Hoạt hình"),
                List.of("Akira Kurosawa"),
                List.of("Toshiro Mifune")),
            new MovieData(
                "The Dark Knight",
                "Batman đối đầu với Joker trong tác phẩm siêu anh hùng kinh điển.",
                152,
                LocalDate.of(2008, 7, 18),
                LocalDate.of(2008, 7, 25),
                LocalDate.of(2009, 1, 31),
                "https://example.com/dark-knight.jpg",
                "https://example.com/dark-knight-trailer.mp4",
                MovieStatus.ENDED,
                AgeRestriction.T16,
                List.of(MovieFormat.TWO_D, MovieFormat.IMAX),
                "Mỹ",
                List.of("Hành động"),
                List.of("Christopher Nolan"),
                List.of("Leonardo DiCaprio")),
            new MovieData(
                "Your Name",
                "Câu chuyện tình yêu siêu nhiên giữa hai thanh niên hoán đổi cơ thể.",
                106,
                LocalDate.of(2016, 8, 26),
                LocalDate.of(2016, 9, 10),
                LocalDate.of(2017, 3, 31),
                "https://example.com/your-name.jpg",
                "https://example.com/your-name-trailer.mp4",
                MovieStatus.ENDED,
                AgeRestriction.T13,
                List.of(MovieFormat.TWO_D),
                "Nhật Bản",
                List.of("Hoạt hình", "Tình cảm"),
                List.of("Akira Kurosawa"),
                List.of("Toshiro Mifune")),
            new MovieData(
                "Dune: Part Two",
                "Phần tiếp theo của sử thi khoa học viễn tưởng về hành tinh Arrakis.",
                166,
                LocalDate.of(2024, 3, 1),
                LocalDate.of(2024, 3, 15),
                LocalDate.of(2024, 8, 31),
                "https://example.com/dune-part-two.jpg",
                "https://example.com/dune-part-two-trailer.mp4",
                MovieStatus.NOW_SHOWING,
                AgeRestriction.T13,
                List.of(MovieFormat.TWO_D, MovieFormat.IMAX, MovieFormat.DOLBY_ATMOS),
                "Mỹ",
                List.of("Viễn tưởng", "Hành động"),
                List.of("Christopher Nolan"),
                List.of("Leonardo DiCaprio")));

    // Tạo và lưu movies
    for (MovieData data : moviesData) {
      try {
        Movie movie = createMovieFromData(data, countries, genres, directors, actors);
        movieRepository.save(movie);
        log.info("Created movie: {}", data.title);
      } catch (Exception e) {
        log.error("Failed to create movie: {} - Error: {}", data.title, e.getMessage());
      }
    }

    log.info("Movies initialization completed - Created {} movies", moviesData.size());
  }

  private Movie createMovieFromData(
      MovieData data,
      List<Country> countries,
      List<Genre> genres,
      List<Person> directors,
      List<Person> actors) {

    // Tìm country
    Country country =
        countries.stream()
            .filter(c -> c.getName().equals(data.countryName))
            .findFirst()
            .orElse(countries.get(0)); // Fallback to first country

    // Tìm genres
    List<Genre> movieGenres =
        data.genreNames.stream()
            .map(
                genreName ->
                    genres.stream()
                        .filter(g -> g.getName().equals(genreName))
                        .findFirst()
                        .orElse(null))
            .filter(g -> g != null)
            .toList();

    // Tìm directors
    List<Person> movieDirectors =
        data.directorNames.stream()
            .map(
                directorName ->
                    directors.stream()
                        .filter(d -> d.getName().equals(directorName))
                        .findFirst()
                        .orElse(directors.get(0))) // Fallback to first director
            .toList();

    // Tìm actors
    List<Person> movieActors =
        data.actorNames.stream()
            .map(
                actorName ->
                    actors.stream()
                        .filter(a -> a.getName().equals(actorName))
                        .findFirst()
                        .orElse(actors.get(0))) // Fallback to first actor
            .toList();

    return Movie.builder()
        .title(data.title)
        .description(data.description)
        .duration(data.duration)
        .releaseDate(data.releaseDate)
        .premiereDate(data.premiereDate)
        .endDate(data.endDate)
        .thumbnailUrl(data.thumbnailUrl)
        .trailerUrl(data.trailerUrl)
        .movieStatus(data.movieStatus)
        .ageRestriction(data.ageRestriction)
        .availableFormats(new ArrayList<>(data.availableFormats))
        .isDeleted(false)
        .isPublished(true)
        .country(country)
        .genres(new ArrayList<>(movieGenres))
        .directors(new ArrayList<>(movieDirectors))
        .actors(new ArrayList<>(movieActors))
        .build();
  }

  // Helper class để lưu trữ dữ liệu movie
  private static class MovieData {
    final String title;
    final String description;
    final Integer duration;
    final LocalDate releaseDate;
    final LocalDate premiereDate;
    final LocalDate endDate;
    final String thumbnailUrl;
    final String trailerUrl;
    final MovieStatus movieStatus;
    final AgeRestriction ageRestriction;
    final List<MovieFormat> availableFormats;
    final String countryName;
    final List<String> genreNames;
    final List<String> directorNames;
    final List<String> actorNames;

    MovieData(
        String title,
        String description,
        Integer duration,
        LocalDate releaseDate,
        LocalDate premiereDate,
        LocalDate endDate,
        String thumbnailUrl,
        String trailerUrl,
        MovieStatus movieStatus,
        AgeRestriction ageRestriction,
        List<MovieFormat> availableFormats,
        String countryName,
        List<String> genreNames,
        List<String> directorNames,
        List<String> actorNames) {
      this.title = title;
      this.description = description;
      this.duration = duration;
      this.releaseDate = releaseDate;
      this.premiereDate = premiereDate;
      this.endDate = endDate;
      this.thumbnailUrl = thumbnailUrl;
      this.trailerUrl = trailerUrl;
      this.movieStatus = movieStatus;
      this.ageRestriction = ageRestriction;
      this.availableFormats = availableFormats;
      this.countryName = countryName;
      this.genreNames = genreNames;
      this.directorNames = directorNames;
      this.actorNames = actorNames;
    }
  }

  private void initializeCinemas() {
    log.info("Initializing cinemas...");

    if (cinemaRepository.count() > 0) {
      log.info("Cinemas already exist, skipping initialization...");
      return;
    }

    // Danh sách 5 rạp chiếu phim
    List<CinemaData> cinemasData =
        List.of(
            new CinemaData(
                "CGV Vincom Đồng Khởi",
                "Tầng 3, Vincom Center Đồng Khởi, 72 Lê Thánh Tôn, Quận 1, TP.HCM",
                "0283822345",
                "Quận 1",
                List.of(
                    new RoomData("P01", RoomType.STANDARD),
                    new RoomData("P02", RoomType.STANDARD),
                    new RoomData("P03", RoomType.VIP),
                    new RoomData("P04", RoomType.IMAX),
                    new RoomData("P05", RoomType.STANDARD))),
            new CinemaData(
                "Lotte Cinema Landmark 81",
                "Tầng 5, Vincom Landmark 81, 720A Điện Biên Phủ, Quận 1, TP.HCM",
                "0283933456",
                "Quận 1",
                List.of(
                    new RoomData("L01", RoomType.STANDARD),
                    new RoomData("L02", RoomType.VIP),
                    new RoomData("L04", RoomType.STANDARD),
                    new RoomData("L05", RoomType.IMAX))),
            new CinemaData(
                "Galaxy Cinema Nguyễn Du",
                "116 Nguyễn Du, Quận 1, TP.HCM",
                "0283844567",
                "Quận 1",
                List.of(
                    new RoomData("G01", RoomType.STANDARD),
                    new RoomData("G02", RoomType.STANDARD),
                    new RoomData("G03", RoomType.VIP))),
            new CinemaData(
                "CGV Vincom Thủ Đức",
                "Tầng 4, Vincom Plaza Thủ Đức, 216 Võ Văn Ngân, Thủ Đức, TP.HCM",
                "0283955678",
                "Thủ Đức",
                List.of(
                    new RoomData("T01", RoomType.STANDARD),
                    new RoomData("T02", RoomType.STANDARD),
                    new RoomData("T03", RoomType.VIP),
                    new RoomData("T04", RoomType.STANDARD),
                    new RoomData("T06", RoomType.IMAX))),
            new CinemaData(
                "Lotte Cinema Gò Vấp",
                "Tầng 6, Lotte Mart Gò Vấp, 242 Nguyễn Văn Lượng, Gò Vấp, TP.HCM",
                "0283866789",
                "Gò Vấp",
                List.of(
                    new RoomData("GV1", RoomType.STANDARD),
                    new RoomData("GV2", RoomType.STANDARD),
                    new RoomData("GV3", RoomType.VIP),
                    new RoomData("GV5", RoomType.STANDARD))));

    // Tạo và lưu cinemas với rooms
    for (CinemaData data : cinemasData) {
      try {
        Cinema cinema = createCinemaFromData(data);
        cinemaRepository.save(cinema);
        log.info("Created cinema: {} with {} rooms", data.name, data.rooms.size());
      } catch (Exception e) {
        log.error("Failed to create cinema: {} - Error: {}", data.name, e.getMessage());
      }
    }

    log.info("Cinemas initialization completed - Created {} cinemas", cinemasData.size());
  }

  private Cinema createCinemaFromData(CinemaData data) {
    Cinema cinema =
        Cinema.builder()
            .name(data.name)
            .address(data.address)
            .hotline(data.hotline)
            .province(data.province)
            .isActive(true)
            .build();

    // Tạo rooms cho cinema
    for (RoomData roomData : data.rooms) {
      Room room =
          Room.builder().name(roomData.name).roomType(roomData.roomType).isActive(true).build();

      cinema.addRoom(room); // Sử dụng method addRoom để set relationship
    }

    return cinema;
  }

  // Helper classes để lưu trữ dữ liệu
  private static class CinemaData {
    final String name;
    final String address;
    final String hotline;
    final String province;
    final List<RoomData> rooms;

    CinemaData(String name, String address, String hotline, String province, List<RoomData> rooms) {
      this.name = name;
      this.address = address;
      this.hotline = hotline;
      this.province = province;
      this.rooms = rooms;
    }
  }

  private static class RoomData {
    final String name;
    final RoomType roomType;

    RoomData(String name, RoomType roomType) {
      this.name = name;
      this.roomType = roomType;
    }
  }
}
