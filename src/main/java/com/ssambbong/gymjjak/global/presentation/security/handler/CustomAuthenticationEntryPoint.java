package com.ssambbong.gymjjak.global.presentation.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssambbong.gymjjak.global.domain.auth.AuthErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

// 인증 실패
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {

        AuthErrorCode authErrorCode = resolveAuthErrorCode(request);

        response.setStatus(authErrorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorResponse = new LinkedHashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", authErrorCode.getHttpStatus().value());
        errorResponse.put("error", authErrorCode.getHttpStatus().getReasonPhrase());
        errorResponse.put("code", authErrorCode.getCode());
        errorResponse.put("message", authErrorCode.getMessage());
        errorResponse.put("path", request.getRequestURI());

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }

    private AuthErrorCode resolveAuthErrorCode(HttpServletRequest request) {
        Object exception = request.getAttribute("authErrorCode");

        if (exception instanceof AuthErrorCode authErrorCode) {
            return authErrorCode;
        }

        return AuthErrorCode.ACCESS_TOKEN_MISSING;
    }
}
