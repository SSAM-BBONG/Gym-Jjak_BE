package com.ssambbong.gymjjak.global.security.config;

import com.ssambbong.gymjjak.global.security.handler.CustomAccessDeniedHandler;
import com.ssambbong.gymjjak.global.security.handler.CustomAuthenticationEntryPoint;
import com.ssambbong.gymjjak.global.security.jwt.JwtAuthenticationFilter;
import com.ssambbong.gymjjak.global.security.jwt.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(JwtProperties.class)
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                // CORS 설정 적용
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // JWT 기반 Stateless 인증에서는 CSRF 비활성화
                .csrf(csrf -> csrf.disable())

                // JWT API 서버에서는 기본 로그인 방식 비활성화
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .logout(logout -> logout.disable())

                // 세션 사용하지 않음
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // URL별 인증/인가 설정
                .authorizeHttpRequests(auth -> auth

                        // 회원가입, 로그인, 토큰 재발급 등 인증 없이 접근 가능
                        .requestMatchers("/api/auth/**").permitAll()

                        // Swagger 사용 시 허용
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // 신고 관리
                        .requestMatchers("/api/reportgroup/**")
//                        .hasAnyAuthority("ADMIN")
                        // 임시 설정
                        .permitAll()

                        .requestMatchers("/api/reports/**")
                        .hasAnyAuthority("ADMIN")

                        // 일반 사용자 API
                        // 관리자가 사용자 API도 접근 가능해야 하면 ROLE_ADMIN 포함
                        .requestMatchers("/api/users/**")
                        .hasAnyAuthority("ADMIN", "USER","TRAINER")

                        // 트레이너 API
                        .requestMatchers("/api/trainers/**")
                        .hasAnyAuthority("TRAINER", "ADMIN")

                        // 조직 API
                        .requestMatchers("/api/organizations/**")
                        .hasAnyAuthority("ORGANIZATION", "ADMIN")

                        .requestMatchers("/api/organization-applications/**")
                        .hasAnyAuthority("USER", "TRAINER", "ADMIN")

                        .requestMatchers("/api/organizations/**")
                        .hasAnyAuthority("TRAINER", "ADMIN")

                        // 그 외 요청은 인증 필요
                        .anyRequest().authenticated()
                )

                // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 등록
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                // 인증/인가 예외 처리
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 프론트엔드 주소 허용
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:3000",
                "http://localhost:8080"
//                "https://gymjjak.com"
        ));

        // 허용 HTTP 메서드
        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
        ));

        // 요청 허용 헤더
        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-Requested-With",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers",
                "X-Refresh-Token"
        ));

        // 프론트에서 읽을 수 있게 노출할 응답 헤더
        configuration.setExposedHeaders(List.of(
                "Authorization",
                "New-Access-Token"
        ));

        // 쿠키, Authorization 헤더 등 인증 정보 포함 허용
        configuration.setAllowCredentials(true);

        // Preflight 요청 캐시 시간
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
