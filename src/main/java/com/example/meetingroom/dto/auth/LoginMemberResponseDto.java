package com.example.meetingroom.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginMemberResponseDto {
    private Long id;
    private String username;

    @Builder
    public LoginMemberResponseDto(Long id, String username){
        this.id = id;
        this.username = username;
    }
}
