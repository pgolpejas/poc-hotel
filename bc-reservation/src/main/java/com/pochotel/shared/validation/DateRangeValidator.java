package com.pochotel.shared.validation;



import com.pochotel.reservation.adapters.api.HotelAvailabilitySearchDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

class DateRangeValidator implements ConstraintValidator<ValidDateRange, HotelAvailabilitySearchDto> {
    @Override
    public boolean isValid(HotelAvailabilitySearchDto value, ConstraintValidatorContext context) {
        if (Objects.nonNull(value.checkIn()) && Objects.nonNull(value.checkOut())) {
            return !value.checkIn().isAfter(value.checkOut());
        } else {
            return true;
        }
    }
}
