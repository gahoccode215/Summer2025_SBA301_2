package com.sba301.online_ticket_sales.dto.review.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateReviewRequest {

    @NotNull(message = "ID review không được để trống")
    private String id;

    @NotNull(message = "Rating không được để trống")
    @Min(value = 1, message = "Rating phải từ 1 đến 5")
    @Max(value = 5, message = "Rating phải từ 1 đến 5")
    private Integer rating;

    @Size(max = 1000, message = "Bình luận không được vượt quá 1000 ký tự")
    private String comment;
}
