package com.sba301.online_ticket_sales.dto.genre.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenreCreationRequest {
    @NotBlank(message = "Name is required")
    private String name;
}
