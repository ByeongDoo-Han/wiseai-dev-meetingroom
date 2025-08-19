package com.example.meetingroom.service;

import com.example.meetingroom.entity.Payment;
import com.example.meetingroom.entity.PaymentStatus;
import com.example.meetingroom.exception.CustomException;
import com.example.meetingroom.exception.ErrorCode;
import com.example.meetingroom.repository.PaymentsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentsService 테스트")
class PaymentsServiceTest {

    @InjectMocks
    private PaymentsService paymentsService;

    @Mock
    private PaymentsRepository paymentsRepository;

    @Test
    @DisplayName("결제 상태 조회 성공")
    void getPaymentStatus_success() {
        // given
        long paymentId = 1L;
        Payment payment = Payment.builder().id(paymentId).status(PaymentStatus.SUCCESS).build();
        given(paymentsRepository.findById(paymentId)).willReturn(Optional.of(payment));

        // when
        PaymentStatus status = paymentsService.getPaymentStatus(paymentId);

        // then
        assertThat(status).isEqualTo(PaymentStatus.SUCCESS);
    }

    @Test
    @DisplayName("결제 상태 조회 실패 - 존재하지 않는 결제")
    void getPaymentStatus_fail_payment_not_found() {
        // given
        long paymentId = 99L;
        given(paymentsRepository.findById(paymentId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentsService.getPaymentStatus(paymentId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PAYMENT_NOT_FOUND);
    }
}
