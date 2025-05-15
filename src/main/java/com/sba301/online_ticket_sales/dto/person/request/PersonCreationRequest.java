package com.sba301.online_ticket_sales.dto.person.request;

import com.sba301.online_ticket_sales.enums.Occupation;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PersonCreationRequest {
    String name;
    String biography;
    LocalDate birthDate;
    String description;
    Double height;
    Occupation occupation;
}
