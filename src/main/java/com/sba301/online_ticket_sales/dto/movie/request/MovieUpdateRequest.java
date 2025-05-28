package com.sba301.online_ticket_sales.dto.movie.request;

import com.sba301.online_ticket_sales.enums.MovieStatus;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class MovieUpdateRequest {
    private String title;
    private String description;
    private Integer duration;
    private LocalDate releaseDate;
    private String trailerUrl;
    private String director;
    private MovieStatus movieStatus;
    private Long countryId;
    private List<Long> genreIds;
    private List<Long> directorIds;
    private List<Long> actorIds;
}
