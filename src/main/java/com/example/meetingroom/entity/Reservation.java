package com.example.meetingroom.entity;

import com.example.meetingroom.dto.reservation.ReservationResponse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(precision = 10, nullable = false)
    private BigDecimal totalAmount;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "meeting_room_id")
    private MeetingRoom meetingRoom;

    @Builder
    public Reservation(final Long id, final LocalDateTime startTime, final LocalDateTime endTime, final PaymentStatus paymentStatus, final BigDecimal totalAmount, final Member member, final MeetingRoom meetingRoom) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.paymentStatus = paymentStatus;
        this.totalAmount = totalAmount;
        this.member = member;
        this.meetingRoom = meetingRoom;
    }

    public void update(LocalDateTime startTime, LocalDateTime endTime, BigDecimal totalAmount, MeetingRoom meetingRoom){
            this.startTime = startTime;
            this.endTime = endTime;
            this.totalAmount = totalAmount;
            this.meetingRoom = meetingRoom;
    }

    public ReservationResponse toReservationResponseEntity(){
        return ReservationResponse.builder()
            .id(id)
            .memberId(member.getId())
            .meetingRoomId(meetingRoom.getId())
            .startTime(startTime)
            .endTime(endTime)
            .totalAmount(totalAmount)
            .paymentStatus(PaymentStatus.PENDING)
            .build();
    }

}
