package com.example.meetingroom.dto.meetingRoom;

import com.example.meetingroom.entity.MeetingRoom;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class MeetingRoomResponseDto {
    private final Long id;
    private final String name;
    private final int capacity;
    private final BigDecimal pricePerHour;

    private MeetingRoomResponseDto(final Long id, final String name, final int capacity, final BigDecimal pricePerHour) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.pricePerHour = pricePerHour;
    }

    public static MeetingRoomResponseDto fromEntity(MeetingRoom meetingRoom){
        return new MeetingRoomResponseDto(
            meetingRoom.getId(),
            meetingRoom.getName(),
            meetingRoom.getCapacity(),
            meetingRoom.getPricePerHour());
    }
}
