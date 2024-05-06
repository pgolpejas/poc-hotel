package com.hotel.domain.usecase;

import com.hotel.core.domain.dto.Criteria;
import com.hotel.core.domain.dto.PaginationResponse;
import com.hotel.domain.model.Room;

public interface GetRoomsUseCase {

    PaginationResponse<Room> getRooms(Criteria searchDto);
}
