package com.example.meetingroom.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessMessage {

    GET_MEETING_ROOM_SUCCESS(HttpStatus.OK, "회의실 목록 조회 성공"),
    CREATE_MEETING_ROOM_SUCCESS(HttpStatus.CREATED, "회의실 생성 성공"),
    UPDATE_MEETING_ROOM_SUCCESS(HttpStatus.CREATED, "회의실 수정 성공"),
    DELETE_MEETING_ROOM_SUCCESS(HttpStatus.NO_CONTENT, "회의실 삭제 성공"),
    CREATE_RESERVATION_SUCCESS(HttpStatus.CREATED, "회의실 예약 성공"),
    GET_RESERVATION_SUCCESS(HttpStatus.OK, "회의실 예약 조회 성공"),
    UPDATE_RESERVATION_SUCCESS(HttpStatus.OK, "회의실 예약 수정 성공"),
    DELETE_RESERVATION_SUCCESS(HttpStatus.NO_CONTENT, "회의실 예약 삭제 성공"),
    PAYMENT_PROCESS_SUCCESS(HttpStatus.OK, "결제 성공"),
    REGISTER_MEMBER_SUCCESS(HttpStatus.CREATED, "회원가입 성공"),
    LOGIN_MEMBER_SUCCESS(HttpStatus.OK, "로그인 성공"),
    WEBHOOK_RECEIVED_SUCCESS(HttpStatus.OK, "웹훅 수신 및 처리 성공"),
    PAYMENT_STATUS_RETRIEVED_SUCCESS(HttpStatus.OK, "결제 상태 조회 성공");

    private final HttpStatus httpStatus;
    private final String message;

    public String getMessage() {
        return this.message;
    }

    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }
}
