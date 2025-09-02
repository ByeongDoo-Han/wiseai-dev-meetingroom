package com.example.meetingroom.service;

import com.example.meetingroom.dto.payment.WebhookRequestDto;
import com.example.meetingroom.entity.Payment;
import com.example.meetingroom.entity.PaymentStatus;
import com.example.meetingroom.exception.CustomException;
import com.example.meetingroom.exception.ErrorCode;
import com.example.meetingroom.repository.PaymentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

@Service
@RequiredArgsConstructor
@EnableAsync
public class PaymentWebhookService {

    private final PaymentsRepository paymentRepository;

    @Value("${payment.webhook.secret-key}")
    private String secretKey;

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    @Transactional
    @Async
    public void processPaymentStatusWebhook(String signature, WebhookRequestDto dto) {
        // 1. HMAC 서명 검증
        validateSignature(signature, dto.toString());

        // 2. 멱등성 체크 (비관적 락과 함께 조회)
        Payment payment = paymentRepository.findByExternalPaymentId(dto.getExternalId())
            .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        // 이미 처리된 요청인지 확인 (상태가 PENDING이 아니면 처리된 것으로 간주)
        if (payment.getStatus() != PaymentStatus.PENDING) {
            // 이미 처리된 웹훅이므로, 로그만 남기고 정상 응답. (중복 처리 방지)
            System.out.println("이미 처리된 웹훅입니다. Payment ID: " + payment.getId());
            return;
        }

        // 3. 비즈니스 로직 처리
        PaymentStatus newStatus = dto.getStatus();

        switch (newStatus) {
            case SUCCESS -> payment.markAsSuccess();
            case FAILED -> payment.markAsFailed();
            case CANCELLED -> payment.markAsCancelled();
            case PENDING -> throw new CustomException(ErrorCode.INVALID_PAYMENT_STATUS_TRANSITION);
            default -> throw new CustomException(ErrorCode.INVALID_PAYMENT_STATUS);
        }
    }

    private void validateSignature(String signature, String rawBody) {
        try {
            String expectedSignature = calculateHmac(rawBody);
            if (!expectedSignature.equals(signature)) {
                throw new CustomException(ErrorCode.INVALID_WEBHOOK_SIGNATURE);
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("HMAC 계산 중 오류 발생", e);
        }
    }

    private String calculateHmac(String data) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        mac.init(secretKeySpec);
        byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return toHexString(hmacBytes);
    }

    private String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }
}