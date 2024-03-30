package com.hotel.core.domain.ddd;

import java.io.Serial;

public class DomainError extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 758479981323467467L;

  public DomainError(final String message) {
    super(message);
  }
}
