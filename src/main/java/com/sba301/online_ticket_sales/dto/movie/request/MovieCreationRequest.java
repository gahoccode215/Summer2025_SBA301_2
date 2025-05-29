package com.sba301.online_ticket_sales.dto.movie.request;


import com.sba301.online_ticket_sales.enums.MovieStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class MovieCreationRequest {
    private String title;
    private String description;
    private Integer duration;
    private LocalDate releaseDate;
    private String trailerUrl;
    private MovieStatus movieStatus;
    private Integer countryId;
    private List<Integer> genreIds;
    private List<Integer> directorIds;
    private List<Integer> actorIds;
}
