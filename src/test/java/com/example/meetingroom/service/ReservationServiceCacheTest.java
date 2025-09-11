package com.example.meetingroom.service;

import com.example.meetingroom.dto.reservation.ReservationRequestDto;
import com.example.meetingroom.repository.MeetingRoomRepository;
import com.example.meetingroom.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class ReservationServiceCacheTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MeetingRoomRepository meetingRoomRepository;

    // 각 테스트 실행 전에 캐시를 비운다.
    @BeforeEach
    void setUp() {
        // "reservations" 캐시가 존재할 경우에만 clear()를 호출하도록 안전하게 수정
        if (cacheManager.getCache("reservations") != null) {
            cacheManager.getCache("reservations").clear();
        }
    }

    @Test
    @DisplayName("getAllReservation 캐싱 성능 테스트")
    void getAllReservation_caching_performance_test() {
        // 1. 첫 번째 호출 (DB에서 조회)
        long startTimeFirst = System.nanoTime();
        reservationService.getAllReservation();
        long endTimeFirst = System.nanoTime();
        long firstCallDuration = (endTimeFirst - startTimeFirst) / 1_000_000;
        System.out.println("첫 번째 호출 시간 (DB 조회): " + firstCallDuration + "ms");

        // 2. 두 번째 호출 (캐시에서 조회)
        long startTimeSecond = System.nanoTime();
        reservationService.getAllReservation();
        long endTimeSecond = System.nanoTime();
        long secondCallDuration = (endTimeSecond - startTimeSecond) / 1_000_000;
        System.out.println("두 번째 호출 시간 (캐시 조회): " + secondCallDuration + "ms");

        // 3. 검증: 두 번째 호출이 첫 번째 호출보다 빨라야 한다.
        assertThat(secondCallDuration).isLessThan(firstCallDuration);

        System.out.println("결론: 캐시를 통해 약 " + (firstCallDuration - secondCallDuration) + "ms 의 성능 향상이 있었습니다.");
    }

    @Test
    @DisplayName("예약 생성 시 reservations 캐시가 삭제되는지 테스트")
    void createReservation_evicts_cache() {
        // --- GIVEN ---
        // 1. 캐시를 채우기 위해 첫 번째 호출 실행 (DB 조회)
        System.out.println("--- 캐시 채우기 (DB 조회) ---");
        reservationService.getAllReservation();

        // 2. 캐시가 채워졌는지 확인 (빠른 두 번째 호출)
        long startTimeCached = System.nanoTime();
        reservationService.getAllReservation();
        long endTimeCached = System.nanoTime();
        long cachedCallDuration = (endTimeCached - startTimeCached) / 1_000_000;
        System.out.println("캐시된 조회 시간: " + cachedCallDuration + "ms");

        // 3. 예약 생성을 위한 사전 데이터 준비
        ReservationRequestDto dto = ReservationRequestDto.builder()
            .meetingRoomId(1L)
            .startTime(LocalDateTime.of(2025, 9, 25, 12, 00))
            .endTime(LocalDateTime.of(2025, 9, 25, 13, 00))
            .build();
        // --- WHEN ---
        // 4. 캐시를 삭제할 것으로 예상되는 예약 생성 메서드 호출
        System.out.println("\n--- 예약 생성 (캐시 삭제) ---");
        reservationService.createReservation("testuser", dto);
        System.out.println("createReservation() 호출 완료. 'reservations' 캐시가 삭제되었습니다.");

        // --- THEN ---
        // 5. 캐시가 비워졌는지 확인 (다시 느려진 세 번째 호출)
        System.out.println("\n--- 캐시 비워졌는지 확인 (DB 조회) ---");
        long startTimeEvicted = System.nanoTime();
        reservationService.getAllReservation();
        long endTimeEvicted = System.nanoTime();
        long evictedCallDuration = (endTimeEvicted - startTimeEvicted) / 1_000_000;
        System.out.println("캐시 삭제 후 조회 시간: " + evictedCallDuration + "ms");

        // 검증: 캐시 삭제 후 호출 시간이 캐시된 상태의 호출 시간보다 길어야 함을 확인
        assertThat(evictedCallDuration).isGreaterThan(cachedCallDuration);
        System.out.println("\n결론: createReservation() 호출 후 캐시가 정상적으로 삭제되어 DB 조회가 다시 발생함을 확인했습니다.");
    }
}