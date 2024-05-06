package com.hotel.domain.usecase;

import com.hotel.core.domain.dto.Criteria;
import com.hotel.core.domain.dto.PaginationResponse;
import com.hotel.domain.model.Hotel;

public interface GetHotelsUseCase {

    PaginationResponse<Hotel> getHotels(Criteria searchDto);
}
