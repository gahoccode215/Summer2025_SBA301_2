package com.sba301.online_ticket_sales.dto.cinema.request;

import com.sba301.online_ticket_sales.enums.RequestType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CinemaRequest {

  private Long id;

  @NotNull(message = "Name cannot be null")
  @NotBlank(message = "Name cannot be blank")
  private String name;

  @NotNull(message = "Address cannot be null")
  @NotBlank(message = "Address cannot be blank")
  private String address;

  @NotNull(message = "Hotline cannot be null")
  @NotBlank(message = "Hotline cannot be blank")
  private String hotline;

  @NotNull(message = "Province cannot be null")
  @NotBlank(message = "Province cannot be blank")
  private String province;

  private RequestType requestType;

  private List<RoomRequest> roomRequestList;
}
