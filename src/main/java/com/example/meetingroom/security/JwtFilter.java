package com.example.meetingroom.security;

import com.example.meetingroom.util.TokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtFilter.class);
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final TokenProvider tokenProvider;

    public JwtFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String jwt = resolveToken(request);

        if (StringUtils.hasText(jwt)) {
            try {
                // getAuthentication 메소드가 내부적으로 토큰 유효성을 검증하고 예외를 던진다고 가정합니다.
                Authentication authentication = tokenProvider.getAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                LOGGER.info("Authenticated user: {}, uri: {}", authentication.getName(), request.getRequestURI());
            } catch (SecurityException | MalformedJwtException e) {
                LOGGER.warn("Invalid JWT signature, uri: {}", request.getRequestURI());
                setErrorResponse(response, HttpStatus.UNAUTHORIZED, "잘못된 JWT 서명입니다.");
                return; // 인증 실패 시 요청 처리 중단
            } catch (ExpiredJwtException e) {
                LOGGER.warn("Expired JWT token, uri: {}", request.getRequestURI());
                setErrorResponse(response, HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰입니다.");
                return; // 인증 실패 시 요청 처리 중단
            } catch (UnsupportedJwtException e) {
                LOGGER.warn("Unsupported JWT token, uri: {}", request.getRequestURI());
                setErrorResponse(response, HttpStatus.UNAUTHORIZED, "지원되지 않는 JWT 토큰입니다.");
                return; // 인증 실패 시 요청 처리 중단
            } catch (IllegalArgumentException e) {
                LOGGER.warn("JWT token compact of handler are invalid, uri: {}", request.getRequestURI());
                setErrorResponse(response, HttpStatus.UNAUTHORIZED, "JWT 토큰이 잘못되었습니다.");
                return; // 인증 실패 시 요청 처리 중단
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private void setErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"status\": " + status.value() + ", \"message\": \"" + message + "\"}");
    }
}