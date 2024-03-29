package com.pocspringbootkafka.shared.validation;

import com.pocspringbootkafka.hotelavailability.adapters.api.HotelAvailabilitySearchDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
class DateRangeValidatorTest {

    @InjectMocks
    private DateRangeValidator dateRangeValidator;

    @Test
    void dateRangeValidatorValueValid() {

        var hotelAvailabilitySearchDto = new HotelAvailabilitySearchDto("1234Abc", LocalDate.now(), LocalDate.now(), List.of(1));

        assertTrue(dateRangeValidator.isValid(hotelAvailabilitySearchDto, null));
    }

    @Test
    void dateRangeValidatorCheckInAfterCheckOut() {

        var hotelAvailabilitySearchDto = new HotelAvailabilitySearchDto("1234Abc", LocalDate.now().plusDays(1), LocalDate.now(), List.of(1));

        assertFalse(dateRangeValidator.isValid(hotelAvailabilitySearchDto, null));
    }

}