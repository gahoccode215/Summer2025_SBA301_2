package com.sba301.online_ticket_sales.dto.movie.request;


import com.sba301.online_ticket_sales.enums.MovieStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieCreationRequest {
    String title;
    String description;
    Integer duration;
    LocalDate releaseDate;
    String trailerUrl;
    String director;
    MovieStatus movieStatus;
}
