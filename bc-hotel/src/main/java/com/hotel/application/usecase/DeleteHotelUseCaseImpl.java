package com.hotel.application.usecase;

import com.hotel.domain.exception.HotelNotFoundException;
import com.hotel.domain.model.Hotel;
import com.hotel.domain.repository.HotelRepository;
import com.hotel.domain.usecase.DeleteHotelUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeleteHotelUseCaseImpl implements DeleteHotelUseCase {

    private final HotelRepository hotelRepository;

    @Transactional
    @Override
    public void deleteHotel(final UUID id) {
        Optional<Hotel> optionalHotel = hotelRepository.findById(id);

        if (optionalHotel.isEmpty()) {
            throw new HotelNotFoundException("Hotel with id %s not found", id.toString());
        } else {
            Hotel hotel = optionalHotel.get();
            hotel.delete();
            this.hotelRepository.delete(hotel);
        }
    }
}
