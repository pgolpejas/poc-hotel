package com.reservation.infrastructure.repository.redis;

import java.util.UUID;

import com.reservation.infrastructure.repository.entity.HotelEntity;
import org.springframework.data.repository.CrudRepository;

public interface HotelRedisRepository extends CrudRepository<HotelEntity, UUID> {
}
