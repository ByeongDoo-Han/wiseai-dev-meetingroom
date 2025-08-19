package com.example.meetingroom.service;

import com.example.meetingroom.dto.payment.PaymentRequest;
import com.example.meetingroom.dto.payment.PaymentResult;
import com.example.meetingroom.dto.payment.card.CardPaymentApiResponse;
import com.example.meetingroom.dto.payment.card.CardPaymentRequest;
import com.example.meetingroom.entity.PaymentProviderType;
import com.example.meetingroom.entity.PaymentStatus;
import com.example.meetingroom.exception.CustomException;
import com.example.meetingroom.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CardPaymentGateway implements PaymentGateway {

    private final RestTemplate restTemplate;
    private final EncryptionService encryptionService;
    private static final String API_URL = "http://mock-pg-a-card:8080/v1/payments/card";
    private static final String API_KEY = "A_COMPANY_SECRET_KEY_123";

    @Override
    public PaymentProviderType getProviderType() {
        return PaymentProviderType.CARD;
    }

    @Override
    public boolean supports(PaymentProviderType providerType) {
        return providerType == PaymentProviderType.CARD;
    }

    @Override
    public PaymentResult<CardPaymentApiResponse> pay(PaymentRequest request, BigDecimal totalAmount) {
        if (request instanceof CardPaymentRequest cardPaymentRequest) {
            String encryptedCardNumber = encryptionService.encrypt(cardPaymentRequest.getCardNumber());

            CardPaymentRequest apiRequest = CardPaymentRequest.builder()
                .cardNumber(encryptedCardNumber)
                .cardCvc(cardPaymentRequest.getCardCvc())
                .cardExpiryDate(cardPaymentRequest.getCardExpiryDate())
                .build();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-API-KEY", API_KEY);

            HttpEntity<CardPaymentRequest> entity = new HttpEntity<>(apiRequest, headers);

            CardPaymentApiResponse response = restTemplate.postForObject(API_URL, entity, CardPaymentApiResponse.class);

            boolean isSuccess = "Success".equals(response.getResultCode());

            return PaymentResult.<CardPaymentApiResponse>builder()
                .paymentId(isSuccess ? response.getTransactionId() : null)
                .status(isSuccess ? PaymentStatus.SUCCESS : PaymentStatus.FAILED)
                .amount(totalAmount)
                .message(isSuccess ? "카드 결제 성공" : "카드 결제 실패: " + (response != null ? response.getMessage() : "응답 없음"))
                .details(response)
                .build();
        }else {
            throw new CustomException(ErrorCode.PAYMENT_NOT_FOUND);
        }
    }


}