package com.example.meetingroom.config;

import com.example.meetingroom.entity.*;
import com.example.meetingroom.repository.MeetingRoomRepository;
import com.example.meetingroom.repository.MemberRepository;
import com.example.meetingroom.repository.PaymentProviderRepository;
import com.example.meetingroom.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final MeetingRoomRepository meetingRoomRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentProviderRepository paymentProviderRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (memberRepository.count() == 0) {
            System.out.println("초기 데이터 로드 시작...");

            PaymentProvider card = PaymentProvider.create(
                "병두카드",
                "http://meetingroom-card/payments/card",
                "bd_payments_key_abcd1234",
                PaymentProviderType.CARD
            );
            paymentProviderRepository.save(card);

            PaymentProvider simplePay = PaymentProvider.create(
                "병두 간편결제",
                "http://meetingroom-simple/payments/simple",
                "bd_user:bd_password",
                PaymentProviderType.SIMPLE_PAYMENT
            );
            paymentProviderRepository.save(simplePay);

            PaymentProvider virtualPay = PaymentProvider.create(
                "병두 가상계좌 결제",
                "http://meetingroom-virtual/payments/virtual",
                """
                    {
                        "clientId" : "CLIENT_ID",
                        "clientPassword" : "CLIENT_PASSWORD"
                    }
                """,
                PaymentProviderType.VIRTUAL_ACCOUNT
            );
            paymentProviderRepository.save(virtualPay);

            Member adminMember = Member.create("admin", passwordEncoder.encode("adminpass"), Role.ADMIN);
            memberRepository.save(adminMember);

            Member userMember = Member.create("testuser", passwordEncoder.encode("testpass"), Role.MEMBER);
            memberRepository.save(userMember);

            Member member = Member.create("string", passwordEncoder.encode("string"), Role.MEMBER);
            memberRepository.save(member);
            System.out.println("사용자 데이터 로드 완료.");

            MeetingRoom roomA = MeetingRoom.create("회의실 A", 10, new BigDecimal("15000"));
            meetingRoomRepository.save(roomA);

            MeetingRoom roomB = MeetingRoom.create("회의실 B", 5, new BigDecimal("10000"));
            meetingRoomRepository.save(roomB);

            System.out.println("회의실 데이터 로드 완료.");

            Reservation sampleReservation = Reservation.create(userMember, roomA,
                LocalDateTime.of(2025, 9, 1, 10, 0),
                LocalDateTime.of(2025, 9, 1, 11, 0));
            reservationRepository.save(sampleReservation);

            Reservation sampleReservation2 = Reservation.create(member, roomA,
                LocalDateTime.of(2025, 9, 2, 10, 0),
                LocalDateTime.of(2025, 9, 2, 11, 0));
            reservationRepository.save(sampleReservation2);

            System.out.println("샘플 예약 데이터 로드 완료.");
            System.out.println("초기 데이터 로드 완료.");
        } else {
            System.out.println("데이터가 이미 존재하여 초기 데이터 로드를 건너뜁니다.");
        }
    }
}