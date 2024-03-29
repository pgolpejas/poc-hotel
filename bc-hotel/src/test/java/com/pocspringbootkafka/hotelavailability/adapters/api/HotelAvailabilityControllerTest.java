package com.pocspringbootkafka.hotelavailability.adapters.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pocspringbootkafka.hotelavailability.domain.model.HotelAvailabilityDbSearch;
import com.pocspringbootkafka.hotelavailability.ports.HotelAvailabilityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HotelAvailabilityController.class)
class HotelAvailabilityControllerTest {

    @MockBean
    private HotelAvailabilityService service;

    @MockBean
    private HotelAvailabilityDtoMapper mapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @Test
    void search() throws Exception {
        final String searchId = "test";
        HotelAvailabilitySearchDto searchDto = new HotelAvailabilitySearchDto("1234Abc", LocalDate.now(), LocalDate.now(), List.of(1, 2, 3));

        when(service.search(mapper.toHotelAvailabilitySearch(searchDto))).thenReturn(searchId);

        mvc.perform(MockMvcRequestBuilders.post(HotelAvailabilityController.MAPPING + HotelAvailabilityController.SEARCH_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(searchDto))).andExpect(status().isOk()).andDo(print());

    }

    @Test
    void searchValidationHotelIdEmpty() throws Exception {
        final String searchId = "test";
        HotelAvailabilitySearchDto searchDto = new HotelAvailabilitySearchDto(null, LocalDate.now(), LocalDate.now(), List.of(1, 2, 3));

        when(service.search(mapper.toHotelAvailabilitySearch(searchDto))).thenReturn(searchId);

        mvc.perform(
                MockMvcRequestBuilders.post(HotelAvailabilityController.MAPPING + HotelAvailabilityController.SEARCH_PATH)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(searchDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].field").value("hotelId"))
            .andExpect(jsonPath("$.errors[0].code").value("NotEmpty"))
            .andExpect(jsonPath("$.errors[0].defaultMessage").value("must not be empty"))
            .andDo(print())
            .andReturn();
    }

    @Test
    void searchValidationCheckInEmpty() throws Exception {
        final String searchId = "test";
        HotelAvailabilitySearchDto searchDto = new HotelAvailabilitySearchDto("1234Abc", null, LocalDate.now(), List.of(1, 2, 3));

        when(service.search(mapper.toHotelAvailabilitySearch(searchDto))).thenReturn(searchId);

        mvc.perform(
                MockMvcRequestBuilders.post(HotelAvailabilityController.MAPPING + HotelAvailabilityController.SEARCH_PATH)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(searchDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].field").value("checkIn"))
            .andExpect(jsonPath("$.errors[0].code").value("NotNull"))
            .andExpect(jsonPath("$.errors[0].defaultMessage").value("must not be null"))
            .andDo(print())
            .andReturn();
    }

    @Test
    void searchValidationCheckOutEmpty() throws Exception {
        final String searchId = "test";
        HotelAvailabilitySearchDto searchDto = new HotelAvailabilitySearchDto("1234Abc", LocalDate.now(), null, List.of(1, 2, 3));

        when(service.search(mapper.toHotelAvailabilitySearch(searchDto))).thenReturn(searchId);

        mvc.perform(
                MockMvcRequestBuilders.post(HotelAvailabilityController.MAPPING + HotelAvailabilityController.SEARCH_PATH)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(searchDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].field").value("checkOut"))
            .andExpect(jsonPath("$.errors[0].code").value("NotNull"))
            .andExpect(jsonPath("$.errors[0].defaultMessage").value("must not be null"))
            .andDo(print())
            .andReturn();
    }

    @Test
    void searchValidationAgesEmpty() throws Exception {
        final String searchId = "test";
        HotelAvailabilitySearchDto searchDto = new HotelAvailabilitySearchDto("1234Abc", LocalDate.now(), LocalDate.now(), null);

        when(service.search(mapper.toHotelAvailabilitySearch(searchDto))).thenReturn(searchId);

        mvc.perform(
                MockMvcRequestBuilders.post(HotelAvailabilityController.MAPPING + HotelAvailabilityController.SEARCH_PATH)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(searchDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].field").value("ages"))
            .andExpect(jsonPath("$.errors[0].code").value("NotEmpty"))
            .andExpect(jsonPath("$.errors[0].defaultMessage").value("must not be empty"))
            .andDo(print())
            .andReturn();
    }

    @Test
    void searchValidationAgesSize() throws Exception {
        final String searchId = "test";
        HotelAvailabilitySearchDto searchDto =
            new HotelAvailabilitySearchDto("1234Abc", LocalDate.now(), LocalDate.now(), List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11));

        when(service.search(mapper.toHotelAvailabilitySearch(searchDto))).thenReturn(searchId);

        mvc.perform(
                MockMvcRequestBuilders.post(HotelAvailabilityController.MAPPING + HotelAvailabilityController.SEARCH_PATH)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(searchDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].field").value("ages"))
            .andExpect(jsonPath("$.errors[0].code").value("Size"))
            .andExpect(jsonPath("$.errors[0].defaultMessage").value("size must be between 0 and 10"))
            .andDo(print())
            .andReturn();
    }

    @Test
    void searchValidationAgesNegative() throws Exception {
        final String searchId = "test";
        HotelAvailabilitySearchDto searchDto = new HotelAvailabilitySearchDto("1234Abc", LocalDate.now(), LocalDate.now(), List.of(1, -1));

        when(service.search(mapper.toHotelAvailabilitySearch(searchDto))).thenReturn(searchId);

        mvc.perform(
                MockMvcRequestBuilders.post(HotelAvailabilityController.MAPPING + HotelAvailabilityController.SEARCH_PATH)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(searchDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].field").value("ages[1]"))
            .andExpect(jsonPath("$.errors[0].code").value("Range"))
            .andExpect(jsonPath("$.errors[0].defaultMessage").value("must be between 0 and 120"))
            .andDo(print())
            .andReturn();
    }

    @Test
    void searchValidationAgesInvalidRange() throws Exception {
        final String searchId = "test";
        HotelAvailabilitySearchDto searchDto = new HotelAvailabilitySearchDto("1234Abc", LocalDate.now(), LocalDate.now(), List.of(121, 1));

        when(service.search(mapper.toHotelAvailabilitySearch(searchDto))).thenReturn(searchId);

        mvc.perform(
                MockMvcRequestBuilders.post(HotelAvailabilityController.MAPPING + HotelAvailabilityController.SEARCH_PATH)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(searchDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].field").value("ages[0]"))
            .andExpect(jsonPath("$.errors[0].code").value("Range"))
            .andExpect(jsonPath("$.errors[0].defaultMessage").value("must be between 0 and 120"))
            .andDo(print())
            .andReturn();
    }

    @Test
    void searchValidationHotelIdPatternSize() throws Exception {
        final String searchId = "test";
        HotelAvailabilitySearchDto searchDto = new HotelAvailabilitySearchDto("1234AbcXXXX", LocalDate.now(), LocalDate.now(), List.of(1, 2, 3));

        when(service.search(mapper.toHotelAvailabilitySearch(searchDto))).thenReturn(searchId);

        mvc.perform(
                MockMvcRequestBuilders.post(HotelAvailabilityController.MAPPING + HotelAvailabilityController.SEARCH_PATH)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(searchDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors", hasSize(2)))
            .andExpect(jsonPath("$.errors[0].field").value("hotelId"))
            //            .andExpect(jsonPath("$.errors[0].code").value("Pattern"))
            //            .andExpect(jsonPath("$.errors[0].defaultMessage").value("must match \"\\d{4}[A-Za-z]{3}\""))
            .andExpect(jsonPath("$.errors[1].field").value("hotelId"))
            //            .andExpect(jsonPath("$.errors[1].code").value("Size"))
            //            .andExpect(jsonPath("$.errors[1].defaultMessage").value("size must be between 0 and 7"))
            .andDo(print())
            .andReturn();
    }

    @Test
    void searchValidationHotelIdPattern() throws Exception {
        final String searchId = "test";
        HotelAvailabilitySearchDto searchDto = new HotelAvailabilitySearchDto("1234567", LocalDate.now(), LocalDate.now(), List.of(1, 2, 3));

        when(service.search(mapper.toHotelAvailabilitySearch(searchDto))).thenReturn(searchId);

        mvc.perform(
                MockMvcRequestBuilders.post(HotelAvailabilityController.MAPPING + HotelAvailabilityController.SEARCH_PATH)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(searchDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].field").value("hotelId"))
            .andExpect(jsonPath("$.errors[0].code").value("Pattern"))
            .andExpect(jsonPath("$.errors[0].defaultMessage").value("must match \"\\d{4}[A-Za-z]{3}\""))
            .andDo(print())
            .andReturn();
    }

    @Test
    void searchValidationCheckInAfterCheckOut() throws Exception {
        final String searchId = "test";
        HotelAvailabilitySearchDto searchDto =
            new HotelAvailabilitySearchDto("1234Abc", LocalDate.now().plusDays(1), LocalDate.now(), List.of(1, 2, 3));

        when(service.search(mapper.toHotelAvailabilitySearch(searchDto))).thenReturn(searchId);

        mvc.perform(
                MockMvcRequestBuilders.post(HotelAvailabilityController.MAPPING + HotelAvailabilityController.SEARCH_PATH)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(searchDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].code").value("ValidDateRange"))
//            .andExpect(jsonPath("$.errors[0].defaultMessage").value("checkOut date must be equal to or greater than before checkIn date"))
            .andDo(print())
            .andReturn();
    }

    @Test
    void searchValidationCheckInPast() throws Exception {
        final String searchId = "test";
        HotelAvailabilitySearchDto searchDto =
            new HotelAvailabilitySearchDto("1234Abc", LocalDate.now().minusDays(1), LocalDate.now(), List.of(1, 2, 3));

        when(service.search(mapper.toHotelAvailabilitySearch(searchDto))).thenReturn(searchId);

        mvc.perform(
                MockMvcRequestBuilders.post(HotelAvailabilityController.MAPPING + HotelAvailabilityController.SEARCH_PATH)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(searchDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].field").value("checkIn"))
            .andExpect(jsonPath("$.errors[0].code").value("FutureOrPresent"))
            .andExpect(jsonPath("$.errors[0].defaultMessage").value("must be a date in the present or in the future"))
            .andDo(print())
            .andReturn();
    }

    @Test
    void searchValidationCheckOutPast() throws Exception {
        final String searchId = "test";
        HotelAvailabilitySearchDto searchDto =
            new HotelAvailabilitySearchDto("1234Abc", LocalDate.now(), LocalDate.now().minusDays(1), List.of(1, 2, 3));

        when(service.search(mapper.toHotelAvailabilitySearch(searchDto))).thenReturn(searchId);

        mvc.perform(
                MockMvcRequestBuilders.post(HotelAvailabilityController.MAPPING + HotelAvailabilityController.SEARCH_PATH)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(searchDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors", hasSize(2)))
            //            .andExpect(jsonPath("$.errors[0].field").value("checkOut"))
            //            .andExpect(jsonPath("$.errors[0].code").value("FutureOrPresent"))
            //            .andExpect(jsonPath("$.errors[0].defaultMessage").value("must be a date in the present or in the future"))
            .andDo(print())
            .andReturn();
    }

    @Test
    void count() throws Exception {
        final String searchId = "test";
        HotelAvailabilityDbSearch search =
            new HotelAvailabilityDbSearch(searchId, "hotelId", LocalDate.now(), LocalDate.now(), List.of(1, 2, 3), 1);
        HotelAvailabilitySearchCountDto countDto = new HotelAvailabilitySearchCountDto(searchId,
            new HotelAvailabilitySearchDto(search.hotelId(), search.checkIn(), search.checkOut(), search.ages()), search.count());

        when(service.findById(searchId)).thenReturn(search);

        mvc.perform(MockMvcRequestBuilders.get(HotelAvailabilityController.MAPPING + HotelAvailabilityController.COUNT_PATH, searchId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(countDto))).andExpect(status().isOk()).andDo(print());

    }

    @Test
    void countNotFound() throws Exception {
        final String searchId = "test";

        when(service.findById(searchId)).thenReturn(null);

        mvc.perform(MockMvcRequestBuilders.get(HotelAvailabilityController.MAPPING + HotelAvailabilityController.COUNT_PATH, searchId)
            .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound()).andDo(print());

    }

    @Test
    void countMethodNotSupportedException() throws Exception {
        final String searchId = "test";

        when(service.findById(searchId)).thenReturn(null);

        mvc.perform(MockMvcRequestBuilders.post(HotelAvailabilityController.MAPPING + HotelAvailabilityController.COUNT_PATH, searchId)
            .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isMethodNotAllowed()).andDo(print());

    }



}