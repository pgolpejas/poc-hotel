package com.pocspringbootkafka.hotelavailability.adapters.db;

import com.pocspringbootkafka.hotelavailability.domain.model.HotelAvailabilityDbSearch;
import com.pocspringbootkafka.hotelavailability.ports.HotelAvailabilityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class HotelAvailabilityRepositoryAdapter implements HotelAvailabilityRepository {
    Logger log = LoggerFactory.getLogger(HotelAvailabilityRepositoryAdapter.class);

    private final JpaHotelAvailabilitySearchRepository jpaHotelAvailabilitySearchRepository;
    private final HotelAvailabilitySearchEntityMapper hotelAvailabilityEntityMapper;

    public HotelAvailabilityRepositoryAdapter(JpaHotelAvailabilitySearchRepository jpaHotelAvailabilitySearchRepository,
                                              HotelAvailabilitySearchEntityMapper hotelAvailabilityEntityMapper) {
        this.jpaHotelAvailabilitySearchRepository = jpaHotelAvailabilitySearchRepository;
        this.hotelAvailabilityEntityMapper = hotelAvailabilityEntityMapper;
    }

    @Override
    public Optional<HotelAvailabilityDbSearch> findById(String id) {
        log.info("findById {}", id);
        Optional<HotelAvailabilitySearchEntity> entity = jpaHotelAvailabilitySearchRepository.findById(id);
        return entity.map(hotelAvailabilityEntityMapper::toHotelAvailabilitySearch);
    }

    @Override
    public HotelAvailabilityDbSearch save(HotelAvailabilityDbSearch search) {
        log.info("save {}", search);

        HotelAvailabilitySearchEntity entity = hotelAvailabilityEntityMapper.toHotelAvailabilitySearchEntity(search);
        entity = jpaHotelAvailabilitySearchRepository.save(entity);
        return hotelAvailabilityEntityMapper.toHotelAvailabilitySearch(entity);
    }

}
