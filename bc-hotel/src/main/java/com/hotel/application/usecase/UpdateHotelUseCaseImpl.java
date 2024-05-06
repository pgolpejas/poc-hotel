package com.hotel.application.usecase;

import com.hotel.domain.exception.HotelNotFoundException;
import com.hotel.domain.model.Hotel;
import com.hotel.domain.repository.HotelRepository;
import com.hotel.domain.usecase.UpdateHotelUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UpdateHotelUseCaseImpl implements UpdateHotelUseCase {

    private final HotelRepository hotelRepository;

    @Transactional
    @Override
    public void updateHotel(final Hotel hotel) {

        Optional<Hotel> optionalHotel = hotelRepository.findById(hotel.id());

        if (optionalHotel.isEmpty()) {
            throw new HotelNotFoundException("Hotel with id %s not found", hotel.id().toString());
        } else {
            Hotel aggregate = optionalHotel.get();
            aggregate.update(hotel);
            this.hotelRepository.save(aggregate);
        }
    }
}
