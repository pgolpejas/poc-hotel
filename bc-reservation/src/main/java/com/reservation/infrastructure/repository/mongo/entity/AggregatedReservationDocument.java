package com.reservation.infrastructure.repository.mongo.entity;

import java.time.LocalDate;
import java.util.UUID;

import com.reservation.domain.model.Hotel;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "aggregated_reservation")
public class AggregatedReservationDocument {

  @Id
  private UUID id;

  private Integer roomTypeId;

  private Hotel hotel;

  private UUID guestId;

  private LocalDate start;

  private LocalDate end;

  private String status;

}
