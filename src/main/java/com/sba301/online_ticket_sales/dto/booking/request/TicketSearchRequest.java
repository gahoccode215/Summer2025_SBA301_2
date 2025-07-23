package com.sba301.online_ticket_sales.dto.booking.request;

import lombok.Data;

@Data
public class TicketSearchRequest {
  private String ticketCode;
  private int page = 0;
  private int size = 20;
  private String sortBy = "createdAt";
  private String sortDirection = "DESC";
  private Long cinemaId;
}
