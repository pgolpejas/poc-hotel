package com.pocspringbootkafka.hotelavailability.adapters.api;

import com.pocspringbootkafka.hotelavailability.domain.model.HotelAvailabilityDbSearch;
import com.pocspringbootkafka.hotelavailability.ports.HotelAvailabilityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Hotel availability controller", description = "Hotel availability API")
@RequestMapping(value = HotelAvailabilityController.MAPPING)
public class HotelAvailabilityController {

    public static final String DELIMITER_PATH = "/";
    public static final String MAPPING = DELIMITER_PATH + "hotel-availability/1.0";
    public static final String SEARCH_PATH = DELIMITER_PATH + "search";
    public static final String COUNT_PATH = DELIMITER_PATH + "count/{searchId}";

    private final HotelAvailabilityService service;
    private final HotelAvailabilityDtoMapper mapper;

    public HotelAvailabilityController(HotelAvailabilityService service, HotelAvailabilityDtoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping(value = SEARCH_PATH)
    public String search(@Valid @RequestBody HotelAvailabilitySearchDto searchDto) {
        return service.search(mapper.toHotelAvailabilitySearch(searchDto));
    }

    @GetMapping(value = COUNT_PATH)
    public ResponseEntity<HotelAvailabilitySearchCountDto> count(@PathVariable("searchId") String searchId) {
        HotelAvailabilityDbSearch search = service.findById(searchId);

        if (null != search) {
            return ResponseEntity.ok(new HotelAvailabilitySearchCountDto(searchId,
                new HotelAvailabilitySearchDto(search.hotelId(), search.checkIn(), search.checkOut(), search.ages()), search.count()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
