package com.pocspringbootkafka.shared.constants;

public class AppConstants {

    private AppConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String TOPIC_NAME = "hotel_availability_searches";
    public static final String GROUP_ID = "group_id";
}
