package com.pochotel.reservation.domain;

import com.pochotel.reservation.domain.model.HotelAvailabilityDbSearch;
import com.pochotel.reservation.domain.model.HotelAvailabilitySearch;
import com.pochotel.reservation.ports.HotelAvailabilityRepository;
import com.pochotel.reservation.ports.HotelAvailabilityService;
import com.pochotel.shared.utils.SearchIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
class HotelAvailabilityServiceImpl implements HotelAvailabilityService {
    Logger log = LoggerFactory.getLogger(HotelAvailabilityServiceImpl.class);

    private final HotelAvailabilityRepository hotelAvailabilityRepository;

    public HotelAvailabilityServiceImpl(HotelAvailabilityRepository hotelAvailabilityRepository){
        this.hotelAvailabilityRepository = hotelAvailabilityRepository;
    }

    @Override
    public String search(HotelAvailabilitySearch search) {
        log.info("search {}", search);
        
        String key = SearchIdGenerator.generateSearchId(search);

        HotelAvailabilityDbSearch dtoToPersist = hotelAvailabilityRepository.findById(key)
                .map(searchFound -> createHotelAvailabilityDbSearch(search, key, searchFound.count() + 1))
                .orElse(createHotelAvailabilityDbSearch(search, key, 1));
        hotelAvailabilityRepository.save(dtoToPersist);

        return key;
    }

    private HotelAvailabilityDbSearch createHotelAvailabilityDbSearch(HotelAvailabilitySearch search, String searchId, Integer count) {
        return new HotelAvailabilityDbSearch(searchId, search.hotelId(), search.checkIn(), search.checkOut(), search.ages(), count);
    }

    @Override
    public HotelAvailabilityDbSearch findById(String searchId) {
        log.info("findById {}", searchId);
        return hotelAvailabilityRepository.findById(searchId).orElse(null);

    }
}
