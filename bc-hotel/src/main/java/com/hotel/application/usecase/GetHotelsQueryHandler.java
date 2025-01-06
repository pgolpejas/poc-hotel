package com.hotel.application.usecase;

import com.hotel.core.domain.dto.Criteria;
import com.hotel.core.domain.dto.PaginationResponse;
import com.hotel.domain.model.Hotel;
import com.hotel.domain.repository.HotelRepository;
import com.hotel.domain.usecase.GetHotelsQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetHotelsQueryHandler implements GetHotelsQuery {

  private final HotelRepository hotelRepository;

  @Override
  public PaginationResponse<Hotel> getHotels(final Criteria searchDto) {
    return this.hotelRepository.search(searchDto);
  }
}
