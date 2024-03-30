package com.reservation.domain.model;

import java.util.List;

public record Reservations(long totalItems, List<Reservation> reservations) {

}
