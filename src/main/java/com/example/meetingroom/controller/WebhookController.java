package com.example.meetingroom.controller;

import com.example.meetingroom.dto.payment.PaymentWebhookRequest;
import com.example.meetingroom.entity.PaymentProvider;
import com.example.meetingroom.service.PaymentWebhookService;
import com.example.meetingroom.util.CustomResponseEntity;
import com.example.meetingroom.util.ResponseUtil;
import com.example.meetingroom.util.SuccessMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "웹훅", description = "외부 시스템으로부터의 콜백(웹훅) 처리 API")
@RestController
@RequestMapping("/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private final PaymentWebhookService paymentWebhookService;

    @Operation(summary = "결제 상태 웹훅 수신", description = "외부 결제사로부터 결제 상태 변경 알림을 수신합니다.")
    @PostMapping("/payments/{provider}")
    public ResponseEntity<CustomResponseEntity<Void>> receivePaymentStatusWebhook(@PathVariable PaymentProvider provider, @RequestBody PaymentWebhookRequest request) {
        paymentWebhookService.processPaymentStatusWebhook(request);
        return ResponseUtil.success(null, SuccessMessage.WEBHOOK_RECEIVED_SUCCESS);
    }
}
