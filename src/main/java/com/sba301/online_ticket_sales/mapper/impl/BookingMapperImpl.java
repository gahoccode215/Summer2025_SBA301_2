package com.sba301.online_ticket_sales.mapper.impl;

import com.sba301.online_ticket_sales.dto.booking.response.BookingDetailResponse;
import com.sba301.online_ticket_sales.dto.booking.response.BookingResponse;
import com.sba301.online_ticket_sales.dto.moviescreen.response.MovieScreenResponse;
import com.sba301.online_ticket_sales.entity.Booking;
import com.sba301.online_ticket_sales.entity.BookingDetail;
import com.sba301.online_ticket_sales.entity.MovieScreen;
import com.sba301.online_ticket_sales.mapper.BookingMapper;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Component
public class BookingMapperImpl implements BookingMapper {

    @Override
    public BookingResponse toResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .bookingCode(booking.getBookingCode())
                .userId(booking.getUser().getId())
                .username(booking.getUser().getUsername())
                .totalTickets(booking.getTotalTickets())
                .totalAmount(booking.getTotalAmount())
                .bookingStatus(booking.getBookingStatus())
                .paymentStatus(booking.getPaymentStatus())
                .vnpayTransactionId(booking.getVnpayTransactionId())
                .paymentUrl(booking.getPaymentUrl())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .bookingDetails(booking.getBookingDetails().stream()
                        .map(this::toBookingDetailResponse)
                        .collect(Collectors.toList()))
                .build();
    }
    private BookingDetailResponse toBookingDetailResponse(BookingDetail detail) {
        return BookingDetailResponse.builder()
                .id(detail.getId())
                .seatNumber(detail.getSeatNumber())
                .ticketPrice(detail.getTicketPrice())
                .movieScreen(toMovieScreenResponse(detail.getMovieScreen()))
                .build();
    }

    private MovieScreenResponse toMovieScreenResponse(MovieScreen screen) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        return MovieScreenResponse.builder().build();
//        return MovieScreenResponse.builder()
//                .id(screen.getId())
//                .screenRoom(screen.getScreenRoom())
//                .screenDate(screen.getScreenDate().format(dateFormatter))
//                .screenTime(screen.getScreenTime().format(timeFormatter))
//                .price(screen.getPrice())
//                .availableSeats(screen.getAvailableSeats())
//                .movie(BookingResponse.BookingDetailResponse.MovieScreenResponse.MovieBasicInfo.builder()
//                        .id(screen.getMovie().getId())
//                        .title(screen.getMovie().getTitle())
//                        .thumbnailUrl(screen.getMovie().getThumbnailUrl())
//                        .duration(screen.getMovie().getDuration())
//                        .ageRestriction(screen.getMovie().getAgeRestriction().name())
//                        .build())
//                .cinema(BookingResponse.BookingDetailResponse.MovieScreenResponse.CinemaBasicInfo.builder()
//                        .id(screen.getCinema().getId())
//                        .name(screen.getCinema().getName())
//                        .address(screen.getCinema().getAddress())
//                        .province(screen.getCinema().getProvince())
//                        .build())
//                .build();
    }
}
