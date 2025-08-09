package com.example.meetingroom.repository;

import com.example.meetingroom.entity.MeetingRoom;
import com.example.meetingroom.entity.PaymentStatus;
import com.example.meetingroom.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByMeetingRoomAndPaymentStatus(MeetingRoom meetingRoom, PaymentStatus paymentStatus);
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END "
        + "FROM Reservation r "
        + "WHERE r.meetingRoom.id = :meetingRoomId "
        + "AND r.endTime > :startTime "
        + "AND r.startTime < :endTime")
    boolean existDuplicatedReservation(@Param("meetingRoomId") Long meetingRoomId,
                                       @Param("startTime") LocalDateTime startTime,
                                       @Param("endTime") LocalDateTime endTime);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END "
        + "FROM Reservation r "
        + "WHERE r.id <> :reservationId "
        + "AND r.meetingRoom.id = :meetingRoomId "
        + "AND r.endTime > :startTime "
        + "AND r.startTime < :endTime")
    boolean existDuplicatedReservationExcludingItself(@Param("reservationId") Long reservationId,
                                       @Param("meetingRoomId") Long meetingRoomId,
                                       @Param("startTime") LocalDateTime startTime,
                                       @Param("endTime") LocalDateTime endTime);
}