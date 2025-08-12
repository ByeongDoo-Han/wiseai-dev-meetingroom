package com.example.meetingroom.entity;

import com.example.meetingroom.dto.meetingRoom.MeetingRoomResponseDto;
import com.example.meetingroom.exception.CustomException;
import com.example.meetingroom.exception.ErrorCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeetingRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(nullable = false, unique = true)
    private String name;
    @NotNull
    @Column(nullable = false)
    private int capacity;
    @NotNull
    @Column(precision = 10, nullable = false)
    private BigDecimal pricePerHour;

    @Builder
    public MeetingRoom(Long id, String name, int capacity, BigDecimal pricePerHour) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.pricePerHour = pricePerHour;
    }

    public void isCapacityLargerThanZero(){
        if(this.capacity<1){
            throw new CustomException(ErrorCode.MEETING_ROOM_CAPACITY_IS_ZERO);
        }
    }

    public void isPricePerHourLargerThanZero(){
        if(this.pricePerHour.compareTo(BigDecimal.ONE) < 0){
            throw new CustomException(ErrorCode.MEETING_ROOM_CAPACITY_IS_ZERO);
        }
    }

    public MeetingRoomResponseDto toResponseEntity() {
        return MeetingRoomResponseDto.builder()
            .id(getId())
            .name(getName())
            .capacity(getCapacity())
            .pricePerHour(getPricePerHour())
            .build();
    }

    public void update(
        String name, int capacity, BigDecimal pricePerHour
    ) {
        this.name = name;
        this.capacity = capacity;
        this.pricePerHour = pricePerHour;
    }
}
