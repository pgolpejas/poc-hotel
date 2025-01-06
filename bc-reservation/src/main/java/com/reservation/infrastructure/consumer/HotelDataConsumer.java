package com.reservation.infrastructure.consumer;

import static com.hotel.core.domain.utils.EventHelper.EVENT_TYPE;

import java.util.function.Consumer;

import com.hotel.domain.avro.v1.HotelSnapshot;
import com.reservation.domain.model.Hotel;
import com.reservation.domain.usecase.CreateHotelDataCommand;
import com.reservation.infrastructure.consumer.mapper.HotelSnapshotEventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class HotelDataConsumer {

  private final HotelSnapshotEventMapper hotelSnapshotEventMapper;

  private final CreateHotelDataCommand createHotelDataCommand;

  @Bean
  public Consumer<Message<HotelSnapshot>> handleHotel() {
    return message -> {
      log.info("[DATA-INPUT-HEADER-RECEIVED]: {}", message.getHeaders());

      final Hotel hotel = this.hotelSnapshotEventMapper.mapToHotel(message.getPayload());

      this.createHotelDataCommand.createHotelData(hotel);

      log.info("{}: {}", message.getHeaders().get(EVENT_TYPE), message.getPayload());
    };
  }

}
