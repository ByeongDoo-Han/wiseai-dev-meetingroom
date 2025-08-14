package com.example.meetingroom.service;

import com.example.meetingroom.dto.payment.PaymentRequest;
import com.example.meetingroom.dto.payment.PaymentResult;
import com.example.meetingroom.entity.Member;
import com.example.meetingroom.entity.Reservation;
import com.example.meetingroom.entity.PaymentStatus;
import com.example.meetingroom.entity.Role;
import com.example.meetingroom.exception.CustomException;
import com.example.meetingroom.exception.ErrorCode;
import com.example.meetingroom.repository.PaymentsRepository;
import com.example.meetingroom.repository.ReservationRepository;
import com.example.meetingroom.util.MemberDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentsServiceTest {

    @InjectMocks
    private PaymentsService paymentsService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private PaymentsRepository paymentsRepository;

    @Mock
    private List<PaymentGateway> paymentGateways;

    @Mock
    private PaymentGateway mockGateway;

    private Member userA;
    private Member userB;
    private Reservation reservation;
    private MemberDetails memberDetailsA;

    @BeforeEach
    void setUp() {
        userA = Member.builder().id(1L).username("userA").role(Role.MEMBER).build();
        userB = Member.builder().id(2L).username("userB").role(Role.MEMBER).build();
        memberDetailsA = new MemberDetails(userA);

        reservation = Reservation.builder()
            .id(1L)
            .member(userA)
            .totalAmount(new BigDecimal("10000"))
            .build();
    }

    @DisplayName("결제 성공 테스트")
    @Test
    void processPayment_Success() {
        // given
        PaymentRequest request = PaymentRequest.builder()
            .reservationId(1L)
            .amount(new BigDecimal("10000"))
            .build();

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        // lenient() is used because this mock might not be called in all test paths
        lenient().when(paymentGateways.stream()).thenReturn(List.of(mockGateway).stream());
        when(mockGateway.supports(any())).thenReturn(true);
        when(mockGateway.pay(any(PaymentRequest.class)))
            .thenReturn(PaymentResult.builder().status(PaymentStatus.SUCCESS).build());

        // when
        assertDoesNotThrow(() -> paymentsService.processPayment(1L, request, memberDetailsA.getUsername()));

        // then
        verify(reservationRepository).findById(1L);
        verify(mockGateway).pay(request);
    }

    @DisplayName("존재하지 않는 예약 결제 실패 테스트")
    @Test
    void processPayment_Fail_ReservationNotFound() {
        // given
        PaymentRequest request = PaymentRequest.builder().reservationId(999L).build();
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            paymentsService.processPayment(999L, request, memberDetailsA.getUsername());
        });
        assertEquals(ErrorCode.RESERVATION_NOT_FOUND, exception.getErrorCode());
    }

    @DisplayName("권한 없는 결제 실패 테스트")
    @Test
    void processPayment_Fail_Unauthorized() {
        // given
        PaymentRequest request = PaymentRequest.builder().reservationId(1L).amount(new BigDecimal("10000")).build();
        MemberDetails memberDetailsB = new MemberDetails(userB);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            paymentsService.processPayment(1L, request, memberDetailsB.getUsername());
        });
        assertEquals(ErrorCode.HANDLE_ACCESS_DENIED, exception.getErrorCode());
    }

    @DisplayName("금액이 일치하지 않는 결제 실패 테스트")
    @Test
    void processPayment_Fail_AmountMismatch() {
        // given
        PaymentRequest request = PaymentRequest.builder()
            .reservationId(1L)
            .amount(new BigDecimal("5000")) // Mismatched amount
            .build();

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            paymentsService.processPayment(1L, request, memberDetailsA.getUsername());
        });
        assertEquals(ErrorCode.PAYMENT_AMOUNT_MISMATCH, exception.getErrorCode());
    }
}
