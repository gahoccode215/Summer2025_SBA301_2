package com.sba301.online_ticket_sales.dto.person.request;

import com.sba301.online_ticket_sales.enums.Occupation;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonUpdateRequest {
  private String name;
  private String description;
  private LocalDate birthDate;
  private Double height;
  private Occupation occupation;
  private String biography;
  private Integer countryId;
  private List<String> images;
}
