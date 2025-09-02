package com.example.meetingroom.dto.payment;

import com.example.meetingroom.entity.PaymentProviderType;
import com.example.meetingroom.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class WebhookRequestDto {
    private final String externalId;
    private final PaymentStatus status;
    private final Long paymentId;
    private final BigDecimal amount;
    private final Long memberId;
    private final PaymentProviderType method;
}
