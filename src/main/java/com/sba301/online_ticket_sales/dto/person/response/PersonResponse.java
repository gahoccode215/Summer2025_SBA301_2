package com.sba301.online_ticket_sales.dto.person.response;

import com.sba301.online_ticket_sales.enums.Occupation;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonResponse {
  private Integer id;
  private String name;
  private String description;
  private LocalDate birthDate;
  private Double height;
  private Occupation occupation;
  private String biography;
  private String country;
  private List<String> images;
}
