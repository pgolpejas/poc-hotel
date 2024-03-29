package com.pocspringbootkafka.shared.validation;



import com.pocspringbootkafka.hotelavailability.adapters.api.HotelAvailabilitySearchDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

class DateRangeValidator implements ConstraintValidator<ValidDateRange, HotelAvailabilitySearchDto> {
    @Override
    public boolean isValid(HotelAvailabilitySearchDto value, ConstraintValidatorContext context) {
        if (Objects.nonNull(value.checkIn()) && Objects.nonNull(value.checkOut())) {
            return value.checkIn().compareTo(value.checkOut()) <= 0;
        } else {
            return true;
        }
    }
}
