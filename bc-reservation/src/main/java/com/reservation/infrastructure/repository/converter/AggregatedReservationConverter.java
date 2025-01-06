package com.reservation.infrastructure.repository.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.reservation.domain.model.AggregatedReservation.Reservation;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AggregatedReservationConverter implements AttributeConverter<Reservation, String> {

  private final ObjectMapper objectMapper;

  public AggregatedReservationConverter() {
    this.objectMapper = JsonMapper.builder()
        .addModule(new JavaTimeModule())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .build();
  }

  @Override
  public String convertToDatabaseColumn(Reservation attribute) {
    try {
      return this.objectMapper.writeValueAsString(attribute);
    } catch (final JsonProcessingException e) {
      throw new IllegalArgumentException("Error converting Reservation to JSON", e);
    }
  }

  @Override
  public Reservation convertToEntityAttribute(String dbData) {
    try {
      return this.objectMapper.readValue(dbData, Reservation.class);
    } catch (final JsonProcessingException e) {
      throw new IllegalArgumentException("Error converting JSON to Reservation", e);
    }
  }
}
