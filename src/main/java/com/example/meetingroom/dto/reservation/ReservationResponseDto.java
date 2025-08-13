package com.example.meetingroom.dto.reservation;

import com.example.meetingroom.entity.PaymentStatus;
import com.example.meetingroom.entity.Reservation;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class ReservationResponseDto {
    private Long id;
    private String username; // 추가
    private String meetingRoomName; // 추가
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal totalAmount;
    private PaymentStatus paymentStatus;

    @Builder
    public ReservationResponseDto(final Long id, final String username, final String meetingRoomName, final LocalDateTime startTime, final LocalDateTime endTime, final BigDecimal totalAmount, final PaymentStatus paymentStatus) {
        this.id = id;
        this.username = username;
        this.meetingRoomName = meetingRoomName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
    }

    public static ReservationResponseDto from(Reservation reservation) {
        return ReservationResponseDto.builder()
            .id(reservation.getId())
            .startTime(reservation.getStartTime())
            .endTime(reservation.getEndTime())
            .paymentStatus(reservation.getPaymentStatus())
            .meetingRoomName(reservation.getMeetingRoom().getName())
            .totalAmount(reservation.getTotalAmount())
            .username(reservation.getMember().getUsername())
            .build();
    }
}
