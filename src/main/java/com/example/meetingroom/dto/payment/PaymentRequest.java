package com.example.meetingroom.dto.payment;

import com.example.meetingroom.entity.PaymentProviderType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {

    @NotNull(message = "결제사 타입은 필수입니다.")
    private PaymentProviderType providerType;

}