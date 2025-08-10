package com.example.meetingroom.util;

import com.example.meetingroom.exception.CustomException;
import com.example.meetingroom.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomResponseEntity<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        CustomResponseEntity<Object> body = CustomResponseEntity.builder()
            .success(false)
            .message(errorMessage)
            .httpStatus(HttpStatus.BAD_REQUEST)
            .data(null)
            .build();

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(body);
    }
}