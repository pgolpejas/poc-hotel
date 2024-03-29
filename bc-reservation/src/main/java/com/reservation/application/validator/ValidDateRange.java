package com.reservation.application.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateRangeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateRange {
    String message() default "{validation.date.InRange.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
