package com.example.meetingroom.controller;

import com.example.meetingroom.dto.payment.PaymentRequest;
import com.example.meetingroom.dto.payment.PaymentResult;
import com.example.meetingroom.service.PaymentsService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "결제", description = "결제 처리 관련 API")
@RestController
@RequestMapping("/payments") // /payments 로 시작하는 모든 요청을 처리
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentsService paymentsService;

    @Operation(summary = "결제 요청", description = "지정된 결제 수단으로 결제를 시도합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "결제 성공 또는 처리 중 (가상계좌 등)"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효성 검사 실패)", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "404", description = "예약을 찾을 수 없음 / 지원하지 않는 결제 방식", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "500", description = "결제사 연동 오류 또는 서버 오류", content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping("/process")
    public ResponseEntity<CustomResponseEntity<PaymentResult>> processPayment(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @Valid @RequestBody PaymentRequest request) {

        return ResponseUtil.success(
            paymentsService.processPayment(request, memberDetails.getUsername()),
            SuccessMessage.PAYMENT_PROCESS_SUCCESS
        );
    }
}