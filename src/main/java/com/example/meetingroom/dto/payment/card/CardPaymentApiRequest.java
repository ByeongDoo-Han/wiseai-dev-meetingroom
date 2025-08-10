package com.example.meetingroom.dto.payment.card;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CardPaymentApiRequest {
    private String cardNumber;
    private BigDecimal amount;
}
