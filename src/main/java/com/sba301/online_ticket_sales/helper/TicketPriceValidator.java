package com.sba301.online_ticket_sales.helper;

import com.sba301.online_ticket_sales.dto.cinema.request.CinemaRequest;
import com.sba301.online_ticket_sales.dto.cinema.request.TicketPriceRequest;
import com.sba301.online_ticket_sales.enums.DateType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TicketPriceValidator implements ConstraintValidator<ValidTicketPrices, CinemaRequest> {

  @Override
  public boolean isValid(CinemaRequest request, ConstraintValidatorContext context) {
    if (request.getTicketPriceRequests() == null || request.getTicketPriceRequests().size() != 2) {
      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate(
              "Must provide exactly 2 ticket prices (WEEKDAY and WEEKEND)")
          .addPropertyNode("1900")
          .addConstraintViolation();
      return false;
    }

    boolean hasWeekday = false;
    boolean hasWeekend = false;

    for (TicketPriceRequest t : request.getTicketPriceRequests()) {
      if (t.getDateType() == DateType.WEEKDAY) hasWeekday = true;
      else if (t.getDateType() == DateType.WEEKEND) hasWeekend = true;
    }

    if (!hasWeekday || !hasWeekend) {
      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate(
              "Both WEEKDAY and WEEKEND ticket prices are required")
          .addPropertyNode("1901")
          .addConstraintViolation();
      return false;
    }

    return true;
  }
}
