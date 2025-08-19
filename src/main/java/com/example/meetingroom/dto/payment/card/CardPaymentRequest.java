package com.example.meetingroom.dto.payment.card;

import com.example.meetingroom.dto.payment.PaymentRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CardPaymentRequest extends PaymentRequest {
    private String cardNumber;
    private String cardCvc;
    private String cardExpiryDate;
}