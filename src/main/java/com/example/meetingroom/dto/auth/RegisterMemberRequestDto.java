package com.example.meetingroom.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RegisterMemberRequestDto {
    private String username;
    private String password;
}
