package com.hotel.application.usecase;

import com.hotel.core.domain.dto.Criteria;
import com.hotel.core.domain.dto.PaginationResponse;
import com.hotel.domain.model.Room;
import com.hotel.domain.repository.RoomRepository;
import com.hotel.domain.usecase.GetRoomsQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetRoomsQueryHandler implements GetRoomsQuery {

  private final RoomRepository repository;

  @Override
  public PaginationResponse<Room> getRooms(final Criteria searchDto) {
    return this.repository.search(searchDto);
  }
}
