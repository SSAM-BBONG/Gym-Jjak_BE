package com.ssambbong.gymjjak.chatbot.presentation.internal;

import com.ssambbong.gymjjak.global.infrastructure.config.AiServiceProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * `/internal/chatbot/**`를 FastAPI 서버 간 호출로만 제한하는 필터입니다.
 * 실제 사용자 권한은 이후 서비스에서 활성 세션과 요청 ID로 다시 검증합니다.
 */
@Component
@RequiredArgsConstructor
public class ChatbotInternalApiKeyFilter extends OncePerRequestFilter {

    private static final String INTERNAL_PATH_PREFIX = "/internal/chatbot/";
    private static final String INTERNAL_API_KEY_HEADER = "X-Internal-Api-Key";

    private final AiServiceProperties aiServiceProperties;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith(INTERNAL_PATH_PREFIX);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String providedKey = request.getHeader(INTERNAL_API_KEY_HEADER);
        String configuredKey = aiServiceProperties.getInternalApiKey();

        // 상수 시간 비교로 키 값 비교 과정에서의 불필요한 정보 노출을 줄입니다.
        if (providedKey == null || configuredKey == null || !MessageDigest.isEqual(
                configuredKey.getBytes(StandardCharsets.UTF_8),
                providedKey.getBytes(StandardCharsets.UTF_8)
        )) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
