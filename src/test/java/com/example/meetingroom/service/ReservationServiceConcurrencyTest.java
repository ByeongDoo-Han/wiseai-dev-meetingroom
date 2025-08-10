package com.example.meetingroom.service;

import com.example.meetingroom.dto.reservation.ReservationRequest;
import com.example.meetingroom.dto.reservation.ReservationUpdateRequest;
import com.example.meetingroom.entity.*;
import com.example.meetingroom.exception.CustomException;
import com.example.meetingroom.exception.ErrorCode;
import com.example.meetingroom.repository.MeetingRoomRepository;
import com.example.meetingroom.repository.MemberRepository;
import com.example.meetingroom.repository.ReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {"spring.data.redis.host=localhost"})
@DisplayName("ReservationService 동시성 테스트")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class ReservationServiceConcurrencyTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MeetingRoomRepository meetingRoomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    @DisplayName("두 사용자가 동시에 같은 시간에 예약을 생성하면 한 명만 성공해야 한다")
    void create_reservation_concurrently() throws InterruptedException {
        // given
        Member member1 = memberRepository.save(Member.builder().username("concurrentUser1").password("password").role(Role.MEMBER).build());
        Member member2 = memberRepository.save(Member.builder().username("concurrentUser2").password("password").role(Role.MEMBER).build());
        MeetingRoom meetingRoom = meetingRoomRepository.save(MeetingRoom.builder().name("동시성 테스트 회의실").pricePerHour(new BigDecimal("1000")).build());

        LocalDateTime startTime = LocalDateTime.of(2025, 12, 1, 15, 0);
        LocalDateTime endTime = startTime.plusHours(1);

        ReservationRequest request = ReservationRequest.builder()
            .meetingRoomId(meetingRoom.getId())
            .startTime(startTime)
            .endTime(endTime)
            .build();

        int numberOfThreads = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < numberOfThreads; i++) {
            final String username = (i == 0) ? member1.getUsername() : member2.getUsername();
            executorService.submit(() -> {
                try {
                    latch.countDown();
                    latch.await();
                    reservationService.createReservation(username, request);
                    successCount.incrementAndGet();
                } catch (CustomException e) {
                    if (e.getErrorCode() == ErrorCode.RESERVATION_TIME_CONFLICT) {
                        failureCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                }
            });
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        reservationRepository.flush();

        // then
        long reservationCount = reservationRepository.countByMeetingRoomIdAndStartTime(meetingRoom.getId(), startTime);

        assertThat(successCount.get() + failureCount.get()).isEqualTo(numberOfThreads);
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failureCount.get()).isEqualTo(1);
        assertThat(reservationCount).isEqualTo(1);

    }

    @Test
    @DisplayName("두 사용자가 동시에 서로의 예약 시간으로 변경 시, 교착 상태 없이 처리되어야 한다 (둘 다 실패)")
    void update_reservation_for_swap_concurrently() throws InterruptedException {
        // given
        Member memberA = memberRepository.save(Member.builder().username("memberA").password("password").role(Role.MEMBER).build());
        Member memberB = memberRepository.save(Member.builder().username("memberB").password("password").role(Role.MEMBER).build());
        MeetingRoom meetingRoom = meetingRoomRepository.save(MeetingRoom.builder().name("교착상태 테스트 회의실").pricePerHour(new BigDecimal("1000")).build());

        Reservation reservationA = reservationRepository.save(Reservation.builder()
            .member(memberA).meetingRoom(meetingRoom)
            .paymentStatus(PaymentStatus.PENDING)
            .startTime(LocalDateTime.of(2026, 1, 1, 10, 0))
            .endTime(LocalDateTime.of(2026, 1, 1, 11, 0))
            .totalAmount(new BigDecimal("1000"))
            .build());

        Reservation reservationB = reservationRepository.save(Reservation.builder()
            .member(memberB).meetingRoom(meetingRoom)
            .paymentStatus(PaymentStatus.PENDING)
            .startTime(LocalDateTime.of(2026, 1, 1, 12, 0))
            .endTime(LocalDateTime.of(2026, 1, 1, 13, 0))
            .totalAmount(new BigDecimal("1000"))
            .build());

        ReservationUpdateRequest requestA = ReservationUpdateRequest.builder()
            .reservationId(reservationA.getId()).meetingRoomId(meetingRoom.getId())
            .startTime(reservationB.getStartTime()).endTime(reservationB.getEndTime())
            .build();

        ReservationUpdateRequest requestB = ReservationUpdateRequest.builder()
            .reservationId(reservationB.getId()).meetingRoomId(meetingRoom.getId())
            .startTime(reservationA.getStartTime()).endTime(reservationA.getEndTime())
            .build();

        int numberOfThreads = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when
        executorService.submit(() -> {
            try {
                latch.countDown();
                latch.await();
                reservationService.updateReservation(memberA.getUsername(), requestA);
                successCount.incrementAndGet();
            } catch (Exception e) {
                failCount.incrementAndGet();
            }
        });

        executorService.submit(() -> {
            try {
                latch.countDown();
                latch.await();
                reservationService.updateReservation(memberB.getUsername(), requestB);
                successCount.incrementAndGet();

            } catch (Exception e) {
                failCount.incrementAndGet();

            }
        });

        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // then
        assertThat(successCount.get()).isEqualTo(0);
        assertThat(failCount.get()).isEqualTo(numberOfThreads);

        Reservation finalReservationA = reservationRepository.findById(reservationA.getId()).get();
        Reservation finalReservationB = reservationRepository.findById(reservationB.getId()).get();

        assertThat(finalReservationA.getStartTime()).isEqualTo(LocalDateTime.of(2026, 1, 1, 10, 0));
        assertThat(finalReservationB.getStartTime()).isEqualTo(LocalDateTime.of(2026, 1, 1, 12, 0));
    }
}

