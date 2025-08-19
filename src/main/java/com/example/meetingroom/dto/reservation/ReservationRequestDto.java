package com.example.meetingroom.dto.reservation;

import com.example.meetingroom.aop.ValidReservationTime;
import com.example.meetingroom.aop.ValidReservationTimeRange;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
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
