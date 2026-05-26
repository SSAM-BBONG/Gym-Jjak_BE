package com.ssambbong.gymjjak.global.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    // TODO : 주원이가 사용할 토큰 담는 객체
    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }

    private Info apiInfo() {
        return new Info()
                .title("GymJjak API")
                .version("v1.0.0")
                .description("짐짝 프로젝트 API 문서");
    }

    // TODO : 주원이가 SecurityComponents 관련 추가
}
