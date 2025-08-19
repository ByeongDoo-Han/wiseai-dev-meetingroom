package com.example.meetingroom.dto.payment.simple;

import com.example.meetingroom.dto.payment.PaymentRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SimplePaymentRequest extends PaymentRequest {
    private String simplePaymentProvider;
}