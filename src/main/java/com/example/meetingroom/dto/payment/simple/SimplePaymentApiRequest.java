package com.example.meetingroom.dto.payment.simple;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimplePaymentApiRequest {
    private String userId;
    private BigDecimal amount;
}
