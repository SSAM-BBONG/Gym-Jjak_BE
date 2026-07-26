package com.ssambbong.gymjjak.global.infrastructure.security.config;

import com.ssambbong.gymjjak.global.infrastructure.security.oauth.OAuth2SuccessHandler;
import com.ssambbong.gymjjak.global.presentation.security.JwtAuthenticationFilter;
import com.ssambbong.gymjjak.chatbot.presentation.internal.ChatbotInternalApiKeyFilter;
import com.ssambbong.gymjjak.global.presentation.security.handler.CustomAccessDeniedHandler;
import com.ssambbong.gymjjak.global.presentation.security.handler.CustomAuthenticationEntryPoint;
import com.ssambbong.gymjjak.global.infrastructure.security.jwt.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
    private final ChatbotInternalApiKeyFilter chatbotInternalApiKeyFilter;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

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

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers("/api/auth/logout").authenticated()

                        // 회원가입, 로그인, 토큰 재발급 등 인증 없이 접근 가능
                        // Swagger 사용 시 허용
                        .requestMatchers(
                                "/api/auth/**",
                                "/oauth2/**",
                                "/login/oauth2/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/ws/**",
                                "/error"
                        ).permitAll()

                        .requestMatchers("/api/token/reissue").permitAll()

                        .requestMatchers("/api/token/validate").authenticated()

                        // FastAPI 서버 간 챗봇 도구 API: 별도 API 키 필터가 인증을 수행함
                        .requestMatchers("/internal/chatbot/**").permitAll()

                        .requestMatchers("/api/onboarding/**")
                        .hasAnyAuthority("USER", "ADMIN")
                        
                        // 신고 관리
                        .requestMatchers("/api/reportgroup/**")
                        .hasAnyAuthority("ADMIN")

                        // 부위 API
                        .requestMatchers(HttpMethod.POST, "/api/parts/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/parts/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/parts/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/parts/**").hasAnyAuthority("ADMIN", "TRAINER", "USER")

                        // PT 추천 API — 구독 여부 무관 전 회원 무료 제공
                        .requestMatchers(HttpMethod.POST, "/api/pt-recommendations").hasAnyAuthority("TRAINER", "USER")

                        // PT API
                        .requestMatchers(HttpMethod.GET, "/api/trainer-profiles/*/reviews").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/trainer-profiles/*/reviews/summary").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/pt-courses/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/pt-courses/*/reservations").hasAnyAuthority("USER", "TRAINER")
                        .requestMatchers(HttpMethod.POST, "/api/pt-courses/*/reservations/*/reviews").hasAnyAuthority("USER", "TRAINER")
                        .requestMatchers(HttpMethod.POST, "/api/pt-courses/**")
                        .hasAnyAuthority("TRAINER")


                        .requestMatchers("/api/reports/**").authenticated()

                        // 일반 사용자 API
                        // 관리자가 사용자 API도 접근 가능해야 하면 ROLE_ADMIN 포함
                        .requestMatchers("/api/users/**")

                        .hasAnyAuthority("ADMIN", "USER","TRAINER","ORGANIZATION")
                                // 트레이너 신청 목록 조회 - 조직
                                .requestMatchers(HttpMethod.GET, "/api/trainer-applications")
                                .hasAuthority("ORGANIZATION")

                                // 내 트레이너 신청서 목록 조회 - 사용자/트레이너
                                .requestMatchers(HttpMethod.GET, "/api/trainer-applications/me")
                                .hasAnyAuthority("USER", "TRAINER")

                                // 내 트레이너 신청서 상세 조회 - 사용자/트레이너
                                .requestMatchers(HttpMethod.GET, "/api/trainer-applications/me/*")
                                .hasAnyAuthority("USER", "TRAINER")

                                // 트레이너 신청 상세 조회 - 조직
                                .requestMatchers(HttpMethod.GET, "/api/trainer-applications/*")
                                .hasAuthority("ORGANIZATION")

                                // 트레이너 신청 생성 - 사용자
                                .requestMatchers(HttpMethod.POST, "/api/trainer-applications")
                                .hasAuthority("USER")

                                // 트레이너 신청 승인 - 조직
                                .requestMatchers(HttpMethod.PATCH, "/api/trainer-applications/*/approve")
                                .hasAuthority("ORGANIZATION")

                                // 트레이너 신청 반려 - 조직
                                .requestMatchers(HttpMethod.PATCH, "/api/trainer-applications/*/reject")
                                .hasAuthority("ORGANIZATION")

                                // 트레이너 신청 수정/취소 - 사용자
                                .requestMatchers(HttpMethod.PATCH, "/api/trainer-applications/*")
                                .hasAuthority("USER")

                        // 트레이너 검색 - 조직 및 관리자
                        .requestMatchers(HttpMethod.GET, "/api/trainers/search")
                        .hasAnyAuthority("ORGANIZATION", "ADMIN")

                        // 본인 프로필 조회는 TRAINER만 허용
                        .requestMatchers(HttpMethod.GET, "/api/trainers/me")
                        .hasAuthority("TRAINER")

                        // 프로필 ID로 트레이너 프로필 상세 조회
                        .requestMatchers(HttpMethod.GET, "/api/trainers/*")
                        .permitAll()

                        // 트레이너 API
                        .requestMatchers("/api/trainers/**")
                        .hasAnyAuthority("TRAINER", "ADMIN")

                        // 트레이너 메인 대시보드
                        .requestMatchers(HttpMethod.GET, "/api/dashboard/trainer/main")
                        .hasAuthority("TRAINER")

                        // 조직 API
                        .requestMatchers(HttpMethod.GET, "/api/organizations/*/detail").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/organizations/search")
                        .hasAnyAuthority("USER", "TRAINER")
                        .requestMatchers(HttpMethod.GET, "/api/organizations/trainer/my-organizations")
                        .hasAuthority("TRAINER")
                        .requestMatchers("/api/organizations/**")
                        .hasAnyAuthority("ORGANIZATION", "ADMIN")

                        .requestMatchers("/api/organization-applications/**")
                        .hasAnyAuthority("USER", "TRAINER", "ADMIN")

                        // Metric 관련 API
                        .requestMatchers(
                                "/actuator/health",
                                "/actuator/info",
                                "/actuator/prometheus"
                        ).permitAll()
                        .requestMatchers("/actuator/**").hasAuthority("ADMIN")

                        // Calendar API
                        .requestMatchers("/api/calendar/**")
                        .authenticated()

                        // Community API
                        .requestMatchers("/api/community/**")
                        .permitAll()

                        // 결제 웹훅 (PortOne 서버에서 호출)
                        .requestMatchers(HttpMethod.POST, "/api/payments/webhook").permitAll()

                        // 구독 플랜 목록 조회 (비로그인 접근 가능)
                        .requestMatchers(HttpMethod.GET, "/api/subscriptions/plans").permitAll()

                        // 내 구독 조회
                        .requestMatchers(HttpMethod.GET, "/api/subscriptions/me").hasAnyAuthority("USER", "TRAINER")

                        // 구독 결제 요청
                        .requestMatchers(HttpMethod.POST, "/api/payments/subscriptions").hasAnyAuthority("USER", "TRAINER")

                        // 그 외 요청은 인증 필요
                        .anyRequest().authenticated()
                )

                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2SuccessHandler)
                )

                // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 등록
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .addFilterBefore(
                        chatbotInternalApiKeyFilter,
                        JwtAuthenticationFilter.class
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
                "http://localhost:8080",
                // 프론트 배포 주소
//                "http://13.209.67.161",
//                "https://13.124.200.97.sslip.io"
//                "https://gymjjak.com"
                "https://gymjjak.site",
                "https://www.gymjjak.site"
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
}
