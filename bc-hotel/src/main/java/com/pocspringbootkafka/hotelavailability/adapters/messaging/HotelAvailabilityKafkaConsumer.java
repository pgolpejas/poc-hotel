package com.pocspringbootkafka.hotelavailability.adapters.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pocspringbootkafka.hotelavailability.domain.model.HotelAvailabilityDbSearch;
import com.pocspringbootkafka.hotelavailability.domain.model.HotelAvailabilitySearch;
import com.pocspringbootkafka.hotelavailability.ports.HotelAvailabilityRepository;
import com.pocspringbootkafka.shared.constants.AppConstants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
class HotelAvailabilityKafkaConsumer {
    Logger log = LoggerFactory.getLogger(HotelAvailabilityKafkaConsumer.class);

    private final HotelAvailabilityRepository hotelAvailabilityRepository;
    private final ObjectMapper objectMapper;

    public HotelAvailabilityKafkaConsumer(HotelAvailabilityRepository hotelAvailabilityRepository, ObjectMapper objectMapper) {
        this.hotelAvailabilityRepository = hotelAvailabilityRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(id = "search", topics = AppConstants.TOPIC_NAME, groupId = AppConstants.GROUP_ID)
    public void consumeMessage(@Payload final ConsumerRecord<String, String> message,
                               @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String key,
                               @Header(name = KafkaHeaders.OFFSET, required = false) Long offset,
                               @Header(KafkaHeaders.RECEIVED_PARTITION) int partition
    ) throws JsonProcessingException {
        final HotelAvailabilitySearch search = objectMapper.readValue(message.value(), HotelAvailabilitySearch.class);
        log.info("Received(key={}, offset={}, partition={}): {}", key, offset, partition, search);

        HotelAvailabilityDbSearch dtoToPersist = hotelAvailabilityRepository.findById(key)
                .map(searchFound -> createHotelAvailabilityDbSearch(search, key, searchFound.count() + 1))
                .orElse(createHotelAvailabilityDbSearch(search, key, 1));
        hotelAvailabilityRepository.save(dtoToPersist);
    }

    private HotelAvailabilityDbSearch createHotelAvailabilityDbSearch(HotelAvailabilitySearch search, String searchId, Integer count) {
        return new HotelAvailabilityDbSearch(searchId, search.hotelId(), search.checkIn(), search.checkOut(), search.ages(), count);
    }
}
