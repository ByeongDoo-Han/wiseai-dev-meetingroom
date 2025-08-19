package com.example.meetingroom.entity;

import com.example.meetingroom.exception.CustomException;
import com.example.meetingroom.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentProviderType paymentProviderType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = true, unique = true)
    private String externalPaymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

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

    public void markAsPending() {
        if (this.status != null) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_STATUS_TRANSITION);
        }
        this.status = PaymentStatus.PENDING;
    }

    public void update(final PaymentStatus status, final String externalPaymentId) {
        this.status = status;
        this.externalPaymentId = externalPaymentId;
    }
}