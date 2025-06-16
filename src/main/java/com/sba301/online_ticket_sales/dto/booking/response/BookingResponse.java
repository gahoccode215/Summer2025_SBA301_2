package com.sba301.online_ticket_sales.dto.booking.response;
import com.sba301.online_ticket_sales.enums.BookingStatus;
import com.sba301.online_ticket_sales.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;

    private String bookingCode;

    private Long userId;

    private String username;

    private Integer totalTickets;

    private BigDecimal totalAmount;

    private BookingStatus bookingStatus;

    private PaymentStatus paymentStatus;

    private String vnpayTransactionId;

    private String paymentUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<BookingDetailResponse> bookingDetails;
}
