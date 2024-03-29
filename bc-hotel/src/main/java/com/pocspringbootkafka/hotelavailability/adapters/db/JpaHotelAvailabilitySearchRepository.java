package com.pocspringbootkafka.hotelavailability.adapters.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface JpaHotelAvailabilitySearchRepository extends JpaRepository<HotelAvailabilitySearchEntity, String> {
}
