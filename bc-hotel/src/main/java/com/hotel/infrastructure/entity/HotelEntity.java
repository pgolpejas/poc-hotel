package com.hotel.infrastructure.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "hotel")
@SuppressWarnings("squid:S1170")
public class HotelEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private UUID id;

    @Version
    private int version;

    @NotBlank
    @Size(max = 100)
    @Column(name = "name")
    private String name;

    @NotBlank
    @Size(max = 200)
    @Column(name = "address")
    private String address;

    @NotBlank
    @Size(max = 100)
    @Column(name = "city")
    private String city;

    @NotBlank
    @Size(max = 100)
    @Column(name = "state")
    private String state;

    @NotBlank
    @Size(max = 100)
    @Column(name = "country")
    private String country;

    @NotBlank
    @Size(max = 100)
    @Column(name = "postal_code")
    private String postalCode;
}
