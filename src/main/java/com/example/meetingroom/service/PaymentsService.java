package com.example.meetingroom.service;

import com.example.meetingroom.aop.DistributedLock;
import com.example.meetingroom.dto.payment.PaymentRequest;
import com.example.meetingroom.dto.payment.PaymentResult;
import com.example.meetingroom.entity.Payment;
import com.example.meetingroom.entity.PaymentStatus;
import com.example.meetingroom.entity.Reservation;
import com.example.meetingroom.exception.CustomException;
import com.example.meetingroom.exception.ErrorCode;
import com.example.meetingroom.repository.PaymentsRepository;
import com.example.meetingroom.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentsService {

    private static final String RESERVATION_LOCK_PREFIX = "RESERVATION:";
    private final List<PaymentGateway> paymentGateways;
    private final PaymentsRepository paymentRepository;
    private final ReservationRepository reservationRepository;


    @Transactional
    public PaymentStatus getPaymentStatus(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));
        return payment.getStatus();
    }
}