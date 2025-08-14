package com.example.meetingroom.entity;

import com.example.meetingroom.dto.reservation.ReservationResponseDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    public void isStartTimeOnTime(){

    }

    public BigDecimal calculateTotalPrice(BigDecimal pricePerHour) {
        long hours = ChronoUnit.HOURS.between(this.startTime, this.endTime);
        long minutes = ChronoUnit.MINUTES.between(this.startTime, this.endTime) % 60;
        BigDecimal totalHours = BigDecimal.valueOf(hours).add(BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, BigDecimal.ROUND_HALF_UP));
        return pricePerHour.multiply(totalHours);
    }
}
