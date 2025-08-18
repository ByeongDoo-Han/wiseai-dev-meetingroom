package com.example.meetingroom.dto.payment;

import com.example.meetingroom.entity.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResult<T> {
    private String paymentId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private T details;
}