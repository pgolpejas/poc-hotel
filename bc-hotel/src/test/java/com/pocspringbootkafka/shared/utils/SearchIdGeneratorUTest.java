package com.pocspringbootkafka.shared.utils;

import com.pocspringbootkafka.hotelavailability.domain.model.HotelAvailabilitySearch;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.List;

class SearchIdGeneratorUTest {

    @Test
    void createNewInstanceKo() throws NoSuchMethodException {
        Constructor<SearchIdGenerator> pcc = SearchIdGenerator.class.getDeclaredConstructor();
        pcc.setAccessible(true);
        InvocationTargetException thrown =  Assertions.assertThrows(InvocationTargetException.class, () -> pcc.newInstance());
        Assertions.assertEquals("Utility class", thrown.getTargetException().getMessage());
    }

    @Test
    void generateSearchId() {
        List<Integer> ages = List.of(1, 2, 3);
        Assertions.assertEquals("hotelId-20230613-20230613-1-2-3", SearchIdGenerator.generateSearchId(
            new HotelAvailabilitySearch("hotelId", LocalDate.of(2023, 6, 13), LocalDate.of(2023, 6, 13), ages)));
        ages = List.of(3, 2, 1);
        Assertions.assertEquals("hotelId-20230613-20230613-1-2-3", SearchIdGenerator.generateSearchId(
            new HotelAvailabilitySearch("hotelId", LocalDate.of(2023, 6, 13), LocalDate.of(2023, 6, 13), ages)));
        ages = List.of(3, 1, 2);
        Assertions.assertEquals("hotelId-20230613-20230613-1-2-3", SearchIdGenerator.generateSearchId(
            new HotelAvailabilitySearch("hotelId", LocalDate.of(2023, 6, 13), LocalDate.of(2023, 6, 13), ages)));
        ages = List.of(1, 3, 2);
        Assertions.assertEquals("hotelId-20230613-20230613-1-2-3", SearchIdGenerator.generateSearchId(
            new HotelAvailabilitySearch("hotelId", LocalDate.of(2023, 6, 13), LocalDate.of(2023, 6, 13), ages)));

        ages = List.of(2, 1, 3);
        Assertions.assertEquals("hotelId-20230613-20230613-1-2-3", SearchIdGenerator.generateSearchId(
            new HotelAvailabilitySearch("hotelId", LocalDate.of(2023, 6, 13), LocalDate.of(2023, 6, 13), ages)));

    }

}