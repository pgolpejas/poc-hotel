package com.hotel.core.domain.messages;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppMessageManager {
  public static String getPlainMessage(final AppMessage key, final Object... args) {
    final String messageTemplate = key.getMessage();
    return String.format("%s", String.format(messageTemplate, args));
  }

  public static String getMessage(final AppMessage key, final Object... args) {
    final String messageCode = key.getMessageCode();
    final String messageTemplate = key.getMessage();
    return String.format("[%s] %s", messageCode, String.format(messageTemplate, args));
  }
}
