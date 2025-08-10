package com.example.meetingroom.service;

import com.example.meetingroom.dto.payment.PaymentRequest;
import com.example.meetingroom.dto.payment.PaymentResult;
import com.example.meetingroom.entity.Payment;
import com.example.meetingroom.entity.PaymentStatus;
import com.example.meetingroom.entity.Reservation;
import com.example.meetingroom.exception.CustomException;
import com.example.meetingroom.exception.ErrorCode;
import com.example.meetingroom.repository.PaymentsRepository;
import com.example.meetingroom.repository.ReservationRepository;
import com.example.meetingroom.util.MemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentsService {

    private final List<PaymentGateway> paymentGateways;
    private final PaymentsRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public PaymentResult processPayment(Long id, PaymentRequest request, String username) {
        // 1. 예약 정보 조회
        Reservation reservation = reservationRepository.findById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        // 2. 예약 소유권 확인
        if (username == null) {
            throw new CustomException(ErrorCode.HANDLE_ACCESS_DENIED);
        }
        if (!reservation.getMember().getUsername().equals(username)) {
            throw new CustomException(ErrorCode.HANDLE_ACCESS_DENIED);
        }

        // 3. 결제 금액 일치 여부 확인
        if (reservation.getTotalAmount().compareTo(request.getAmount()) != 0) {
            throw new CustomException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }

        // 4. 요청에 맞는 결제 게이트웨이 탐색
        PaymentGateway gateway = paymentGateways.stream()
            .filter(pg -> pg.supports(request.getProviderType()))
            .findFirst()
            .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_PROVIDER_NOT_FOUND));

        // 5. 게이트웨이를 통해 결제 실행
        PaymentResult result = gateway.pay(request);

        // 6. 결제 결과 저장
        Payment payment = Payment.builder()
            .paymentProviderType(request.getProviderType())
            .amount(result.getAmount())
            .status(result.getStatus())
            .externalPaymentId(result.getPaymentId())
            .reservation(reservation)
            .build();
        paymentRepository.save(payment);

        // 7. 예약 상태 업데이트
        reservation.updatePaymentStatus(result.getStatus());

        return result;
    }

    public PaymentStatus getPaymentStatus(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));
        return payment.getStatus();
    }
}