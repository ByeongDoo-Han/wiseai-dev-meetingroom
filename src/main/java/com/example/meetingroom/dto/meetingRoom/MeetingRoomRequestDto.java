package com.example.meetingroom.dto.meetingRoom;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class MeetingRoomRequestDto {
    private final String name;
    private final int capacity;
    private final BigDecimal pricePerHour;

    @Builder
    public MeetingRoomRequestDto(final String name, final int capacity, final BigDecimal pricePerHour) {
        this.name = name;
        this.capacity = capacity;
        this.pricePerHour = pricePerHour;
    }
}
