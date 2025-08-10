package com.example.meetingroom.service;

import com.example.meetingroom.dto.payment.PaymentRequest;
import com.example.meetingroom.dto.payment.PaymentResult;
import com.example.meetingroom.entity.PaymentProviderType;

public interface PaymentGateway {

    boolean supports(PaymentProviderType providerType);

    PaymentResult pay(PaymentRequest request);
}
