package com.example.meetingroom.service;

import com.example.meetingroom.dto.auth.TokenResponseDto;
import com.example.meetingroom.entity.Member;
import com.example.meetingroom.util.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final RedissonClient redissonClient;
    private final TokenProvider tokenProvider;
    private static final long TOKEN_EXPIRE_SECONDS = 60*60;
    private static final long REFRESH_TOKEN_EXPIRE_SECONDS = 60*60*24;
    private static final String REFRESH_TOKEN_PREFIX = "refreshToken:";
    private static final String REFRESH_TOKEN = "refreshToken";
    private static final String ACCESS_TOKEN_PREFIX = "accessToken:";
    private static final String GRANT_TYPE = "Bearer ";
    private static final String STRICT = "Strict";
    private static final String AUTH_HEADER = "Authorization";

    @Transactional
    public ResponseEntity<TokenResponseDto> login(final Member foundMember) {
        String username = foundMember.getUsername();
        String role = "ROLE_"+foundMember.getRole().name();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            username, null,
            List.of(new SimpleGrantedAuthority(role))
        );

        String accessToken = tokenProvider.createAccessToken(authentication);
        return createNewToken(accessToken, username, authentication);
    }

    @Transactional
    public ResponseEntity<Void> logout(final HttpServletRequest request){
        String authHeader = request.getHeader(AUTH_HEADER);
        if (authHeader == null || !authHeader.startsWith(GRANT_TYPE)) {
            return ResponseEntity.badRequest().build();
        }
        String token = authHeader.substring(7);
        String email = tokenProvider.getAuthentication(token).getName();
        deleteRefreshToken(email);
        return ResponseEntity.ok().build();
    }

    private void saveRefreshToken(final String email, final String refreshToken){
        RBucket<String> bucket = redissonClient.getBucket(REFRESH_TOKEN_PREFIX+email);
        bucket.set(refreshToken, Duration.ofSeconds(REFRESH_TOKEN_EXPIRE_SECONDS));
    }

    private String getRefreshToken(final String email) {
        RBucket<String> bucket = redissonClient.getBucket(REFRESH_TOKEN_PREFIX + email);
        return bucket.get();
    }

    private void deleteRefreshToken(final String email) {
        RBucket<String> bucket = redissonClient.getBucket(REFRESH_TOKEN_PREFIX + email);
        bucket.delete();
    }

    @Transactional
    public ResponseEntity<TokenResponseDto> refresh(final String refreshToken) {
        Authentication authentication = tokenProvider.getAuthentication(refreshToken);
        String accessToken = tokenProvider.createAccessToken(authentication);
        String email = authentication.getName();
        String oldRefreshToken = getRefreshToken(email);
        if (refreshToken == null) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(TokenResponseDto.builder()
                    .message("Refresh token is missing")
                    .build());
        }
        if(!refreshToken.equals(oldRefreshToken)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(TokenResponseDto.builder()
                    .message("유효하지 않은 RefreshToken입니다.")
                    .build());
        }
        return createNewToken(accessToken, email, authentication);
    }

    private ResponseEntity<TokenResponseDto> createNewToken(final String accessToken,
                                                            final String username,
                                                            final Authentication authentication) {
        String newRefreshToken = tokenProvider.createRefreshToken(authentication);
        saveRefreshToken(username, newRefreshToken);
        ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_TOKEN, newRefreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(Duration.ofSeconds(REFRESH_TOKEN_EXPIRE_SECONDS))
            .sameSite(STRICT)
            .build();
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(TokenResponseDto.builder()
                .accessToken(accessToken)
                .message("토큰 발급 성공")
                .build());
    }
}
