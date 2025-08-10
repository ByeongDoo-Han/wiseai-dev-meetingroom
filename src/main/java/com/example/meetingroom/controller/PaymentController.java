package com.example.meetingroom.controller;

import com.example.meetingroom.entity.PaymentStatus;
import com.example.meetingroom.service.PaymentsService;
import com.example.meetingroom.util.CustomResponseEntity;
import com.example.meetingroom.util.ResponseUtil;
import com.example.meetingroom.util.SuccessMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "결제", description = "결제 처리 관련 API")
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentsService paymentsService;


    @Operation(summary = "결제 상태 조회", description = "결제 ID를 통해 결제 상태를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "결제 상태 조회 성공"),
        @ApiResponse(responseCode = "404", description = "결제 정보를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/{paymentId}/status")
    public ResponseEntity<CustomResponseEntity<PaymentStatus>> getPaymentStatus(
        @PathVariable Long paymentId) {
        return ResponseUtil.success(
            paymentsService.getPaymentStatus(paymentId),
            SuccessMessage.PAYMENT_STATUS_RETRIEVED_SUCCESS
        );
    }
}
