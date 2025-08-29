package com.example.meetingroom.repository;

import com.example.meetingroom.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentsRepository extends JpaRepository<Payment, Long> {
    // 웹훅 처리를 위해 externalPaymentId로 Payment를 조회하는 메소드 추가
    Optional<Payment> findByExternalPaymentId(String externalPaymentId);

    Optional<Payment> findByReservationId(Long reservationId);
}
