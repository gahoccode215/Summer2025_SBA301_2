package com.sba301.online_ticket_sales.dto.dashboard;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Data
@Builder
public class RecentBooking {
    private String ticketCode;
    private String customerName;
    private String movieTitle;
    private String cinemaName;
    private LocalDateTime bookingTime;
    private BigDecimal totalAmount;
    private String paymentStatus;
}
