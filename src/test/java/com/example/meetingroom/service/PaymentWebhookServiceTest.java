package com.example.meetingroom.service;

import com.example.meetingroom.dto.payment.PaymentWebhookRequest;
import com.example.meetingroom.entity.Payment;
import com.example.meetingroom.entity.PaymentStatus;
import com.example.meetingroom.entity.Reservation;
import com.example.meetingroom.repository.PaymentsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentWebhookServiceTest {

    @InjectMocks
    private PaymentWebhookService paymentWebhookService;

    @Mock
    private PaymentsRepository paymentsRepository;

    private Reservation reservation;
    private Payment payment;

    @BeforeEach
    void setUp() {
        reservation = Reservation.builder()
                .id(1L)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        payment = Payment.builder()
                .id(1L)
                .externalPaymentId("ext_payment_id_123")
                .status(PaymentStatus.PENDING)
                .reservation(reservation)
                .build();
    }

    @DisplayName("결제 성공 웹훅 처리 테스트")
    @Test
    void processWebhook_Success() {
        // given
        PaymentWebhookRequest request = new PaymentWebhookRequest("ext_payment_id_123", "SUCCESS");
        when(paymentsRepository.findByExternalPaymentId("ext_payment_id_123")).thenReturn(Optional.of(payment));

        // when
        paymentWebhookService.processPaymentStatusWebhook(request);

        // then
        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
        assertEquals(PaymentStatus.SUCCESS, reservation.getPaymentStatus());
        verify(paymentsRepository).findByExternalPaymentId("ext_payment_id_123");
    }

    @DisplayName("결제 실패 웹훅 처리 테스트")
    @Test
    void processWebhook_Fail() {
        // given
        PaymentWebhookRequest request = new PaymentWebhookRequest("ext_payment_id_123", "FAILED");
        when(paymentsRepository.findByExternalPaymentId("ext_payment_id_123")).thenReturn(Optional.of(payment));

        // when
        paymentWebhookService.processPaymentStatusWebhook(request);

        // then
        assertEquals(PaymentStatus.FAILED, payment.getStatus());
        assertEquals(PaymentStatus.FAILED, reservation.getPaymentStatus());
        verify(paymentsRepository).findByExternalPaymentId("ext_payment_id_123");
    }

    @DisplayName("결제 취소 웹훅 처리 테스트")
    @Test
    void processWebhook_CancelSuccess() {
        // given
        PaymentWebhookRequest request = new PaymentWebhookRequest("ext_payment_id_123", "CANCELLED");
        when(paymentsRepository.findByExternalPaymentId("ext_payment_id_123")).thenReturn(Optional.of(payment));

        // when
        paymentWebhookService.processPaymentStatusWebhook(request);

        // then
        assertEquals(PaymentStatus.CANCELLED, payment.getStatus());
        assertEquals(PaymentStatus.CANCELLED, reservation.getPaymentStatus());
        verify(paymentsRepository).findByExternalPaymentId("ext_payment_id_123");
    }
}