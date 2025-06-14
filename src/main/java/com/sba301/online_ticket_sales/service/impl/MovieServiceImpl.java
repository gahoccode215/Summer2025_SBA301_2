package com.sba301.online_ticket_sales.service.impl;

import com.sba301.online_ticket_sales.constant.PredefinedRole;
import com.sba301.online_ticket_sales.dto.movie.request.MovieCreationRequest;
import com.sba301.online_ticket_sales.dto.movie.request.MovieUpdateRequest;
import com.sba301.online_ticket_sales.dto.movie.response.MovieResponse;
import com.sba301.online_ticket_sales.entity.Movie;
import com.sba301.online_ticket_sales.enums.ErrorCode;
import com.sba301.online_ticket_sales.enums.MovieFormat;
import com.sba301.online_ticket_sales.enums.MovieStatus;
import com.sba301.online_ticket_sales.exception.AppException;
import com.sba301.online_ticket_sales.mapper.MovieMapper;
import com.sba301.online_ticket_sales.repository.MovieRepository;
import com.sba301.online_ticket_sales.service.MovieService;
import jakarta.persistence.criteria.Predicate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieServiceImpl implements MovieService {

    MovieRepository movieRepository;
    MovieMapper movieMapper;

    @Override
    @Transactional
    public MovieResponse createMovie(MovieCreationRequest request) {
        validateMovieCreation(request);
        return movieMapper.toMovieResponse(movieRepository.save(movieMapper.toMovie(request)));
    }

    @Override
    @Transactional
    public MovieResponse updateMovie(Long id, MovieUpdateRequest request) {

        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));

        validateMovieUpdate(movie, request);

        movieMapper.updateMovieFromRequest(request, movie);

        Movie updatedMovie = movieRepository.save(movie);

        return movieMapper.toMovieResponse(updatedMovie);
    }

    @Override
    @Transactional
    public void deleteMovie(Long id) {

        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));

        if (Boolean.TRUE.equals(movie.getIsDeleted())) {
            throw new AppException(ErrorCode.MOVIE_ALREADY_DELETED);
        }

        validateMovieDeletion(movie);

        performSoftDelete(movie);

        movieRepository.save(movie);
        log.info("Movie soft deleted successfully: {} (ID: {})", movie.getTitle(), id);
    }

    @Override
    public MovieResponse getMovieDetail(Long id) {

        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));

        if (Boolean.TRUE.equals(movie.getIsDeleted())) {
            throw new AppException(ErrorCode.MOVIE_NOT_FOUND);
        }

        // Kiểm tra movie có được publish không (chỉ admin/manager mới xem được unpublished)
        if (!Boolean.TRUE.equals(movie.getIsPublished()) && !hasAdminAccess()) {
            throw new AppException(ErrorCode.MOVIE_NOT_FOUND);
        }

        return movieMapper.toMovieResponse(movie);
    }

    @Override
    public Page<MovieResponse> getAllMovies(
            Pageable pageable, String keyword, MovieStatus movieStatus) {

        Specification<Movie> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Only fetch non-deleted movies
            predicates.add(cb.equal(root.get("isDeleted"), false));

            // Only fetch published movies (trừ khi là admin)
            if (!hasAdminAccess()) {
                predicates.add(cb.equal(root.get("isPublished"), true));
            }

            // Search by title (cải tiến search)
            if (keyword != null && !keyword.isBlank()) {
                String searchPattern = "%" + keyword.toLowerCase().trim() + "%";
                Predicate titlePredicate = cb.like(cb.lower(root.get("title")), searchPattern);
                Predicate descriptionPredicate = cb.like(cb.lower(root.get("description")), searchPattern);

                // Search trong cả title và description
                predicates.add(cb.or(titlePredicate, descriptionPredicate));
            }

            // Filter by movieStatus
            if (movieStatus != null) {
                predicates.add(cb.equal(root.get("movieStatus"), movieStatus));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Movie> movies = movieRepository.findAll(spec, pageable);

        return movies.map(movieMapper::toMovieResponse);
    }

    private void validateMovieCreation(MovieCreationRequest request) {
        // Kiểm tra title đã tồn tại chưa
        if (movieRepository.existsByTitleIgnoreCase(request.getTitle())) {
            throw new AppException(ErrorCode.MOVIE_TITLE_ALREADY_EXISTS);
        }

        // Validate premiere date logic
        if (request.getPremiereDate() != null && request.getReleaseDate() != null) {
            if (request.getPremiereDate().isBefore(request.getReleaseDate())) {
                throw new AppException(ErrorCode.INVALID_PREMIERE_DATE);
            }
        }

        // Validate end date logic
        if (request.getEndDate() != null && request.getPremiereDate() != null) {
            if (request.getEndDate().isBefore(request.getPremiereDate())) {
                throw new AppException(ErrorCode.INVALID_END_DATE);
            }
        }
    }

    private void validateMovieUpdate(Movie existingMovie, MovieUpdateRequest request) {
        if (request.getTitle() != null && !request.getTitle().equals(existingMovie.getTitle())) {
            if (movieRepository.existsByTitleIgnoreCaseAndIdNot(request.getTitle(), existingMovie.getId())) {
                throw new AppException(ErrorCode.MOVIE_TITLE_ALREADY_EXISTS);
            }
        }

        LocalDate releaseDate = request.getReleaseDate() != null ? request.getReleaseDate() : existingMovie.getReleaseDate();
        LocalDate premiereDate = request.getPremiereDate() != null ? request.getPremiereDate() : existingMovie.getPremiereDate();
        LocalDate endDate = request.getEndDate() != null ? request.getEndDate() : existingMovie.getEndDate();

        if (premiereDate != null && releaseDate != null) {
            if (premiereDate.isBefore(releaseDate)) {
                throw new AppException(ErrorCode.INVALID_PREMIERE_DATE);
            }
        }

        if (endDate != null && premiereDate != null) {
            if (endDate.isBefore(premiereDate)) {
                throw new AppException(ErrorCode.INVALID_END_DATE);
            }
        }

        MovieStatus movieStatus = request.getMovieStatus() != null ? request.getMovieStatus() : existingMovie.getMovieStatus();
        List<MovieFormat> availableFormats = request.getAvailableFormats() != null ? request.getAvailableFormats() : existingMovie.getAvailableFormats();

        if (movieStatus == MovieStatus.IMAX) {
            if (availableFormats == null ||
                    (!availableFormats.contains(MovieFormat.IMAX) &&
                            !availableFormats.contains(MovieFormat.IMAX_3D))) {
                throw new AppException(ErrorCode.MOVIE_MISSING_REQUIRED_FORMAT);
            }
        }

        // Kiểm tra không thể xóa phim đang có lịch chiếu
        if (Boolean.TRUE.equals(request.getIsDeleted()) && !existingMovie.getIsDeleted()) {
            // TODO: Kiểm tra có showtimes active không
            // if (showtimeService.hasActiveShowtimes(existingMovie.getId())) {
            //     throw new AppException(ErrorCode.MOVIE_CANNOT_DELETE_ACTIVE);
            // }
        }
    }

    private void validateMovieDeletion(Movie movie) {
        // Kiểm tra phim có lịch chiếu đang hoạt động không
        // TODO: Uncomment khi có ShowtimeService
        // if (showtimeService.hasActiveShowtimes(movie.getId())) {
        //     throw new AppException(ErrorCode.MOVIE_CANNOT_DELETE_ACTIVE);
        // }

        // Kiểm tra phim có vé đã bán không
        // TODO: Uncomment khi có TicketService
        // if (ticketService.hasSoldTickets(movie.getId())) {
        //     throw new AppException(ErrorCode.MOVIE_HAS_SOLD_TICKETS);
        // }

        // Kiểm tra phim đang trong trạng thái đặc biệt
        if (movie.getMovieStatus() == MovieStatus.NOW_SHOWING) {
            log.warn("Attempting to delete movie that is currently showing: {}", movie.getTitle());
            // Có thể cho phép hoặc không cho phép tùy business rule
        }
    }

    private void performSoftDelete(Movie movie) {
        movie.setIsDeleted(true);
        movie.setIsPublished(false);
        movie.getGenres().clear();
        movie.getDirectors().clear();
        movie.getActors().clear();
        movie.setCountry(null);
    }
    private boolean hasAdminAccess() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals(PredefinedRole.ADMIN_ROLE) ||
                                authority.getAuthority().equals(PredefinedRole.MANAGER_ROLE));
    }
}
