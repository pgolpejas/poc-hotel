package com.pocspringbootkafka.utils;

import com.pocspringbootkafka.hotelavailability.adapters.api.HotelAvailabilitySearchDto;
import com.pocspringbootkafka.hotelavailability.domain.model.HotelAvailabilitySearch;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RequestUtils {

    private RequestUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static <T> HttpEntity<T> buildRequest(HttpHeaders headers, T entity) throws Exception {
        headers = (headers != null ? headers : new HttpHeaders());
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<T> request = new HttpEntity<>(entity, headers);
        return request;
    }

}
