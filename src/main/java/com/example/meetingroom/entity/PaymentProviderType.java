package com.example.meetingroom.entity;

public enum PaymentProviderType {
    CARD("신용카드 결제"),
    SIMPLE_PAYMENT("간편결제"),
    VIRTUAL_ACCOUNT("가상계좌 결제");

    private final String method;

    PaymentProviderType(final String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
}
