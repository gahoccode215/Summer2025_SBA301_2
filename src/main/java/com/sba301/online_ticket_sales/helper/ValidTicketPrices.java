package com.sba301.online_ticket_sales.helper;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TicketPriceValidator.class)
@Documented
public @interface ValidTicketPrices {
  String message() default "Must include both WEEKDAY and WEEKEND ticket prices";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
