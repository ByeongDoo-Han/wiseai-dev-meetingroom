package com.example.meetingroom.entity;

import com.example.meetingroom.dto.auth.RegisterMemberRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private Member(String username, String password, Role role) {
        this.password = password;
        this.username = username;
        this.role = role;
    }

    public static Member createNewMember(RegisterMemberRequestDto dto, String encodedPassword){
        return new Member(dto.getUsername(), encodedPassword, Role.MEMBER);
    }
}
