package com.example.meetingroom.controller;

import com.example.meetingroom.dto.auth.LoginMemberRequestDto;
import com.example.meetingroom.dto.auth.RegisterMemberRequestDto;
import com.example.meetingroom.dto.auth.TokenResponseDto;
import com.example.meetingroom.service.AuthService;
import com.example.meetingroom.service.TokenService;
import com.example.meetingroom.util.CustomResponseEntity;
import com.example.meetingroom.util.ResponseUtil;
import com.example.meetingroom.util.SuccessMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인증", description = "사용자 회원가입, 로그인, 로그아웃 관련 API")
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final TokenService tokenService;

    @Operation(summary = "회원가입", description = "새로운 사용자를 시스템에 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "회원가입 성공"),
        @ApiResponse(responseCode = "409", description = "이미 존재하는 사용자 ID", content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping("/register")
    public ResponseEntity<CustomResponseEntity<Object>> registerMember(@RequestBody RegisterMemberRequestDto registerMemberDto){
        return ResponseUtil.success(
            authService.registerMember(registerMemberDto),
            SuccessMessage.REGISTER_MEMBER_SUCCESS
        );
    }

    @Operation(summary = "로그인", description = "사용자 ID와 비밀번호로 로그인하고 JWT 토큰을 발급받습니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그인 성공 및 토큰 발급"),
        @ApiResponse(responseCode = "401", description = "인증 실패 (ID 또는 비밀번호 오류)", content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> loginMember(@RequestBody LoginMemberRequestDto loginMemberRequestDto){
        return authService.loginMember(loginMemberRequestDto);
    }

    @Operation(summary = "로그아웃", description = "현재 로그인된 사용자의 토큰을 만료 처리합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
        @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰", content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logoutMember(HttpServletRequest request){
        return tokenService.logout(request);
    }
}
