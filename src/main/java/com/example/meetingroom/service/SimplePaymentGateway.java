package com.example.meetingroom.service;

import com.example.meetingroom.dto.payment.PaymentRequest;
import com.example.meetingroom.dto.payment.PaymentResult;
import com.example.meetingroom.dto.payment.simple.SimplePaymentApiRequest;
import com.example.meetingroom.entity.PaymentProviderType;
import com.example.meetingroom.entity.PaymentStatus;
import com.example.meetingroom.exception.CustomException;
import com.example.meetingroom.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class SimplePaymentGateway implements PaymentGateway {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String API_URL = "http://mock-pg-b-simple:8080/v1/simplepay";
    private static final String API_KEY = "B_COMPANY_API_KEY";
    private static final String API_SECRET = "B_COMPANY_API_SECRET";


    @Override
    public boolean supports(PaymentProviderType providerType) {
        return providerType == PaymentProviderType.SIMPLE_PAYMENT;
    }

    @Override
    public PaymentResult pay(PaymentRequest request) {
        SimplePaymentApiRequest apiRequest = objectMapper.convertValue(request, SimplePaymentApiRequest.class);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String encodedCredentials = Base64.getEncoder().encodeToString((API_KEY + ":" + API_SECRET).getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + encodedCredentials);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("user_id", apiRequest.getUserId());
        map.add("amount", request.getAmount().toPlainString());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(API_URL, entity, String.class);

        boolean isSuccess = response.getStatusCode() == HttpStatus.OK && response.getBody() != null && response.getBody().startsWith("SUCCESS");
        String paymentId = null;
        if (isSuccess) {
            paymentId = response.getBody().split("|")[1];
        }

        return PaymentResult.builder()
            .paymentId(paymentId)
            .status(isSuccess ? PaymentStatus.SUCCESS : PaymentStatus.FAILED)
            .amount(request.getAmount())
            .createdAt(LocalDateTime.now())
            .message(isSuccess ? "간편결제 성공" : "간편결제 실패")
            .build();
    }
}
