package com.example.meetingroom.service;

import com.example.meetingroom.aop.DistributedLock;
import com.example.meetingroom.dto.payment.PaymentRequest;
import com.example.meetingroom.dto.payment.PaymentResult;
import com.example.meetingroom.dto.reservation.ReservationRequestDto;
import com.example.meetingroom.dto.reservation.ReservationResponseDto;
import com.example.meetingroom.entity.*;
import com.example.meetingroom.exception.CustomException;
import com.example.meetingroom.exception.ErrorCode;
import com.example.meetingroom.repository.MeetingRoomRepository;
import com.example.meetingroom.repository.PaymentsRepository;
import com.example.meetingroom.repository.ReservationRepository;
import com.example.meetingroom.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {
    public static final String RESERVATION_LOCK_PREFIX = "RESERVATION:";
    public static final String RESERVATION_CACHED_VALUE = "reservations";
    public static final String RESERVATION_CACHED_KEY = "all";
    private final ReservationRepository reservationRepository;
    private final MeetingRoomRepository meetingRoomRepository;
    private final MemberRepository memberRepository;
    private final PaymentsRepository paymentRepository;
    private final Map<PaymentProviderType, PaymentGateway> paymentGateways;

    private final CacheManager cacheManager;

    @DistributedLock(key = "#request.meetingRoomId", lockName = RESERVATION_LOCK_PREFIX)
    @CacheEvict(value = RESERVATION_CACHED_VALUE, allEntries = true)
    public ReservationResponseDto createReservation(final String username, final ReservationRequestDto request) {
        MeetingRoom meetingRoom = meetingRoomRepository.findById(request.getMeetingRoomId()).orElseThrow(
            () -> new CustomException(ErrorCode.MEETING_ROOM_NOT_FOUND)
        );
        Member member = memberRepository.findMemberByUsername(username).orElseThrow(
            () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        validationDuplicated(request);
        if (reservationRepository.existDuplicatedReservation(
            request.getMeetingRoomId(), request.getStartTime(), request.getEndTime()
        )) {
            throw new CustomException(ErrorCode.RESERVATION_TIME_CONFLICT);
        }
        Reservation reservation = Reservation.create(request, member, meetingRoom);
        Payment payment = Payment.createWithReservation(reservation);
        reservationRepository.save(reservation);
        paymentRepository.save(payment);
        return ReservationResponseDto.from(reservation);
    }

    private void validationDuplicated(final ReservationRequestDto request) {
        List<ReservationResponseDto> cachedReservations = cacheManager.getCache(RESERVATION_CACHED_VALUE).get(RESERVATION_CACHED_KEY,List.class);
        if(cachedReservations !=null){
            boolean hasConflict = cachedReservations.stream()
                .anyMatch(r -> r.getMeetingRoomId().equals(request.getMeetingRoomId())
                    && reservationRepository.existDuplicatedReservation(
                    request.getMeetingRoomId(), request.getStartTime(), request.getEndTime()
                ));
            if(hasConflict){
                throw new CustomException(ErrorCode.RESERVATION_TIME_CONFLICT);
            }
        }else{
            boolean hasConflict = reservationRepository.existDuplicatedReservation(
                request.getMeetingRoomId(), request.getStartTime(), request.getEndTime()
            );
            if (hasConflict) {
                throw new CustomException(ErrorCode.RESERVATION_TIME_CONFLICT);
            }
        }
    }

    @Transactional
    @Cacheable(value = RESERVATION_CACHED_VALUE, key = "'all'")
    public List<ReservationResponseDto> getAllReservation() {
        return reservationRepository.findAll().stream().map(ReservationResponseDto::from).collect(Collectors.toList());
    }

    @Transactional
    @DistributedLock(key = "#request.meetingRoomId", lockName = RESERVATION_LOCK_PREFIX)
    @CacheEvict(value = RESERVATION_CACHED_VALUE, allEntries = true)
    public ReservationResponseDto updateReservation(final Long id, final String username, final ReservationRequestDto request) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(
            () -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND)
        );
        MeetingRoom meetingRoom = meetingRoomRepository.findById(request.getMeetingRoomId()).orElseThrow(
            () -> new CustomException(ErrorCode.MEETING_ROOM_NOT_FOUND)
        );
        Member member = memberRepository.findMemberByUsername(username).orElseThrow(
            () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        if (!Objects.equals(reservation.getMember().getId(), member.getId())) {
            throw new CustomException(ErrorCode.HANDLE_ACCESS_DENIED);
        }
        if (reservationRepository.existDuplicatedReservationExcludingItself(
            reservation.getId(), request.getMeetingRoomId(), request.getStartTime(), request.getEndTime())
        ) {
            throw new CustomException(ErrorCode.RESERVATION_TIME_CONFLICT);
        }

        reservation.update(request.getStartTime(), request.getEndTime(), meetingRoom);
        return ReservationResponseDto.from(reservation);
    }

    @DistributedLock(key = "#reservationId", lockName = RESERVATION_LOCK_PREFIX)
    @Transactional
    @CacheEvict(value = RESERVATION_CACHED_VALUE, allEntries = true)
    public void cancelReservation(final String username, final Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
            () -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND)
        );
        if (!reservation.getMember().getUsername().equals(username)) {
            throw new CustomException(ErrorCode.HANDLE_ACCESS_DENIED);
        }
        reservationRepository.deleteById(reservationId);
    }

    @Transactional
    @DistributedLock(key = "#id", lockName = RESERVATION_LOCK_PREFIX)
    public PaymentResult<?> processPayment(Long id, PaymentRequest request, String username) {
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

        // 2.1 결제 됐는지 확인
        if (reservation.getPaymentStatus().equals(PaymentStatus.SUCCESS)) {
            throw new CustomException(ErrorCode.RESERVATION_ALREADY_PAID);
        }

        // 3. 전략 설정
        PaymentGateway gateway = paymentGateways.get(request.getProviderType());
        if (gateway == null) {
            throw new CustomException(ErrorCode.PAYMENT_PROVIDER_NOT_FOUND);
        }

        // 4. 결제 객체 생성 및 저장
        Payment payment = paymentRepository.findByReservationId(reservation.getId()).orElseThrow(
            () -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND)
        );

        // 5. 게이트웨이를 통해 결제 실행
        PaymentResult<?> result = gateway.pay(request, reservation.getTotalAmount());

        // 6. 결제 결과에 따른 결제 객체 변경
        payment.update(result.getStatus(), result.getExternalPaymentId());

        // 7. 예약 상태 업데이트
        reservation.updatePaymentStatus(result.getStatus());

        return result;
    }
}