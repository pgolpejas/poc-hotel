package com.reservation.application.validator;


import com.reservation.application.dto.ReservationDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

class DateRangeValidator implements ConstraintValidator<ValidDateRange, ReservationDto> {
    @Override
    public boolean isValid(ReservationDto value, ConstraintValidatorContext context) {
        if (Objects.nonNull(value.start()) && Objects.nonNull(value.end())) {
            return !value.start().isAfter(value.end());
        } else {
            return true;
        }
    }
}
