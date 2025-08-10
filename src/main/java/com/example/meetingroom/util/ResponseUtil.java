package com.example.meetingroom.util;

import org.springframework.http.ResponseEntity;

public class ResponseUtil {
    public static <T> ResponseEntity<CustomResponseEntity<T>> success(T data, SuccessMessage message) {
        return ResponseEntity
            .ok(CustomResponseEntity.<T>builder()
                .data(data)
                .success(true)
                .message(message.getMessage())
                .httpStatus(message.getHttpStatus())
                .build());
    }
}
