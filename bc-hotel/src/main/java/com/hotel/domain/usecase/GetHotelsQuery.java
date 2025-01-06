package com.hotel.domain.usecase;

import com.hotel.core.domain.dto.Criteria;
import com.hotel.core.domain.dto.PaginationResponse;
import com.hotel.domain.model.Hotel;

public interface GetHotelsQuery {

  PaginationResponse<Hotel> getHotels(Criteria searchDto);
}
