package com.pochotel.reservation.ports;

import com.pochotel.reservation.domain.model.HotelAvailabilityDbSearch;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface HotelAvailabilityRepository {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<HotelAvailabilityDbSearch> findById(String id);

    HotelAvailabilityDbSearch save(HotelAvailabilityDbSearch search);

}
