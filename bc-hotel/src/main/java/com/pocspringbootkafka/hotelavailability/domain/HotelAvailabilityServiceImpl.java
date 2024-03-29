package com.pocspringbootkafka.hotelavailability.domain;

import com.pocspringbootkafka.hotelavailability.adapters.api.HotelAvailabilityController;
import com.pocspringbootkafka.hotelavailability.domain.model.HotelAvailabilityDbSearch;
import com.pocspringbootkafka.hotelavailability.domain.model.HotelAvailabilitySearch;
import com.pocspringbootkafka.hotelavailability.ports.HotelAvailabilityMessagingProducerService;
import com.pocspringbootkafka.hotelavailability.ports.HotelAvailabilityRepository;
import com.pocspringbootkafka.hotelavailability.ports.HotelAvailabilityService;
import com.pocspringbootkafka.shared.utils.SearchIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
class HotelAvailabilityServiceImpl implements HotelAvailabilityService {
    Logger log = LoggerFactory.getLogger(HotelAvailabilityServiceImpl.class);

    private final HotelAvailabilityRepository hotelAvailabilityRepository;
    private final HotelAvailabilityMessagingProducerService messagingProducerService;

    public HotelAvailabilityServiceImpl(HotelAvailabilityRepository hotelAvailabilityRepository,
                                        HotelAvailabilityMessagingProducerService messagingProducerService) {
        this.hotelAvailabilityRepository = hotelAvailabilityRepository;
        this.messagingProducerService = messagingProducerService;
    }

    @Override
    public String search(HotelAvailabilitySearch search) {
        log.info("search {}", search);

        messagingProducerService.sendMessage(search);

        return SearchIdGenerator.generateSearchId(search);
    }

    @Override
    public HotelAvailabilityDbSearch findById(String searchId) {
        log.info("findById {}", searchId);
        return hotelAvailabilityRepository.findById(searchId).orElse(null);

    }
}
