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
    HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "C003", "Access is Denied"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C004", "Server Error"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "C005", "Resource not found"),

    // Meeting Room Errors
    MEETING_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "존재하지 않는 회의실입니다."),
    MEETING_ROOM_ALREADY_EXISTED_NAME(HttpStatus.CONFLICT, "M002", "이미 존재하는 회의실 이름입니다."),

    // Reservation Errors
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "존재하지 않는 예약입니다."),
    RESERVATION_TIME_CONFLICT(HttpStatus.CONFLICT, "R002", "해당 시간에 이미 예약이 존재합니다."),
    INVALID_RESERVATION_TIME(HttpStatus.BAD_REQUEST, "R003", "Invalid reservation time. Start time must be before end time and in 00 or 30 minute intervals."),
    RESERVATION_ALREADY_PAID(HttpStatus.BAD_REQUEST, "R004", "Reservation is already paid or in process."),
    RESERVATION_STATUS_INVALID(HttpStatus.BAD_REQUEST, "R005", "Invalid reservation status for the operation."),

    // Payment Errors
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "P001", "Payment processing failed"),
    PAYMENT_PROVIDER_NOT_FOUND(HttpStatus.NOT_FOUND, "P002", "Payment provider not found or unsupported"),
    PAYMENT_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "P003", "Payment has already been processed for this reservation."),

    // User Errors
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTED(HttpStatus.CONFLICT, "U002", "이미 가입된 유저입니다."),
    USER_NOT_MATCHED_PASSWORD(HttpStatus.BAD_REQUEST, "U003", "비밀번호가 일치하지 않습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public String getMessage(){
        return this.message;
    }
}
