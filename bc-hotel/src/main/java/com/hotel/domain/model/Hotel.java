package com.hotel.domain.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

import com.hotel.core.domain.ddd.AggregateRoot;
import com.hotel.core.domain.utils.ValidationUtils;
import com.hotel.domain.event.HotelDomainEvent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings({"java:S107"})
public class Hotel extends AggregateRoot implements Serializable {

  @Serial
  private static final long serialVersionUID = 7619183896070437504L;

  private HotelId id;

  private int version;

  @NotBlank
  @Size(max = 100)
  private String name;

  @NotBlank
  @Size(max = 200)
  private String address;

  @NotBlank
  @Size(max = 100)
  private String city;

  @NotBlank
  @Size(max = 100)
  private String state;

  @NotBlank
  @Size(max = 100)
  private String country;

  @NotBlank
  @Size(max = 10)
  private String postalCode;

  @Builder
  public Hotel(@NotNull final UUID id,
      final int version,
      @NotBlank @Size(max = 100) final String name,
      @NotBlank @Size(max = 200) final String address,
      @NotBlank @Size(max = 100) final String city,
      @NotBlank @Size(max = 100) final String state,
      @NotBlank @Size(max = 100) final String country,
      @NotBlank @Size(max = 10) final String postalCode) {

    this.id = new HotelId(id);
    this.version = version;
    this.name = name;
    this.address = address;
    this.city = city;
    this.state = state;
    this.country = country;
    this.postalCode = postalCode;

    // Common validations for all actions on the aggregate
    commonValidations(this);
  }

  private static void commonValidations(final Hotel hotel) {
    ValidationUtils.maxSize(hotel.name(), 100, "name can not be greater than 100 characters");
    ValidationUtils.maxSize(hotel.address(), 200, "address can not be greater than 200 characters");
    ValidationUtils.maxSize(hotel.city(), 100, "city can not be greater than 100 characters");
    ValidationUtils.maxSize(hotel.state(), 100, "state can not be greater than 100 characters");
    ValidationUtils.maxSize(hotel.country(), 100, "country can not be greater than 100 characters");
    ValidationUtils.maxSize(hotel.postalCode(), 10, "postalCode can not be greater than 10 characters");
  }

  public static Hotel create(final Hotel hotel) {

    final Hotel newAggregate = Hotel.builder()
        .id(hotel.id())
        .name(hotel.name())
        .address(hotel.address())
        .city(hotel.city())
        .state(hotel.state())
        .country(hotel.country())
        .postalCode(hotel.postalCode())
        .build();

    newAggregate.registerEvent(
        HotelDomainEvent.HotelCreatedEvent.builder()
            .id(newAggregate.id())
            .name(newAggregate.name())
            .address(newAggregate.address())
            .city(newAggregate.city())
            .state(newAggregate.state())
            .country(newAggregate.country())
            .postalCode(newAggregate.postalCode())
            .build());

    return newAggregate;
  }

  public void delete() {
    this.registerEvent(HotelDomainEvent.HotelDeletedEvent.builder()
        .id(this.id())
        .build());
  }

  public void update(final Hotel hotel) {
    this.name = hotel.name();
    this.address = hotel.address();
    this.city = hotel.city();
    this.state = hotel.state();
    this.country = hotel.country();
    this.postalCode = hotel.postalCode();

    commonValidations(this);

    this.registerEvent(
        HotelDomainEvent.HotelUpdatedEvent.builder()
            .id(this.id())
            .name(this.name())
            .address(this.address())
            .city(this.city())
            .state(this.state())
            .country(this.country())
            .postalCode(this.postalCode())
            .build());
  }

  public UUID id() {
    return this.id.value();
  }

  @Override
  public String getAggregateId() {
    return this.id.value().toString();
  }

  public String name() {
    return this.name;
  }

  public String address() {
    return this.address;
  }

  public String city() {
    return this.city;
  }

  public String state() {
    return this.state;
  }

  public String country() {
    return this.country;
  }

  public String postalCode() {
    return this.postalCode;
  }

}
