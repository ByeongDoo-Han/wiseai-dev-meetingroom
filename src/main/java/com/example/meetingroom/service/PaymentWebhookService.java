package com.example.meetingroom.service;

import com.example.meetingroom.dto.payment.PaymentWebhookRequest;
import com.example.meetingroom.entity.Payment;
import com.example.meetingroom.entity.PaymentStatus;
import com.example.meetingroom.exception.CustomException;
import com.example.meetingroom.exception.ErrorCode;
import com.example.meetingroom.repository.PaymentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentWebhookService {

    private final PaymentsRepository paymentRepository;

    @Transactional
    public void processPaymentStatusWebhook(PaymentWebhookRequest request) {
        Payment payment = paymentRepository.findByExternalPaymentId(request.getExternalPaymentId())
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        PaymentStatus newStatus;
        try {
            newStatus = PaymentStatus.valueOf(request.getNewStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_STATUS);
        }

        if (newStatus == PaymentStatus.SUCCESS) {
            payment.markAsSuccess();
        } else if (newStatus == PaymentStatus.FAILED) {
            payment.markAsFailed();
        } else if (newStatus == PaymentStatus.CANCELLED) {
            payment.markAsCancelled();
        } else if (newStatus == PaymentStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_STATUS_TRANSITION);
        }

        // paymentRepository.save(payment); // @Transactional이므로 변경 감지(dirty checking)에 의해 자동 저장
    }
}
