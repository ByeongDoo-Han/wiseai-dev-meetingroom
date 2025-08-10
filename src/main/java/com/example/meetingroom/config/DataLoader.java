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

            PaymentProvider card = PaymentProvider.builder()
                .name("병두카드")
                .providerType(PaymentProviderType.CARD)
                .apiEndpoint("http://meetingroom-card/payments/card")
                .authInfo("bd_payments_key_abcd1234")
                .build();

            paymentProviderRepository.save(card);

            PaymentProvider simplePay = PaymentProvider.builder()
                .name("병두 간편결제")
                .providerType(PaymentProviderType.SIMPLE_PAYMENT)
                .apiEndpoint("http://meetingroom-simple/payments/simple")
                .authInfo("bd_user:bd_password")
                .build();
            paymentProviderRepository.save(simplePay);

            PaymentProvider virtualPay = PaymentProvider.builder()
                .name("병두 가상계좌 결제")
                .providerType(PaymentProviderType.VIRTUAL_ACCOUNT)
                .apiEndpoint("http://meetingroom-virtual/payments/virtual")
                .authInfo(
                    """
                        {
                            "clientId" : "CLIENT_ID",
                            "clientPassword" : "CLIENT_PASSWORD"                            
                        }
                    """
                )
                .build();
            paymentProviderRepository.save(virtualPay);

            Member adminMember = Member.builder()
                .username("admin")
                .password(passwordEncoder.encode("adminpass"))
                .role(Role.ADMIN)
                .build();
            memberRepository.save(adminMember);

            Member userMember = Member.builder()
                .username("testuser")
                .password(passwordEncoder.encode("testpass"))
                .role(Role.MEMBER)
                .build();
            memberRepository.save(userMember);

            Member member = Member.builder()
                .username("string")
                .password(passwordEncoder.encode("string"))
                .role(Role.MEMBER)
                .build();
            memberRepository.save(member);
            System.out.println("사용자 데이터 로드 완료.");

            MeetingRoom roomA = MeetingRoom.builder()
                .name("회의실 A")
                .capacity(10)
                .pricePerHour(new BigDecimal("15000"))
                .build();
            meetingRoomRepository.save(roomA);

            MeetingRoom roomB = MeetingRoom.builder()
                .name("회의실 B")
                .capacity(5)
                .pricePerHour(new BigDecimal("10000"))
                .build();
            meetingRoomRepository.save(roomB);

            System.out.println("회의실 데이터 로드 완료.");

            Reservation sampleReservation = Reservation.builder()
                .member(userMember)
                .meetingRoom(roomA)
                .paymentStatus(PaymentStatus.PENDING)
                .startTime(LocalDateTime.of(2025, 9, 1, 10, 0))
                .endTime(LocalDateTime.of(2025, 9, 1, 11, 0))
                .totalAmount(new BigDecimal("15000"))
                .build();
            reservationRepository.save(sampleReservation);

            Reservation sampleReservation2 = Reservation.builder()
                .member(member)
                .meetingRoom(roomA)
                .paymentStatus(PaymentStatus.PENDING)
                .startTime(LocalDateTime.of(2025, 9, 2, 10, 0))
                .endTime(LocalDateTime.of(2025, 9, 2, 11, 0))
                .totalAmount(new BigDecimal("15000"))
                .build();
            reservationRepository.save(sampleReservation2);

            System.out.println("샘플 예약 데이터 로드 완료.");
            System.out.println("초기 데이터 로드 완료.");
        } else {
            System.out.println("데이터가 이미 존재하여 초기 데이터 로드를 건너뜁니다.");
        }
    }
}
