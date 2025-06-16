package com.sba301.online_ticket_sales.dto.cinema.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public  class CinemaBasicInfo {
    private Long id;
    private String name;
    private String address;
    private String province;
}
