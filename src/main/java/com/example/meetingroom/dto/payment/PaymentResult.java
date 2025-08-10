package com.example.meetingroom.dto.payment;

import com.example.meetingroom.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResult {
    private String paymentId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String message;
    private LocalDateTime createdAt;
}