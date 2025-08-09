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
    ;

    private final HttpStatus httpStatus;
    private final String message;

    public String getMessage(){
        return this.message;
    }
    public HttpStatus getHttpStatus(){
        return this.httpStatus;
    }
}
