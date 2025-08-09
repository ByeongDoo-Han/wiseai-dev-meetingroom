package com.example.meetingroom.entity;

import com.example.meetingroom.dto.auth.RegisterMemberResponseDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
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
    @NotNull
    private String username;
    @NotNull
    @Column(nullable = false)
    private String password;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public Member(String username, String password, Role role){
        this.password = password;
        this.username = username;
        this.role = role;
    }

    public RegisterMemberResponseDto toRegistMemberResponseEntity(){
        return RegisterMemberResponseDto.builder()
            .id(this.id)
            .username(this.username)
            .role(this.role)
            .build();
    }
}
