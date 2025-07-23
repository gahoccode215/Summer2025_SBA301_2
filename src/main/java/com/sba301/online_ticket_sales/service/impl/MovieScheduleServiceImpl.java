package com.sba301.online_ticket_sales.service.impl;

import com.sba301.online_ticket_sales.dto.moviescreen.request.UpsertMovieScreenRequest;
import com.sba301.online_ticket_sales.dto.moviescreen.response.*;
import com.sba301.online_ticket_sales.entity.Movie;
import com.sba301.online_ticket_sales.entity.MovieScreen;
import com.sba301.online_ticket_sales.entity.Room;
import com.sba301.online_ticket_sales.entity.User;
import com.sba301.online_ticket_sales.enums.ErrorCode;
import com.sba301.online_ticket_sales.enums.MovieScreenStatus;
import com.sba301.online_ticket_sales.enums.RoomType;
import com.sba301.online_ticket_sales.exception.AppException;
import com.sba301.online_ticket_sales.repository.MovieRepository;
import com.sba301.online_ticket_sales.repository.MovieScreenRepository;
import com.sba301.online_ticket_sales.repository.RoomRepository;
import com.sba301.online_ticket_sales.repository.TicketOrderRepository;
import com.sba301.online_ticket_sales.service.MovieScheduleService;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MovieScheduleServiceImpl implements MovieScheduleService {
  private final MovieRepository movieRepository;
  private final RoomRepository roomRepository;
  private final MovieScreenRepository movieScreenRepository;
  private final TicketOrderRepository ticketOrderRepository;

  private static final int BREAK_TIME_MINUTES = 10;

  @Override
  @Transactional
  public void createMovieSchedule(UpsertMovieScreenRequest request) {
    var authentication =
        (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info("User: {}", authentication.getUsername());

    List<String> roleNames =
        authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
    boolean isAdmin =
        roleNames.contains("ADMIN")
            || roleNames.contains("ROLE_ADMIN")
            || roleNames.contains("MANAGER")
            || roleNames.contains("ROLE_MANAGER");
    if (!isAdmin) throw new AppException(ErrorCode.SCHEDULE_NO_PERMISSION);

    Movie movie =
        movieRepository
            .findById(request.getMovieId())
            .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));

    Room room =
        roomRepository
            .findById(request.getRoomId())
            .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

    if (room.isActive() && !movie.getIsDeleted()) {
      log.info("Room and movie are active, proceeding with schedule creation");
    } else {
      log.error("Room or movie is not active, cannot create schedule");
      throw new AppException(ErrorCode.ROOM_OR_MOVIE_NOT_ACTIVE);
    }

    LocalDateTime endTime =
        request.getShowtime().plusMinutes(movie.getDuration() + BREAK_TIME_MINUTES);

    int hasConflict =
        movieScreenRepository.hasConflictSchedule(room.getId(), request.getShowtime(), endTime);

    if (hasConflict > 0) {
      log.error("Schedule has conflict with another schedule");
      throw new AppException(ErrorCode.SCHEDULE_ALREADY_EXISTS);
    }

    MovieScreen movieScreen =
        MovieScreen.builder()
            .movie(movie)
            .room(room)
            .ticketPrice(request.getTicketPrice())
            .status(MovieScreenStatus.ACTIVE)
            .showtime(request.getShowtime())
            .build();
    movieScreen.setCreatedBy(authentication.getId());
    movieScreenRepository.save(movieScreen);
    log.info("Created movie schedule with ID: {}", movieScreen.getId());
  }

  @Override
  @Transactional
  public void updateMovieSchedule(Long id, UpsertMovieScreenRequest request) {
    var authentication =
        (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info("User: {}", authentication.getUsername());
    List<String> roleNames =
        authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
    boolean isAdmin =
        roleNames.contains("ADMIN")
            || roleNames.contains("ROLE_ADMIN")
            || roleNames.contains("MANAGER")
            || roleNames.contains("ROLE_MANAGER");
    if (!isAdmin) throw new AppException(ErrorCode.SCHEDULE_NO_PERMISSION);

    Movie movie =
        movieRepository
            .findById(request.getMovieId())
            .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));

    Room room =
        roomRepository
            .findById(request.getRoomId())
            .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

    MovieScreen movieScreen =
        movieScreenRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));

    LocalDateTime endTime =
        request.getShowtime().plusMinutes(movie.getDuration() + BREAK_TIME_MINUTES);
    int hasConflict =
        movieScreenRepository.hasConflictScheduleExcludingId(
            id, room.getId(), request.getShowtime(), endTime);

    if (hasConflict > 0) {
      log.error("Schedule has conflict with another schedule");
      throw new AppException(ErrorCode.SCHEDULE_ALREADY_EXISTS);
    }
    movieScreen.setUpdatedBy(authentication.getId());
    movieScreen.setMovie(movie);
    movieScreen.setTicketPrice(request.getTicketPrice());
    movieScreen.setRoom(room);
    movieScreen.setShowtime(request.getShowtime());
    movieScreenRepository.save(movieScreen);
    log.info("Movie screen schedule updated successfully for movie ID: {}", request.getMovieId());
  }

  @Override
  @Transactional
  public void activateMovieSchedule(Long id, MovieScreenStatus status) {
    var authentication =
        (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info("User: {}", authentication.getUsername());
    List<String> roleNames =
        authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
    boolean isAdmin =
        roleNames.contains("ADMIN")
            || roleNames.contains("ROLE_ADMIN")
            || roleNames.contains("MANAGER")
            || roleNames.contains("ROLE_MANAGER");
    if (!isAdmin) throw new AppException(ErrorCode.SCHEDULE_NO_PERMISSION);

    MovieScreen movieScreen =
        movieScreenRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));
    if (status != MovieScreenStatus.ACTIVE) {
      List<String> bookedSeats = ticketOrderRepository.findSeatCodesByShowtimeId(id);
      if (!bookedSeats.isEmpty()) {
        throw new AppException(ErrorCode.SCHEDULE_ALREADY_HAS_BOOKINGS);
      }
    }
    movieScreen.setStatus(status);
    movieScreen.setUpdatedBy(authentication.getId());
    movieScreenRepository.save(movieScreen);
    log.info("Movie screen schedule with ID: {} has been updated to status: {}", id, status);
  }

  @Override
  public List<CinemaShowTimeResponse> getMovieShowTimes(Long movieId, LocalDateTime date) {
    log.info("Fetching show times for movie ID: {} on date: {}", movieId, date);

    List<CinemaShowtimeDTO> showTimes;

    if (date != null) {
      LocalDate queryDate = date.toLocalDate();
      LocalDateTime startOfDay = queryDate.atStartOfDay();
      LocalDateTime endOfDay = queryDate.atTime(LocalTime.MAX);

      showTimes = movieScreenRepository.findShowTimesByMovie(movieId, startOfDay, endOfDay);
    } else {
      showTimes = movieScreenRepository.findAllShowTimesByMovie(movieId);
    }

    if (showTimes.isEmpty()) {
      log.info("No show times found for movie ID: {}", movieId);
      return List.of();
    }

    Map<Long, List<ShowTimeResponse>> showTimeMap = new LinkedHashMap<>();
    Map<Long, CinemaShowTimeResponse> cinemaInfoMap = new HashMap<>();

    for (CinemaShowtimeDTO dto : showTimes) {
      ShowTimeResponse showTime =
          ShowTimeResponse.builder()
              .showTimeId(dto.getShowTimeId())
              .roomId(dto.getRoomId())
              .showTime(dto.getShowTime())
              .roomType(RoomType.valueOf(dto.getRoomType()))
              .status(dto.getStatus())
              .build();

      showTimeMap.computeIfAbsent(dto.getCinemaId(), id -> new ArrayList<>()).add(showTime);

      cinemaInfoMap.putIfAbsent(
          dto.getCinemaId(),
          CinemaShowTimeResponse.builder()
              .cinemaId(dto.getCinemaId())
              .cinemaName(dto.getCinemaName())
              .cinemaAddress(dto.getCinemaAddress())
              .showTimes(null)
              .build());
    }

    List<CinemaShowTimeResponse> result =
        showTimeMap.entrySet().stream()
            .map(
                entry -> {
                  CinemaShowTimeResponse base = cinemaInfoMap.get(entry.getKey());
                  return CinemaShowTimeResponse.builder()
                      .cinemaId(base.getCinemaId())
                      .cinemaName(base.getCinemaName())
                      .cinemaAddress(base.getCinemaAddress())
                      .showTimes(entry.getValue())
                      .build();
                })
            .toList();
    return result;
  }

  private LocalDateTime resolveQueryTime(LocalDateTime inputDateTime) {
    LocalDateTime now = LocalDateTime.now();
    if (inputDateTime == null || inputDateTime.toLocalDate().isEqual(now.toLocalDate())) {
      return now;
    } else {
      return inputDateTime.toLocalDate().atStartOfDay();
    }
  }

  @Override
  public List<MovieShowTimeResponse> getMovieShowTimesByCinema(Long cinemaId, LocalDateTime date) {
    log.info("Fetching movie show times for cinema ID: {} on date: {}", cinemaId, date);
    List<MovieShowtimeDTO> showTimes;

    if (date != null) {
      LocalDateTime queryTime = date.toLocalDate().atStartOfDay();
      LocalDateTime upperBound = queryTime.plusDays(1).minusSeconds(1);

      showTimes = movieScreenRepository.findByCinemaAndDate(cinemaId, queryTime, upperBound);
    } else {
      showTimes = movieScreenRepository.findAllByCinema(cinemaId);
    }

    if (showTimes.isEmpty()) {
      log.info("No show times found for cinema ID: {}", cinemaId);
      return List.of();
    }

    Map<Long, MovieShowTimeResponse.MovieShowTimeResponseBuilder> movieMap = new LinkedHashMap<>();

    Map<Long, List<ShowTimeResponse>> showTimeMap = new LinkedHashMap<>();
    Map<Long, MovieShowTimeResponse> movieInfoMap = new HashMap<>();

    for (MovieShowtimeDTO dto : showTimes) {
      ShowTimeResponse showTime =
          ShowTimeResponse.builder()
              .showTimeId(dto.getShowTimeId())
              .roomId(dto.getRoomId())
              .roomType(RoomType.valueOf(dto.getRoomType()))
              .showTime(dto.getShowTime())
              .status(dto.getStatus())
              .build();

      showTimeMap.computeIfAbsent(dto.getMovieId(), id -> new ArrayList<>()).add(showTime);

      movieInfoMap.putIfAbsent(
          dto.getMovieId(),
          MovieShowTimeResponse.builder()
              .movieId(dto.getMovieId())
              .movieName(dto.getMovieName())
              .moviePosterUrl(dto.getMoviePosterUrl())
              .movieDuration(dto.getMovieDuration())
              .movieRating(dto.getMovieRating())
              .movieReleaseDate(dto.getMovieReleaseDate())
              .showTimes(null)
              .build());
    }

    List<MovieShowTimeResponse> result =
        showTimeMap.entrySet().stream()
            .map(
                entry -> {
                  MovieShowTimeResponse base = movieInfoMap.get(entry.getKey());
                  return MovieShowTimeResponse.builder()
                      .movieId(base.getMovieId())
                      .movieName(base.getMovieName())
                      .moviePosterUrl(base.getMoviePosterUrl())
                      .movieDuration(base.getMovieDuration())
                      .movieRating(base.getMovieRating())
                      .movieReleaseDate(base.getMovieReleaseDate())
                      .showTimes(entry.getValue())
                      .build();
                })
            .toList();
    log.info("Found {} movie show times for cinema ID: {}", result.size(), cinemaId);
    return result;
  }
}
