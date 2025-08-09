package com.example.meetingroom.util;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "커스텀 API 응답 포맷")
public class CustomResponseEntity<T> {
    @Schema(description = "요청 성공 여부", example = "true")
    private boolean success = true;
    @Schema(description = "http status")
    private HttpStatus httpStatus;
    @Schema(description = "응답 메시지")
    private String message;
    @Schema(description = "실제 데이터")
    private T data;

    @Builder
    public CustomResponseEntity(boolean success, HttpStatus httpStatus, String message, T data){
        this.success = success;
        this.httpStatus = httpStatus;
        this.message = message;
        this.data = data;
    }
}
