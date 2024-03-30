package com.hotel.core.domain.utils;

import com.hotel.core.domain.ddd.DomainError;
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

}
