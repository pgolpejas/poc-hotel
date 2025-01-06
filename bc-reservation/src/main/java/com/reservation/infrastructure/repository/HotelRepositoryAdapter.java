package com.reservation.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import com.reservation.domain.model.Hotel;
import com.reservation.domain.repository.HotelRepository;
import com.reservation.infrastructure.repository.entity.HotelEntity;
import com.reservation.infrastructure.repository.mapper.HotelRepositoryMapper;
import com.reservation.infrastructure.repository.redis.HotelRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class HotelRepositoryAdapter implements HotelRepository {

  private final HotelRedisRepository hotelRedisRepository;

  private final HotelRepositoryMapper hotelRepositoryMapper;

  @Transactional
  @Override
  public void save(final Hotel aggregate) {
    final HotelEntity entity = this.hotelRepositoryMapper.mapToEntity(aggregate);
    this.hotelRedisRepository.save(entity);
  }

  @Override
  public Optional<Hotel> findById(final UUID id) {
    return this.hotelRedisRepository.findById(id).map(this.hotelRepositoryMapper::mapToAggregate);
  }

}
