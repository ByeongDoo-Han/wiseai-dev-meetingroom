package com.example.meetingroom.service;

import com.example.meetingroom.dto.payment.PaymentWebhookRequest;
import com.example.meetingroom.entity.*;
import com.example.meetingroom.exception.CustomException;
import com.example.meetingroom.exception.ErrorCode;
import com.example.meetingroom.repository.PaymentsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private MeetingRoom room1;
    private Member member;

    @BeforeEach
    void setUp() {
        room1 = MeetingRoom.createForTest(1L, "회의실 A", 10, new BigDecimal("10000"));
        member = Member.createForTest(1L, "testuser", "password", Role.MEMBER);

        reservation = Reservation.createForTest(1L, member, room1,
            LocalDateTime.of(2025, 8, 10, 10, 0),
            LocalDateTime.of(2025, 8, 10, 11, 0),
            new BigDecimal("10000.00"));

        payment = Payment.createForTest(1L, "ext_payment_id_123", PaymentStatus.PENDING, reservation);
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

    @DisplayName("웹훅 처리 실패 - 결제 정보 없음")
    @Test
    void processWebhook_PaymentNotFound() {
        // given
        PaymentWebhookRequest request = new PaymentWebhookRequest("non_existent_id", "SUCCESS");
        when(paymentsRepository.findByExternalPaymentId("non_existent_id")).thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            paymentWebhookService.processPaymentStatusWebhook(request);
        });
        assertEquals(ErrorCode.PAYMENT_NOT_FOUND, exception.getErrorCode());
        verify(paymentsRepository).findByExternalPaymentId("non_existent_id");
    }

    @DisplayName("웹훅 처리 실패 - 유효하지 않은 상태 문자열")
    @Test
    void processWebhook_InvalidStatusString() {
        // given
        PaymentWebhookRequest request = new PaymentWebhookRequest("ext_payment_id_123", "UNKNOWN_STATUS");
        when(paymentsRepository.findByExternalPaymentId("ext_payment_id_123")).thenReturn(Optional.of(payment));

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            paymentWebhookService.processPaymentStatusWebhook(request);
        });
        assertEquals(ErrorCode.INVALID_PAYMENT_STATUS, exception.getErrorCode());
        verify(paymentsRepository).findByExternalPaymentId("ext_payment_id_123");
    }

    @DisplayName("웹훅 처리 실패 - PENDING으로의 유효하지 않은 상태 전환")
    @Test
    void processWebhook_InvalidTransitionToPending() {
        // given
        PaymentWebhookRequest request = new PaymentWebhookRequest("ext_payment_id_123", "PENDING");
        when(paymentsRepository.findByExternalPaymentId("ext_payment_id_123")).thenReturn(Optional.of(payment));

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            paymentWebhookService.processPaymentStatusWebhook(request);
        });
        assertEquals(ErrorCode.INVALID_PAYMENT_STATUS_TRANSITION, exception.getErrorCode());
        verify(paymentsRepository).findByExternalPaymentId("ext_payment_id_123");
    }
}
