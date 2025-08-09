package com.example.meetingroom.service;

import com.example.meetingroom.aop.DistributedLock;
import com.example.meetingroom.dto.reservation.ReservationRequest;
import com.example.meetingroom.dto.reservation.ReservationResponse;
import com.example.meetingroom.dto.reservation.ReservationUpdateRequest;
import com.example.meetingroom.entity.MeetingRoom;
import com.example.meetingroom.entity.Member;
import com.example.meetingroom.entity.Reservation;
import com.example.meetingroom.exception.CustomException;
import com.example.meetingroom.exception.ErrorCode;
import com.example.meetingroom.repository.MeetingRoomRepository;
import com.example.meetingroom.repository.PaymentRepository;
import com.example.meetingroom.repository.ReservationRepository;
import com.example.meetingroom.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MeetingRoomRepository meetingRoomRepository;
    private final MemberRepository memberRepository;
    private final PaymentsService paymentService;
    private final PaymentRepository paymentRepository;

    @DistributedLock(key = "#request.meetingRoomId")
    @Transactional
    public ReservationResponse createReservation(final String username, final ReservationRequest request) {
        MeetingRoom meetingRoom = meetingRoomRepository.findById(request.getMeetingRoomId()).orElseThrow(
            () -> new CustomException(ErrorCode.MEETING_ROOM_NOT_FOUND)
        );
        Member member = memberRepository.findMemberByUsername(username).orElseThrow(
            () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        if (reservationRepository.existDuplicatedReservation(
            request.getMeetingRoomId(),
            request.getStartTime(),
            request.getEndTime()
        )) {
            throw new CustomException(ErrorCode.RESERVATION_TIME_CONFLICT);
        }

        Reservation reservation = Reservation.builder()
            .startTime(request.getStartTime())
            .endTime(request.getEndTime())
            .totalAmount(calculateTotalPrice(request.getStartTime(), request.getEndTime(), meetingRoom.getPricePerHour()))
            .meetingRoom(meetingRoom)
            .member(member)
            .build();
        reservationRepository.save(reservation);
        return reservation.toReservationResponseEntity();
    }

    @Transactional
    public List<ReservationResponse> getAllReservation() {
        List<Reservation> reservationResponseList = reservationRepository.findAll();
        List<ReservationResponse> response = new ArrayList<>();
        for(Reservation reservation:reservationResponseList){
            response.add(reservation.toReservationResponseEntity());
        }
        return response;
    }

    @Transactional
    @DistributedLock(key = "#request.meetingRoomId")
    public ReservationResponse updateReservation(final String username, final ReservationUpdateRequest request) {
        Reservation reservation = reservationRepository.findById(request.getReservationId()).orElseThrow(
            () -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND)
        );
        MeetingRoom meetingRoom = meetingRoomRepository.findById(request.getMeetingRoomId()).orElseThrow(
            () -> new CustomException(ErrorCode.MEETING_ROOM_NOT_FOUND)
        );
        Member member = memberRepository.findMemberByUsername(username).orElseThrow(
            () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        if(!Objects.equals(reservation.getMember().getId(), member.getId())){
            throw new CustomException(ErrorCode.HANDLE_ACCESS_DENIED);
        };
        if (reservationRepository.existDuplicatedReservationExcludingItself(
            reservation.getId(),
            request.getMeetingRoomId(),
            request.getStartTime(),
            request.getEndTime())
        ) {
            throw new CustomException(ErrorCode.RESERVATION_TIME_CONFLICT);
        }

        reservation.update(
            request.getStartTime(),
            request.getEndTime(),
            calculateTotalPrice(request.getStartTime(), request.getEndTime(), meetingRoom.getPricePerHour()),
            meetingRoom);
        reservationRepository.save(reservation);
        return reservation.toReservationResponseEntity();
    }

    private BigDecimal calculateTotalPrice(LocalDateTime startTime, LocalDateTime endTime, BigDecimal pricePerHour){
        Duration duration = Duration.between(startTime, endTime);
        long minutes = duration.toMinutes();
        BigDecimal totalMinutes = new BigDecimal(minutes);
        BigDecimal sixty = new BigDecimal(60);
        BigDecimal hours = totalMinutes.divide(sixty, 2, RoundingMode.HALF_UP);
        return hours.multiply(pricePerHour);
    }

    @Transactional
    public void cancelReservation(final String username, final Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
            () -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND)
        );
        if(!reservation.getMember().getUsername().equals(username)){
            throw new CustomException(ErrorCode.HANDLE_ACCESS_DENIED);
        }
        reservationRepository.deleteById(reservationId);
    }
}