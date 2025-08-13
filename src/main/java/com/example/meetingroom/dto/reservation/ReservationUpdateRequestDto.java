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
@ValidReservationTimeRange
public class ReservationUpdateRequestDto {

    @NotNull
    private Long meetingRoomId;

    @NotNull
    private Long reservationId;

    @NotNull
    @Future(message = "예약 시간은 현재 시간 이후여야 합니다.")
    @ValidReservationTime
    private LocalDateTime startTime;

    @NotNull
    @Future(message = "예약 종료 시간은 현재 시간 이후여야 합니다.")
    @ValidReservationTime
    private LocalDateTime endTime;

    @Builder
    public ReservationUpdateRequestDto(final Long meetingRoomId, final Long reservationId, final LocalDateTime startTime, final LocalDateTime endTime) {
        this.meetingRoomId = meetingRoomId;
        this.reservationId = reservationId;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
