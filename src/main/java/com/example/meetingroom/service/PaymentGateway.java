package com.example.meetingroom.service;

import com.example.meetingroom.dto.payment.PaymentRequest;
import com.example.meetingroom.dto.payment.PaymentResult;
import com.example.meetingroom.entity.PaymentProviderType;

import java.math.BigDecimal;

public interface PaymentGateway {

    boolean supports(PaymentProviderType providerType);

    PaymentResult<?> pay(PaymentRequest request, BigDecimal totalAmount);

    PaymentProviderType getProviderType();
}
