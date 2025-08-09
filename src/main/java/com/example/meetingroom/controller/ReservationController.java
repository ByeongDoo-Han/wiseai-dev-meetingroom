package com.example.meetingroom.controller;

import com.example.meetingroom.dto.reservation.ReservationRequest;
import com.example.meetingroom.dto.reservation.ReservationResponse;
import com.example.meetingroom.dto.reservation.ReservationUpdateRequest;
import com.example.meetingroom.service.ReservationService;
import com.example.meetingroom.util.CustomResponseEntity;
import com.example.meetingroom.util.MemberDetails;
import com.example.meetingroom.util.ResponseUtil;
import com.example.meetingroom.util.SuccessMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/reservation")
    public ResponseEntity<CustomResponseEntity<ReservationResponse>> createReservation(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @Valid @RequestBody ReservationRequest request) {
        return ResponseUtil.success(
            reservationService.createReservation(memberDetails.getUsername(), request),
            SuccessMessage.CREATE_RESERVATION_SUCCESS
        );
    }

    @GetMapping("/reservation")
    public ResponseEntity<CustomResponseEntity<List<ReservationResponse>>> getAllReservation(){
        return ResponseUtil.success(
            reservationService.getAllReservation(),
            SuccessMessage.GET_RESERVATION_SUCCESS
        );
    }

    @PutMapping("/reservation/{id}")
    public ResponseEntity<CustomResponseEntity<ReservationResponse>> updateReservation(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @PathVariable Long id,
        @RequestBody ReservationUpdateRequest request
    ){
        return ResponseUtil.success(
            reservationService.updateReservation(memberDetails.getUsername(),request),
            SuccessMessage.UPDATE_RESERVATION_SUCCESS
        );
    }

//    @DeleteMapping("/{reservationId}")
//    public ResponseEntity<CustomResponseEntity<Void>> cancelReservation(
//            @AuthenticationPrincipal MemberDetails memberDetails,
//            @PathVariable Long reservationId) {
//        reservationService.cancelReservation(memberDetails.getMember(), reservationId);
//        return CustomResponseEntity.toResponseEntity(SuccessMessage.RESERVATION_CANCEL_SUCCESS);
//    }

//    @PatchMapping("/{reservationId}")
//    public ResponseEntity<CustomResponseEntity<ReservationResponse>> updateReservation(
//            @AuthenticationPrincipal MemberDetails memberDetails,
//            @PathVariable Long reservationId,
//            @Valid @RequestBody ReservationRequest request) {
//        ReservationResponse response = reservationService.updateReservation(memberDetails.getMember(), reservationId, request);
//        return CustomResponseEntity.toResponseEntity(SuccessMessage.RESERVATION_UPDATE_SUCCESS, response);
//    }
}
