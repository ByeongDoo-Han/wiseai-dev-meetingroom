package com.example.meetingroom.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class MeetingRoomResponseDto {
    private final Long id;
    private final String name;
    private final int capacity;
    private final BigDecimal pricePerHour;

    @Builder
    public MeetingRoomResponseDto(final Long id, final String name, final int capacity, final BigDecimal pricePerHour) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.pricePerHour = pricePerHour;
    }
}
