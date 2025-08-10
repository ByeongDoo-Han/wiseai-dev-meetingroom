package com.example.meetingroom.service;

import com.example.meetingroom.dto.payment.PaymentRequest;
import com.example.meetingroom.dto.payment.PaymentResult;
import com.example.meetingroom.dto.payment.card.CardPaymentApiRequest;
import com.example.meetingroom.dto.payment.card.CardPaymentApiResponse;
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
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CardPaymentGateway implements PaymentGateway {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final EncryptionService encryptionService;
    private static final String API_URL = "http://mock-pg-a-card:8080/v1/payments/card";
    private static final String API_KEY = "A_COMPANY_SECRET_KEY_123";

    @Override
    public boolean supports(PaymentProviderType providerType) {
        return providerType == PaymentProviderType.CARD;
    }

    @Override
    public PaymentResult pay(PaymentRequest request) {
        Map<String, Object> detailsMap = objectMapper.convertValue(request.getDetails(), Map.class);
        String cardNumber = (String) detailsMap.get("cardNumber");

        String encryptedCardNumber = encryptionService.encrypt(cardNumber);

        CardPaymentApiRequest apiRequest = new CardPaymentApiRequest(encryptedCardNumber, request.getAmount());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-API-KEY", API_KEY);

        HttpEntity<CardPaymentApiRequest> entity = new HttpEntity<>(apiRequest, headers);

        CardPaymentApiResponse response = restTemplate.postForObject(API_URL, entity, CardPaymentApiResponse.class);

        boolean isSuccess = response != null && "00".equals(response.getResultCode());

        return PaymentResult.builder()
            .paymentId(isSuccess ? response.getTransactionId() : null)
            .status(isSuccess ? PaymentStatus.SUCCESS : PaymentStatus.FAILED)
            .amount(request.getAmount())
            .message(isSuccess ? "카드 결제 성공" : "카드 결제 실패: " + (response != null ? response.getMessage() : "응답 없음"))
            .createdAt(LocalDateTime.now())
            .build();
    }
}