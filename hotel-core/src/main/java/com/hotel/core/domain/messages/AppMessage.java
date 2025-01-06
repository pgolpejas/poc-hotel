package com.hotel.core.domain.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AppMessage {

  // Categories:
  // 900-999: Commons

  COM_900("900", "Version cannot be null"),
  COM_901("901", "Conflict in the version field. latest version %d. re-load the registry to see the new changes"),
  COM_902("902", "Validation failed for %s. Errors [%d]: %s"),
  // COM_903("903", "At least productId, externalId and/or reference filter must be provided"),
  COM_904("904", "Something went wrong loading json from object: %s"),
  COM_905("905", "Error creating %s method"),
  COM_906("906", "https://icdmdispla.com/probs/ERR_%s"),
  COM_907("907", "%s parameter has invalid uuid value"),
  COM_908("908", "Error combining results from async tasks: Process[1] %s - Process[2] %s. Error: %s");

  private final String messageCode;

  private final String message;
}
