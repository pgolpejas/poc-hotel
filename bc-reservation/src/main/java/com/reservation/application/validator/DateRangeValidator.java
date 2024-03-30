package com.reservation.application.validator;


import com.reservation.application.dto.ReservationDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

class DateRangeValidator implements ConstraintValidator<ValidDateRange, ReservationDto> {
    @Override
    public boolean isValid(ReservationDto value, ConstraintValidatorContext context) {
        return !value.start().isAfter(value.end());
    }
}
