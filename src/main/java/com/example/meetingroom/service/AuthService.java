package com.example.meetingroom.service;

import com.example.meetingroom.dto.auth.*;
import com.example.meetingroom.entity.Member;
import com.example.meetingroom.entity.Role;
import com.example.meetingroom.exception.CustomException;
import com.example.meetingroom.exception.ErrorCode;
import com.example.meetingroom.repository.MemberRepository;
import com.example.meetingroom.util.CustomResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Transactional
    public RegisterMemberResponseDto registerMember(final RegisterMemberRequestDto registerMemberDto) {
        if (memberRepository.findMemberByUsername(registerMemberDto.getUsername()).isPresent()) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTED);
        }
        String encodedPassword = passwordEncoder.encode(registerMemberDto.getPassword());
        Member member = Member.createNewMember(registerMemberDto,encodedPassword);
        memberRepository.save(member);
        return RegisterMemberResponseDto.from(member);
    }

    @Transactional
    public ResponseEntity<TokenResponseDto> loginMember(final LoginMemberRequestDto loginMemberRequestDto) {
        Member foundMember = memberRepository.findMemberByUsername(loginMemberRequestDto.getUsername()).orElseThrow(
            () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        if (!passwordEncoder.matches(loginMemberRequestDto.getPassword(), foundMember.getPassword())) {
            throw new CustomException(ErrorCode.USER_NOT_MATCHED_PASSWORD);
        }
        return tokenService.login(foundMember);
    }
}
