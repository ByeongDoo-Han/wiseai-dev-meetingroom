package com.example.meetingroom.entity;

public enum PaymentStatus {
    PENDING("결제 대기 중"),
    SUCCESS("결제 성공"),
    FAILED("결제 실패"),
    CANCELLED("결제 취소");

    private final String status;

    PaymentStatus(final String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
