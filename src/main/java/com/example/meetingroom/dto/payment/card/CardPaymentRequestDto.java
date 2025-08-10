package com.example.meetingroom.dto.payment.card;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CardPaymentRequestDto {
    private String cardNumber;
    private BigDecimal totalAmount;

    @Builder
    public CardPaymentRequestDto(String cardNumber, BigDecimal totalAmount) {
        this.cardNumber = cardNumber;
        this.totalAmount = totalAmount;
    }
}
