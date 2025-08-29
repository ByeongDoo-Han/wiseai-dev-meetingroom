package com.example.meetingroom.dto.auth;

import com.example.meetingroom.entity.Member;
import com.example.meetingroom.entity.Reservation;
import com.example.meetingroom.entity.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RegisterMemberResponseDto {
    private Long id;
    private String username;
    private Role role;

    private RegisterMemberResponseDto(String username, Role role) {
        this.username = username;
        this.role = role;
    }

    public static RegisterMemberResponseDto from(Member member){
        return new RegisterMemberResponseDto(member.getUsername(), member.getRole());
    }
}
