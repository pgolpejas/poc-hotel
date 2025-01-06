package com.hotel.core.domain.utils;

import static com.hotel.core.domain.messages.AppMessage.COM_902;
import static com.hotel.core.domain.messages.AppMessageManager.getMessage;

import java.util.Set;
import java.util.stream.Collectors;

import com.hotel.core.domain.ddd.DomainError;
import jakarta.validation.ConstraintViolation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationUtils {

  public static void notEmpty(final String value, final String message) {
    if (StringUtils.isEmpty(value)) {
      throw new DomainError(message);
    }
  }

  public static void maxSize(final String value, final int size, final String message) {
    if (value != null && value.length() > size) {
      throw new DomainError(message);
    }
  }

  public static void notNull(final Object value, final String message) {
    if (value == null) {
      throw new DomainError(message);
    }
  }

  public static <T> String parseViolationsToString(final Set<ConstraintViolation<T>> violations, final String objectName) {
    final String errorDetails =
        violations.stream().map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
            .collect(Collectors.joining(", "));

    return getMessage(COM_902, objectName, violations.size(), errorDetails);
  }

}
