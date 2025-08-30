package com.example.meetingroom.entity;

import com.example.meetingroom.dto.meetingRoom.MeetingRoomRequestDto;
import com.example.meetingroom.exception.CustomException;
import com.example.meetingroom.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
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
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false)
    private int capacity;
    @Column(precision = 10, nullable = false)
    private BigDecimal pricePerHour;

    private MeetingRoom(String name, int capacity, BigDecimal pricePerHour) {
        this.name = name;
        this.capacity = validCapacityLargerThanZero(capacity);
        this.pricePerHour = validPricePerHourLargerThanZero(pricePerHour);
    }

    private MeetingRoom(Long id, String name, int capacity, BigDecimal pricePerHour) {
        this.id = id;
        this.name = name;
        this.capacity = validCapacityLargerThanZero(capacity);
        this.pricePerHour = validPricePerHourLargerThanZero(pricePerHour);
    }

    public static MeetingRoom createForTest(final long l, final String name, final int capacity, final BigDecimal pricePerHour) {
        return new MeetingRoom(l, name, capacity, pricePerHour);
    }

    public static MeetingRoom createForTest(final String name, final int capacity, final BigDecimal pricePerHour) {
        return new MeetingRoom(name, capacity, pricePerHour);
    }

    private int validCapacityLargerThanZero(int capacity) {
        if (capacity < 1) {
            throw new CustomException(ErrorCode.MEETING_ROOM_CAPACITY_IS_ZERO);
        }
        return capacity;
    }

    private BigDecimal validPricePerHourLargerThanZero(BigDecimal pricePerHour) {
        if (pricePerHour.compareTo(BigDecimal.ONE) < 0) {
            throw new CustomException(ErrorCode.MEETING_ROOM_CAPACITY_IS_ZERO);
        }
        return pricePerHour;
    }

    public void update(String name, int capacity, BigDecimal pricePerHour) {
        this.name = name;
        this.capacity = validCapacityLargerThanZero(capacity);
        this.pricePerHour = validPricePerHourLargerThanZero(pricePerHour);
    }

    public static MeetingRoom create(final MeetingRoomRequestDto dto) {
        return new MeetingRoom(dto.getName(), dto.getCapacity(), dto.getPricePerHour());
    }

    public static MeetingRoom create(String name, int capacity, BigDecimal pricePerHour) {
        return new MeetingRoom(name, capacity, pricePerHour);
    }
}
