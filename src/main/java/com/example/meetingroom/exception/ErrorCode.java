package com.example.meetingroom.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common Errors
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "Invalid Input Value"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "Method Not Allowed"),
    HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "C003", "권한이 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C004", "Server Error"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "C005", "Resource not found"),

    // Meeting Room Errors
    MEETING_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "존재하지 않는 회의실입니다."),
    MEETING_ROOM_ALREADY_EXISTED_NAME(HttpStatus.CONFLICT, "M002", "이미 존재하는 회의실 이름입니다."),
    MEETING_ROOM_CAPACITY_IS_ZERO(HttpStatus.BAD_REQUEST, "M003", "회의실 인원은 1명 이상이어야 합니다."),
    MEETING_ROOM_PRICEPERHOUR_IS_ZERO(HttpStatus.BAD_REQUEST, "M004", "회의실 시간 당 요금은 0이 될 수 없습니다."),
    MEETING_ROOM_TIME_IS_ZERO(HttpStatus.BAD_REQUEST, "M005", "회의실 예약 시간은 최소 30분 이상이어야 합니다."),


    // Reservation Errors
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "존재하지 않는 예약입니다."),
    RESERVATION_TIME_CONFLICT(HttpStatus.CONFLICT, "R002", "해당 시간에 이미 예약이 존재합니다."),
    INVALID_RESERVATION_TIME(HttpStatus.BAD_REQUEST, "R003", "예약 시간은 정시, 30분에 예약이 가능하며 30분 단위로 예약이 가능합니다."),
    RESERVATION_TIME_IS_ZERO(HttpStatus.BAD_REQUEST, "R004", "예약 시간은 최소 30분 이상부터 예약 가능합니다."),
    RESERVATION_START_TIME_IS_AFTER_THAN_END_TIME(HttpStatus.BAD_REQUEST, "R005", "예약 시작 시간은 종료 시간보다 이전이어야 합니다."),
    RESERVATION_ALREADY_PAID(HttpStatus.BAD_REQUEST, "R006", "Reservation is already paid or in process."),
    RESERVATION_STATUS_INVALID(HttpStatus.BAD_REQUEST, "R007", "Invalid reservation status for the operation."),

    // Payment Errors
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "P001", "Payment processing failed"),
    PAYMENT_PROVIDER_NOT_FOUND(HttpStatus.NOT_FOUND, "P002", "지원하지 않는 결제 방식입니다."),
    PAYMENT_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "P003", "Payment has already been processed for this reservation."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "P004", "결제 정보를 찾을 수 없습니다."),
    INVALID_PAYMENT_STATUS(HttpStatus.BAD_REQUEST, "P005", "유효하지 않은 결제 상태입니다."),
    INVALID_PAYMENT_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "P006", "유효하지 않은 결제 상태 전이입니다."),
    PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "P007", "결제 금액이 일치하지 않습니다."),
    INVALID_PAYMENT_AMOUNT(HttpStatus.BAD_REQUEST, "P007", "유효하지 않은 결제 금액입니다."),

    // User Errors
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTED(HttpStatus.CONFLICT, "U002", "이미 가입된 유저입니다."),
    USER_NOT_MATCHED_PASSWORD(HttpStatus.BAD_REQUEST, "U003", "비밀번호가 일치하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public String getMessage() {
        return this.message;
    }
}
