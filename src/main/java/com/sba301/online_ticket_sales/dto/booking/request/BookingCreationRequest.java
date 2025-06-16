package com.sba301.online_ticket_sales.dto.booking.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreationRequest {
    @NotEmpty(message = "Danh sách vé không được để trống")
    private List<TicketRequest> tickets;
}
