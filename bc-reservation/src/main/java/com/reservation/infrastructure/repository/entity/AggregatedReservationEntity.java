package com.reservation.infrastructure.repository.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

import com.hotel.core.infrastructure.database.audit.AuditJpa;
import com.reservation.domain.model.AggregatedReservation.Reservation;
import com.reservation.infrastructure.repository.converter.AggregatedReservationConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "aggregated_reservation")
@SuppressWarnings("squid:S1170")
public class AggregatedReservationEntity extends AuditJpa implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  @Id
  private UUID id;

  @NotNull
  @Column(name = "hotel_id")
  private UUID hotelId;

  @NotNull
  @Column(name = "start_date")
  private LocalDate start;

  @NotNull
  @Column(name = "end_date")
  private LocalDate end;

  @Column(name = "aggregate")
  @JdbcTypeCode(SqlTypes.JSON)
  @Convert(converter = AggregatedReservationConverter.class)
  private Reservation reservation;
}
