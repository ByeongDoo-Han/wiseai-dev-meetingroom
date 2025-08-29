package com.example.meetingroom.entity;

import com.example.meetingroom.dto.payment.PaymentRequest;
import com.example.meetingroom.exception.CustomException;
import com.example.meetingroom.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PaymentProviderType paymentProviderType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(unique = true)
    private String externalPaymentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    public void markAsSuccess() {
        if (this.status != PaymentStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_STATUS_TRANSITION);
        }
        this.status = PaymentStatus.SUCCESS;
        this.reservation.updatePaymentStatus(PaymentStatus.SUCCESS);
    }

    public void markAsFailed() {
        if (this.status != PaymentStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_STATUS_TRANSITION);
        }
        this.status = PaymentStatus.FAILED;
        this.reservation.updatePaymentStatus(PaymentStatus.FAILED);
    }

    public void markAsCancelled() {
        if (this.status != PaymentStatus.PENDING && this.status != PaymentStatus.SUCCESS) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_STATUS_TRANSITION);
        }
        this.status = PaymentStatus.CANCELLED;
        this.reservation.updatePaymentStatus(PaymentStatus.CANCELLED);
    }

    public void update(final PaymentStatus status, final String externalPaymentId) {
        this.status = status;
        this.externalPaymentId = externalPaymentId;
    }

    private Payment(final PaymentProviderType paymentProviderType, final BigDecimal amount, final PaymentStatus status, final String externalPaymentId, final Reservation reservation) {
        this.paymentProviderType = paymentProviderType;
        this.amount = amount;
        this.status = status;
        this.externalPaymentId = externalPaymentId;
        this.reservation = reservation;
    }

    public static Payment create(PaymentRequest request, Reservation reservation){
        return new Payment(
            request.getProviderType(),
            reservation.getTotalAmount(),
            PaymentStatus.PENDING,
            null,
            reservation);
    }

    public static Payment createWithReservation(Reservation reservation){
        return new Payment(
            null,
            reservation.getTotalAmount(),
            PaymentStatus.PENDING,
            null,
            reservation
        );
    }
}