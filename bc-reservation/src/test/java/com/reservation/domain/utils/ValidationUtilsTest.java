package com.reservation.domain.utils;

import com.hotel.core.domain.ddd.DomainError;
import com.hotel.core.domain.utils.ValidationUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class ValidationUtilsTest {

    @Test
    void when_value_is_empty_should_throw_exception() {
        assertThrows(DomainError.class, () -> {
            ValidationUtils.notEmpty("", "Value cannot be empty");
        });
    }

    @Test
    void when_value_is_not_empty_should_not_throw_exception() {
        assertDoesNotThrow(() -> ValidationUtils.notEmpty("test", "Value cannot be empty"));
    }

    @Test
    void when_value_exceeds_max_size_should_throw_exception() {
        assertThrows(DomainError.class, () -> {
            ValidationUtils.maxSize("longerthanfive", 5, "Value exceeds max limit");
        });
    }

    @Test
    void when_value_is_within_max_size_should_throw_exception() {
        assertDoesNotThrow(() -> {
            ValidationUtils.maxSize("short", 10, "Value exceeds max limit");
        });
    }

    @Test
    void when_values_is_null_should_throw_exception() {
        assertThrows(DomainError.class, () -> {
            ValidationUtils.notNull(null, "Value cannot be null");
        });
    }

    @Test
    void when_value_is_not_null_should_not_throw_exception() {
        assertDoesNotThrow(() -> {
            ValidationUtils.notNull(new Object(), "Value cannot be null");
        });
    }

    @Test
    void when_value_is_exactly_max_size_should_not_throw_exception() {
        assertDoesNotThrow(() -> {
            ValidationUtils.maxSize("12345", 5, "Value exceeds max limit");
        });
    }

    @Test
    void when_value_is_less_than_max_size_should_not_throw_exception() {
        assertDoesNotThrow(() -> {
            ValidationUtils.maxSize("1234", 5, "Value exceeds max limit");
        });
    }

    @Test
    void when_value_is_greater_than_max_size_by_one_should_throw_exception() {
        assertThrows(DomainError.class, () -> {
            ValidationUtils.maxSize("123456", 5, "Value exceeds max limit");
        });
    }

    @Test
    void when_value_is_much_greater_than_max_size_should_throw_exception() {
        assertThrows(DomainError.class, () -> {
            ValidationUtils.maxSize("1234567890", 5, "Value exceeds max limit");
        });
    }

    @Test
    void when_value_is_null_should_not_throw_exception() {
        assertDoesNotThrow(() -> {
            ValidationUtils.maxSize(null, 5, "Value exceeds max limit");
        });
    }

}
