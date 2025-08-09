package com.example.meetingroom.util;

import com.example.meetingroom.exception.CustomException;
import com.example.meetingroom.exception.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CustomResponseEntity<Object>> handleCustomException(CustomException ex) {
        ErrorCode errorCode = ex.getErrorCode();

        CustomResponseEntity<Object> body = CustomResponseEntity.builder()
            .success(false)
            .message(errorCode.getMessage())
            .httpStatus(errorCode.getHttpStatus())
            .data(null)
            .build();

        return ResponseEntity.ok()
            .body(body);
    }
}