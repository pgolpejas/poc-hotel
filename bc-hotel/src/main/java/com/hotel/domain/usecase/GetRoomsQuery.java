package com.hotel.domain.usecase;

import com.hotel.core.domain.dto.Criteria;
import com.hotel.core.domain.dto.PaginationResponse;
import com.hotel.domain.model.Room;

public interface GetRoomsQuery {

  PaginationResponse<Room> getRooms(Criteria searchDto);
}
