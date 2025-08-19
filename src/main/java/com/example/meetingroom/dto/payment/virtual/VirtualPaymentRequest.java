package com.example.meetingroom.dto.payment.virtual;

import com.example.meetingroom.dto.payment.PaymentRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class VirtualPaymentRequest extends PaymentRequest {
    private String bankName;
}