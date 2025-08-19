package com.example.meetingroom.controller;

import com.example.meetingroom.dto.payment.PaymentRequest;
import com.example.meetingroom.dto.payment.PaymentResult;
import com.example.meetingroom.dto.reservation.ReservationRequestDto;
import com.example.meetingroom.dto.reservation.ReservationResponseDto;
import com.example.meetingroom.dto.reservation.ReservationUpdateRequestDto;
import com.example.meetingroom.service.PaymentsService;
import com.example.meetingroom.service.ReservationService;
import com.example.meetingroom.util.CustomResponseEntity;
import com.example.meetingroom.util.MemberDetails;
import com.example.meetingroom.util.ResponseUtil;
import com.example.meetingroom.util.SuccessMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "예약", description = "회의실 예약 생성, 조회, 수정, 삭제 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    private final PaymentsService paymentsService;


    @Operation(summary = "회의실 예약 생성", description = "특정 회의실을 지정된 시간 동안 예약합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "예약 생성 성공"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "404", description = "회의실 또는 사용자를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "409", description = "예약 시간이 중복됨", content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping
    public ResponseEntity<CustomResponseEntity<ReservationResponseDto>> createReservation(
        @Parameter(hidden = true) @AuthenticationPrincipal MemberDetails memberDetails,
        @Valid @RequestBody ReservationRequestDto request) {
        return ResponseUtil.success(
            reservationService.createReservation(memberDetails.getUsername(), request),
            SuccessMessage.CREATE_RESERVATION_SUCCESS
        );
    }

    @Operation(summary = "모든 예약 조회", description = "시스템에 등록된 모든 예약을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<CustomResponseEntity<List<ReservationResponseDto>>> getAllReservation() {
        return ResponseUtil.success(
            reservationService.getAllReservation(),
            SuccessMessage.GET_RESERVATION_SUCCESS
        );
    }

    @Operation(summary = "예약 정보 수정", description = "기존 예약의 시간 또는 회의실을 변경합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "예약 수정 성공"),
        @ApiResponse(responseCode = "401", description = "인증되지 않았거나 권한이 없는 사용자", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "404", description = "예약, 회의실 또는 사용자를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "409", description = "수정하려는 시간이 다른 예약과 중복됨", content = @Content(schema = @Schema(hidden = true)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<ReservationResponseDto>> updateReservation(
        @Parameter(hidden = true) @AuthenticationPrincipal MemberDetails memberDetails,
        @Parameter(description = "수정할 예약의 ID") @PathVariable Long id,
        @Valid @RequestBody ReservationUpdateRequestDto request
    ) {
        return ResponseUtil.success(
            reservationService.updateReservation(memberDetails.getUsername(), request),
            SuccessMessage.UPDATE_RESERVATION_SUCCESS
        );
    }

    @Operation(summary = "예약 취소", description = "기존 예약을 취소합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "예약 취소 성공"),
        @ApiResponse(responseCode = "401", description = "인증되지 않았거나 권한이 없는 사용자", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "404", description = "취소할 예약을 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<Void>> cancelReservation(
        @Parameter(hidden = true) @AuthenticationPrincipal MemberDetails memberDetails,
        @Parameter(description = "취소할 예약의 ID") @PathVariable Long id) {
        reservationService.cancelReservation(memberDetails.getUsername(), id);
        return ResponseUtil.success(
            null,
            SuccessMessage.DELETE_RESERVATION_SUCCESS
        );

    }

    @Operation(summary = "결제 요청", description = "지정된 결제 수단으로 결제를 시도합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "결제 성공 또는 처리 중 (가상계좌 등)"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효성 검사 실패)", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "404", description = "예약을 찾을 수 없음 / 지원하지 않는 결제 방식", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "500", description = "결제사 연동 오류 또는 서버 오류", content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping("/{id}/payment")
    public ResponseEntity<CustomResponseEntity<PaymentResult>> processPayment(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @RequestBody PaymentRequest request,
        @PathVariable Long id) {

        return ResponseUtil.success(
            reservationService.processPayment(id, request, memberDetails.getUsername()),
            SuccessMessage.PAYMENT_PROCESS_SUCCESS
        );
    }
}
