package com.reservation.domain.utils;

import java.util.List;

public record PageResponse<T>(
    long totalItems, List<T> items) {

}
