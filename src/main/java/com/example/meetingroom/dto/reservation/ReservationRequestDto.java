package com.example.meetingroom.dto.reservation;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReservationRequestDto {

    @NotNull
    private Long meetingRoomId;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @Builder
    public ReservationRequestDto(Long meetingRoomId, LocalDateTime startTime, LocalDateTime endTime) {
        this.meetingRoomId = meetingRoomId;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
