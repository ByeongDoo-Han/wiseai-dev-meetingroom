package com.example.meetingroom.dto.payment.card;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CardPaymentApiResponse {
    private String transactionId;
    private String resultCode;
    private String message;
}
