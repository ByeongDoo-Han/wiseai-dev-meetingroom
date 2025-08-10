package com.example.meetingroom.dto.payment;

import com.example.meetingroom.entity.PaymentStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentWebhookRequest {
    private String externalPaymentId;
    private String newStatus;
}
