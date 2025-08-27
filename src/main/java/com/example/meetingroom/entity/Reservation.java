package com.example.meetingroom.entity;

import com.example.meetingroom.dto.reservation.ReservationRequestDto;
import com.example.meetingroom.exception.CustomException;
import com.example.meetingroom.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Reservation extends BaseTimeEntity {

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

    public void updatePaymentStatus(PaymentStatus newStatus) {
        this.paymentStatus = newStatus;
    }

    public void update(LocalDateTime newStartTime, LocalDateTime newEndTime, MeetingRoom newMeetingRoom) {
        this.startTime = newStartTime;
        this.endTime = newEndTime;
        valid(newStartTime, newEndTime);
        this.meetingRoom = newMeetingRoom;
        this.totalAmount = calculateTotalPrice(newMeetingRoom.getPricePerHour());
    }

    private void valid(LocalDateTime startTime, LocalDateTime endTime) {
        validStartAndEndTimeMinuteZeroOrThirty(startTime, endTime);
        validStartTimeIsBeforeThanEndTime(startTime, endTime);
        validIsNotZero(startTime, endTime);
    }

    private void validStartTimeIsBeforeThanEndTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new CustomException(ErrorCode.RESERVATION_START_TIME_IS_AFTER_THAN_END_TIME);
        }
    }

    private void validStartAndEndTimeMinuteZeroOrThirty(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.getMinute() != 0 && startTime.getMinute() != 30 &&
            endTime.getMinute() != 0 && endTime.getMinute() != 30) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_TIME);
        }
    }

    private void validIsNotZero(LocalDateTime startTime, LocalDateTime endTime) {
        if (endTime.isEqual(startTime)) {
            throw new CustomException(ErrorCode.MEETING_ROOM_TIME_IS_ZERO);
        }
    }

    private BigDecimal calculateTotalPrice(BigDecimal pricePerHour) {
        long hours = ChronoUnit.HOURS.between(this.startTime, this.endTime);
        long minutes = ChronoUnit.MINUTES.between(this.startTime, this.endTime) % 60;
        BigDecimal totalHours = BigDecimal.valueOf(hours).add(BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP));
        return pricePerHour.multiply(totalHours);
    }

    private Reservation(final Member member, final MeetingRoom meetingRoom, final LocalDateTime startTime, final LocalDateTime endTime) {
        this.member = member;
        this.meetingRoom = meetingRoom;
        this.startTime = startTime;
        this.endTime = endTime;
        this.paymentStatus = PaymentStatus.PENDING;
        valid(startTime, endTime);
        this.totalAmount = calculateTotalPrice(meetingRoom.getPricePerHour());
    }

    public static Reservation create(ReservationRequestDto dto, Member member, MeetingRoom meetingRoom) {
        return new Reservation(member, meetingRoom, dto.getStartTime(), dto.getEndTime());
    }
}
