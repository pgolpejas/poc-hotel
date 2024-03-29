package com.reservation.infrastructure.repository;

import com.reservation.domain.model.Reservation;
import com.reservation.domain.repository.ReservationRepository;
import com.reservation.infrastructure.repository.jpa.ReservationJpaRepository;
import com.reservation.infrastructure.repository.mapper.ReservationRepositoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReservationRepositoryAdapter implements ReservationRepository {

    private final ReservationJpaRepository reservationJpaRepository;
    private final ReservationRepositoryMapper reservationRepositoryMapper;

    @Override
    public void save(final Reservation reservation) {
        this.reservationJpaRepository.save(this.reservationRepositoryMapper.mapToEntity(reservation));
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Reservation> findById(final UUID id) {
        return this.reservationJpaRepository.findById(id)
                .map(this.reservationRepositoryMapper::mapToAggregate);
    }
}
