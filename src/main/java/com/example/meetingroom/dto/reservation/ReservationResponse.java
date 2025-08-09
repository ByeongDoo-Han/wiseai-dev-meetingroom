package com.example.meetingroom.dto.reservation;

import com.example.meetingroom.entity.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class ReservationResponse {
    private Long id;
    private Long memberId;
    private Long meetingRoomId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal totalAmount;
    private PaymentStatus paymentStatus;
}
