package com.reservation.infrastructure.consumer;

import static com.hotel.core.domain.utils.EventHelper.EVENT_TYPE;

import java.util.function.Consumer;

import com.reservation.domain.model.Room;
import com.reservation.domain.usecase.CreateRoomDataCommand;
import com.reservation.infrastructure.consumer.mapper.RoomSnapshotEventMapper;
import com.room.domain.avro.v1.RoomSnapshot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RoomDataConsumer {

  private final CreateRoomDataCommand createRoomDataCommand;

  private final RoomSnapshotEventMapper roomSnapshotEventMapper;

  /**
   * Handle room. TODO: Solve concurrency problem with the hotel
   *
   * @return the consumer
   */
  @Bean
  public Consumer<Message<RoomSnapshot>> handleRoom() {
    return message -> {
      log.info("[DATA-INPUT-HEADER-RECEIVED]: {}", message.getHeaders());

      final Room room = this.roomSnapshotEventMapper.mapToRoom(message.getPayload());

      this.createRoomDataCommand.createRoomData(room, message.getPayload().getHotelId());

      log.info("{}: {}", message.getHeaders().get(EVENT_TYPE), message.getPayload());
    };
  }

}
