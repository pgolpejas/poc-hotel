package com.pocspringbootkafka.hotelavailability.adapters.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "hotel-availability-search")
class HotelAvailabilitySearchEntity {

    @Id
    @Column(name = "search-id", length = 80, nullable = false)
    private String searchId;

    @Column(name = "hotel-id", length = 7, nullable = false)
    private String hotelId;

    @Column(name = "check-in", nullable = false)
    private LocalDate checkIn;

    @Column(name = "check-out", nullable = false)
    private LocalDate checkOut;

//    @Type(ListArrayType.class)
    @Column(name = "ages", columnDefinition = "integer[]", nullable = false)
    private List<Integer> ages;

    @Column(name = "count", nullable = false)
    private Integer count;

    public HotelAvailabilitySearchEntity() {
    }

    public HotelAvailabilitySearchEntity(String searchId,
        String hotelId,
        LocalDate checkIn,
        LocalDate checkOut,
        List<Integer> ages,
        Integer count) {
        this.searchId = searchId;
        this.hotelId = hotelId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.ages = ages;
        this.count = count;
    }


    public String getSearchId() {
        return searchId;
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public List<Integer> getAges() {
        return ages;
    }

    public void setAges(List<Integer> ages) {
        this.ages = ages;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof HotelAvailabilitySearchEntity)) {
            return false;
        }

        HotelAvailabilitySearchEntity that = (HotelAvailabilitySearchEntity) o;
        return Objects.equals(searchId, that.searchId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSearchId());
    }
}

