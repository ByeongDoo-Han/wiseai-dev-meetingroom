package com.example.meetingroom.entity;

import com.example.meetingroom.dto.MeetingRoomResponseDto;
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
    public MeetingRoom(Long id, String name, int capacity, BigDecimal pricePerHour){
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.pricePerHour = pricePerHour;
    }

    public MeetingRoomResponseDto toResponseEntity() {
        return MeetingRoomResponseDto.builder()
            .id(getId())
            .name(getName())
            .capacity(getCapacity())
            .pricePerHour(getPricePerHour())
            .build();
    }
}
