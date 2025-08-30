package com.example.meetingroom.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
            .title("회의실 예약 시스템 API")
            .version("v1.0.0")
            .description("회의실 예약 시스템 API 명세서입니다.");

        // 1. 재사용할 예시(Example) 정의
        Example userAlreadyExistedExample = new Example()
            .summary("중복된 사용자 ID")
            .description("이미 시스템에 동일한 사용자 ID가 존재할 경우의 에러 응답입니다.")
            .value("""
                   {
                       "timestamp": "2023-10-27T12:00:00.000Z",
                       "status": 409",
                       "error": "Conflict",
                       "code": "USER_ALREADY_EXISTED",
                       "message": "이미 존재하는 사용자입니다."
                   }
                """);

        String jwtSchemeName = "Authorization";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

        // 2. Components 객체에 예시와 인증 스키마 추가
        Components components = new Components()
            .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                .name(jwtSchemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT"))
            .addExamples("userAlreadyExistedExample", userAlreadyExistedExample); // 예시 추가

        return new OpenAPI()
            .info(info)
            .addSecurityItem(securityRequirement)
            .components(components);
    }
}
