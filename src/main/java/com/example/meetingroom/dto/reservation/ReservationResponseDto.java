package com.example.meetingroom.dto.reservation;

import com.example.meetingroom.entity.PaymentStatus;
import com.example.meetingroom.entity.Reservation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationResponseDto {
    private final Long id;
    private final String username;
    private final String meetingRoomName;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final BigDecimal totalAmount;
    private final PaymentStatus paymentStatus;

    public static ReservationResponseDto from(Reservation reservation) {
        return new ReservationResponseDto(
            reservation.getId(),
            reservation.getMember().getUsername(),
            reservation.getMeetingRoom().getName(),
            reservation.getStartTime(),
            reservation.getEndTime(),
            reservation.getTotalAmount(),
            reservation.getPaymentStatus()
        );
    }
}
