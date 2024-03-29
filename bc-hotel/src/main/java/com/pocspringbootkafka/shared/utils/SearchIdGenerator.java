package com.pocspringbootkafka.shared.utils;

import com.pocspringbootkafka.hotelavailability.domain.model.HotelAvailabilitySearch;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SearchIdGenerator {

    private SearchIdGenerator() {
        throw new IllegalStateException("Utility class");
    }

    public static String generateSearchId(HotelAvailabilitySearch search) {
        List<Integer> ages = new ArrayList<>(search.ages());
        Collections.sort(ages);
        List<String> fieldsId = Arrays.asList(search.hotelId(), search.checkIn().format(DateTimeFormatter.BASIC_ISO_DATE),
            search.checkOut().format(DateTimeFormatter.BASIC_ISO_DATE), ages.stream().map(String::valueOf).collect(Collectors.joining("-")));
        return String.join("-", fieldsId);
    }

}
