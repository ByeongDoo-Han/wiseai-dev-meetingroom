package com.example.meetingroom.service;

import com.example.meetingroom.dto.reservation.ReservationRequest;
import com.example.meetingroom.dto.reservation.ReservationResponse;
import com.example.meetingroom.dto.reservation.ReservationUpdateRequest;
import com.example.meetingroom.entity.MeetingRoom;
import com.example.meetingroom.entity.Member;
import com.example.meetingroom.entity.Reservation;
import com.example.meetingroom.entity.Role;
import com.example.meetingroom.exception.CustomException;
import com.example.meetingroom.exception.ErrorCode;
import com.example.meetingroom.repository.MeetingRoomRepository;
import com.example.meetingroom.repository.MemberRepository;
import com.example.meetingroom.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationService 테스트")
class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private MeetingRoomRepository meetingRoomRepository;

    @Mock
    private MemberRepository memberRepository;

    private Member member;
    private MeetingRoom meetingRoom;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        member = Member.builder().id(1L).role(Role.MEMBER).username("testuser").build();
        meetingRoom = MeetingRoom.builder().id(1L).name("회의실 A").pricePerHour(new BigDecimal("10000")).build();
        reservation = Reservation.builder()
                .id(1L)
                .member(member)
                .meetingRoom(meetingRoom)
                .startTime(LocalDateTime.of(2025, 8, 10, 10, 0))
                .endTime(LocalDateTime.of(2025, 8, 10, 11, 0))
                .totalAmount(new BigDecimal("10000.00"))
                .build();
    }

    @Nested
    @DisplayName("예약 생성 테스트")
    class CreateReservationTest {
        @Test
        @DisplayName("생성 성공")
        void create_reservation_success(){
             LocalDateTime startTime = LocalDateTime.of(2025, 9, 1, 10, 0);
             LocalDateTime endTime = startTime.plusHours(2); // 2시간 예약
             ReservationRequest request = ReservationRequest.builder()
                     .meetingRoomId(1L)
                     .startTime(startTime)
                     .endTime(endTime)
                     .build();
            given(memberRepository.findMemberByUsername(member.getUsername())).willReturn(Optional.of(member));
            given(meetingRoomRepository.findById(request.getMeetingRoomId())).willReturn(Optional.of(meetingRoom));
            given(reservationRepository.existDuplicatedReservation(
                request.getMeetingRoomId(),
                request.getStartTime(),
                request.getEndTime()
            )).willReturn(false);

            Reservation savedReservation = Reservation.builder()
                .id(2L)
                .member(member)
                .meetingRoom(meetingRoom)
                .startTime(startTime)
                .endTime(endTime)
                .totalAmount(new BigDecimal("20000.00"))
                .build();

            given(reservationRepository.save(any(Reservation.class))).willReturn(savedReservation);

            ReservationResponse response = reservationService.createReservation(member.getUsername(), request);

            assertThat(response).isNotNull();
            assertThat(response.getStartTime()).isEqualTo(startTime);
            assertThat(response.getEndTime()).isEqualTo(endTime);
            assertThat(response.getTotalAmount()).isEqualByComparingTo(new BigDecimal("20000.00"));
            verify(reservationRepository).save(any(Reservation.class));
        }

        @Test
        @DisplayName("실패 - 예약 시간 중복")
        void create_fail_time_conflict() {
            // given
            // builder를 사용하여 예약 요청 DTO 생성
            LocalDateTime startTime = LocalDateTime.of(2025, 9, 1, 10, 0);
            LocalDateTime endTime = startTime.plusHours(2);
            ReservationRequest request = ReservationRequest.builder()
                    .meetingRoomId(1L)
                    .startTime(startTime)
                    .endTime(endTime)
                    .build();

            // Repository Mocking 설정
            // 사용자 조회와 회의실 조회는 성공한 것으로 설정
            given(memberRepository.findMemberByUsername(member.getUsername())).willReturn(Optional.of(member));
            given(meetingRoomRepository.findById(request.getMeetingRoomId())).willReturn(Optional.of(meetingRoom));

            // existDuplicatedReservation 메소드가 true를 반환하도록 설정하여 시간 중복 상황을 시뮬레이션
            given(reservationRepository.existDuplicatedReservation(
                    request.getMeetingRoomId(),
                    request.getStartTime(),
                    request.getEndTime()
            )).willReturn(true);

            // when & then
            // 예외가 발생하는 것을 검증
            assertThatThrownBy(() -> reservationService.createReservation(member.getUsername(), request))
                    // CustomException 클래스의 예외가 발생하는지 확인
                    .isInstanceOf(CustomException.class)
                    // 해당 예외의 errorCode 필드 값이 RESERVATION_TIME_CONFLICT 인지 확인
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESERVATION_TIME_CONFLICT);

            // 시간이 중복되어 예외가 발생했으므로, save 메소드가 절대 호출되지 않았는지 검증
            verify(reservationRepository, never()).save(any(Reservation.class));
        }
    }

    @Nested
    @DisplayName("예약 취소 테스트")
    class CancelReservationTest {

        @Test
        @DisplayName("성공")
        void cancel_success() {
            // given
            Long reservationId = 1L;

            // 취소할 예약 조회는 성공해야 함
            given(reservationRepository.findById(reservationId)).willReturn(Optional.of(reservation));

            // when
            reservationService.cancelReservation(member.getUsername(), reservationId);

            // then
            // reservationRepository.delete()가 올바른 reservation 객체를 가지고 1번 호출되었는지 검증
            verify(reservationRepository).deleteById(reservation.getId());
        }

        @Test
        @DisplayName("실패 - 다른 사용자의 예약 취소")
        void cancel_fail_access_denied() {
            // given
            Long reservationId = 1L;
            Member anotherMember = Member.builder().id(2L).username("anotherUser").build();

            // 예약은 존재하지만, 'testuser'의 소유임
            given(reservationRepository.findById(reservationId)).willReturn(Optional.of(reservation));

            // when & then
            // 예외 발생을 검증
            assertThatThrownBy(() -> reservationService.cancelReservation(anotherMember.getUsername(), reservationId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.HANDLE_ACCESS_DENIED);

            // 접근이 거부되었으므로 delete 메소드가 절대 호출되지 않았는지 검증
            verify(reservationRepository, never()).deleteById(reservationId);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 예약 취소")
        void cancel_fail_reservation_not_found() {
            // given
            Long reservationId = 99L; // 존재하지 않는 예약 ID

            // 존재하지 않는 ID로 조회 시 Optional.empty()를 반환하도록 설정
            given(reservationRepository.findById(reservationId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> reservationService.cancelReservation(member.getUsername(), reservationId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESERVATION_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("예약 수정 테스트")
    class UpdateReservationTest {

        @Test
        @DisplayName("성공")
        void update_success() {
            // given
            LocalDateTime newStartTime = LocalDateTime.of(2025, 8, 11, 14, 0);
            LocalDateTime newEndTime = newStartTime.plusHours(3); // 3시간으로 변경
            ReservationUpdateRequest request = ReservationUpdateRequest.builder()
                    .reservationId(1L)
                    .meetingRoomId(1L) // 회의실은 변경하지 않음
                    .startTime(newStartTime)
                    .endTime(newEndTime)
                    .build();

            // 서비스 로직에서 필요한 repository 호출들을 mocking
            given(reservationRepository.findById(request.getReservationId())).willReturn(Optional.of(reservation));
            given(memberRepository.findMemberByUsername(member.getUsername())).willReturn(Optional.of(member));
            given(meetingRoomRepository.findById(request.getMeetingRoomId())).willReturn(Optional.of(meetingRoom));
            // 수정하려는 시간을 포함하여 중복 검사 시, 중복이 없다고 설정
            given(reservationRepository.existDuplicatedReservationExcludingItself(
                    reservation.getId(), request.getMeetingRoomId(), request.getStartTime(), request.getEndTime()
            )).willReturn(false);

            // when
            ReservationResponse response = reservationService.updateReservation(member.getUsername(), request);

            // then
            // 응답 DTO의 값이 요청에 맞게 변경되었는지 확인
            assertThat(response.getStartTime()).isEqualTo(newStartTime);
            assertThat(response.getEndTime()).isEqualTo(newEndTime);
            // 3시간 * 10000원/시간 = 30000원
            assertThat(response.getTotalAmount()).isEqualByComparingTo(new BigDecimal("30000.00"));
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 예약")
        void update_fail_reservation_not_found() {
            // given
            ReservationUpdateRequest request = ReservationUpdateRequest.builder()
                    .reservationId(99L) // 존재하지 않는 예약 ID
                    .meetingRoomId(1L)
                    .startTime(LocalDateTime.now())
                    .endTime(LocalDateTime.now().plusHours(1))
                    .build();

            // 존재하지 않는 ID로 조회 시 Optional.empty()를 반환하도록 설정
            given(reservationRepository.findById(request.getReservationId())).willReturn(Optional.empty());

            // when & then
            // 예외 발생을 검증
            assertThatThrownBy(() -> reservationService.updateReservation(member.getUsername(), request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESERVATION_NOT_FOUND);
        }

        @Test
        @DisplayName("실패 - 이 시간에 이미 예약이 존재")
        void update_fail_time_conflict() {
            // given
            // builder를 사용하여 예약 요청 DTO 생성
            LocalDateTime startTime = LocalDateTime.of(2025, 9, 1, 10, 0);
            LocalDateTime endTime = startTime.plusHours(2);
            ReservationUpdateRequest request = ReservationUpdateRequest.builder()
                .meetingRoomId(1L)
                .reservationId(1L)
                .startTime(startTime)
                .endTime(endTime)
                .build();

            // Repository Mocking 설정
            // 사용자 조회와 회의실 조회는 성공한 것으로 설정
            given(reservationRepository.findById(request.getReservationId())).willReturn(Optional.of(reservation));
            given(memberRepository.findMemberByUsername(member.getUsername())).willReturn(Optional.of(member));
            given(meetingRoomRepository.findById(request.getMeetingRoomId())).willReturn(Optional.of(meetingRoom));

            // existDuplicatedReservation 메소드가 true를 반환하도록 설정하여 시간 중복 상황을 시뮬레이션
            given(reservationRepository.existDuplicatedReservationExcludingItself(
                reservation.getId(),
                request.getMeetingRoomId(),
                request.getStartTime(),
                request.getEndTime()
            )).willReturn(true);

            // when & then
            // 예외가 발생하는 것을 검증
            assertThatThrownBy(() -> reservationService.updateReservation(member.getUsername(), request))
                // CustomException 클래스의 예외가 발생하는지 확인
                .isInstanceOf(CustomException.class)
                // 해당 예외의 errorCode 필드 값이 RESERVATION_TIME_CONFLICT 인지 확인
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESERVATION_TIME_CONFLICT);

            // 시간이 중복되어 예외가 발생했으므로, save 메소드가 절대 호출되지 않았는지 검증
            verify(reservationRepository, never()).save(any(Reservation.class));
        }
    }
}