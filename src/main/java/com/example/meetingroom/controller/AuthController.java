package com.example.meetingroom.controller;

import com.example.meetingroom.dto.auth.LoginMemberRequestDto;
import com.example.meetingroom.dto.auth.RegisterMemberRequestDto;
import com.example.meetingroom.dto.auth.TokenResponseDto;
import com.example.meetingroom.service.AuthService;
import com.example.meetingroom.service.TokenService;
import com.example.meetingroom.util.CustomResponseEntity;
import com.example.meetingroom.util.ResponseUtil;
import com.example.meetingroom.util.SuccessMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<CustomResponseEntity<Object>> registerMember(@RequestBody RegisterMemberRequestDto registerMemberDto){
        return ResponseUtil.success(
            authService.registerMember(registerMemberDto),
            SuccessMessage.REGISTER_MEMBER_SUCCESS
        );
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> loginMember(@RequestBody LoginMemberRequestDto loginMemberRequestDto){
        return authService.loginMember(loginMemberRequestDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logoutMember(HttpServletRequest request){
        return tokenService.logout(request);
    }
}
