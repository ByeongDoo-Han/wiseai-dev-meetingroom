package com.example.meetingroom.service;

import com.example.meetingroom.dto.payment.PaymentRequest;
import com.example.meetingroom.dto.payment.PaymentResult;
import com.example.meetingroom.dto.payment.virtual.VirtualAccountApiRequest;
import com.example.meetingroom.dto.payment.virtual.VirtualAccountApiResponse;
import com.example.meetingroom.entity.PaymentProviderType;
import com.example.meetingroom.entity.PaymentStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class VirtualAccountPaymentGateway implements PaymentGateway {

    private final RestTemplate restTemplate;
    private static final String API_URL = "http://mock-pg-c-virtual:8080/v1/virtualaccount";
    private static final String CLIENT_ID = "C_CLIENT_ID";
    private static final String CLIENT_SECRET = "C_CLIENT_SECRET";

    @Override
    public boolean supports(PaymentProviderType providerType) {
        return providerType == PaymentProviderType.VIRTUAL_ACCOUNT;
    }

    @Override
    public PaymentResult pay(PaymentRequest request) {
        VirtualAccountApiRequest apiRequest = VirtualAccountApiRequest.builder()
            .product("회의실 예약")
            .price(request.getAmount())
            .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.set("X-CLIENT-ID", CLIENT_ID);
        headers.set("X-CLIENT-SECRET", CLIENT_SECRET);

        HttpEntity<VirtualAccountApiRequest> entity = new HttpEntity<>(apiRequest, headers);

        VirtualAccountApiResponse response = restTemplate.postForObject(API_URL, entity, VirtualAccountApiResponse.class);

        boolean isSuccess = response != null && "WAITING".equals(response.getStatus());

        String message = "";
        if (isSuccess) {
            message = String.format("가상계좌 발급 성공: %s %s", response.getBank(), response.getAccountNum());
        } else {
            message = "가상계좌 발급 실패";
        }

        return PaymentResult.builder()
            .paymentId(isSuccess ? response.getAccountNum() : null)
            .status(isSuccess ? PaymentStatus.PENDING : PaymentStatus.FAILED)
            .createdAt(LocalDateTime.now())
            .amount(request.getAmount())
            .message(message)
            .build();
    }
}
