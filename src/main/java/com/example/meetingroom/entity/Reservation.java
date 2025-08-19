package com.example.meetingroom.entity;

import com.example.meetingroom.exception.CustomException;
import com.example.meetingroom.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_room_id", nullable = false)
    private MeetingRoom meetingRoom;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void updatePaymentStatus(PaymentStatus newStatus) {
        this.paymentStatus = newStatus;
    }

    public void update(LocalDateTime newStartTime, LocalDateTime newEndTime, BigDecimal newTotalAmount, MeetingRoom newMeetingRoom) {
        this.startTime = newStartTime;
        this.endTime = newEndTime;
        this.totalAmount = newTotalAmount;
        this.meetingRoom = newMeetingRoom;
    }

    public void valid(){
        validStartTimeMinuteZeroOrThirty();
        validEndTimeMinuteZeroOrThirty();
        validStartTimeIsBeforeThanEndTime();
        validIsNotZero();
    }

    public void validStartTimeIsBeforeThanEndTime(){
        if(this.startTime.isAfter(this.endTime)){
            throw new CustomException(ErrorCode.RESERVATION_START_TIME_IS_AFTER_THAN_END_TIME);
        }
    }

    public void validStartTimeMinuteZeroOrThirty(){
        if(this.startTime.getMinute() != 0 && this.startTime.getMinute()!=30){
            throw new CustomException(ErrorCode.INVALID_RESERVATION_TIME);
        }
    }

    public void validEndTimeMinuteZeroOrThirty(){
        if(this.endTime.getMinute() != 0 && this.endTime.getMinute()!=30){
            throw new CustomException(ErrorCode.INVALID_RESERVATION_TIME);
        }
    }

    public void validIsNotZero(){
        if(endTime.isEqual(startTime)){
            throw new CustomException(ErrorCode.MEETING_ROOM_TIME_IS_ZERO);
        }
    }

    public BigDecimal calculateTotalPrice(BigDecimal pricePerHour) {
        long hours = ChronoUnit.HOURS.between(this.startTime, this.endTime);
        long minutes = ChronoUnit.MINUTES.between(this.startTime, this.endTime) % 60;
        BigDecimal totalHours = BigDecimal.valueOf(hours).add(BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, BigDecimal.ROUND_HALF_UP));
        return pricePerHour.multiply(totalHours);
    }

    @Builder
    public Reservation(final Long id, final Member member, final MeetingRoom meetingRoom, final LocalDateTime startTime, final LocalDateTime endTime, final BigDecimal totalAmount, final PaymentStatus paymentStatus) {
        this.id = id;
        this.member = member;
        this.meetingRoom = meetingRoom;
        this.startTime = startTime;
        this.endTime = endTime;
        this.paymentStatus = paymentStatus;
        valid();
        this.totalAmount = calculateTotalPrice(meetingRoom.getPricePerHour());
    }
}
