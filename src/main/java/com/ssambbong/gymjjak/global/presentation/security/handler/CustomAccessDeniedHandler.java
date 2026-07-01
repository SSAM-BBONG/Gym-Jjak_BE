package com.ssambbong.gymjjak.global.presentation.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssambbong.gymjjak.global.domain.auth.AuthErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

// 인가 실패
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {

        AuthErrorCode errorCode = AuthErrorCode.ACCESS_DENIED;

        log.warn(
                "event=access_denied status={} method={} uri={} queryString={} remoteAddr={} xForwardedFor={} userAgent={} message={}",
                errorCode.getHttpStatus().value(),
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                request.getRemoteAddr(),
                request.getHeader("X-Forwarded-For"),
                request.getHeader("User-Agent"),
                accessDeniedException.getMessage()
        );

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorResponse = new LinkedHashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", errorCode.getHttpStatus().value());
        errorResponse.put("error", errorCode.getHttpStatus().getReasonPhrase());
        errorResponse.put("code", errorCode.getCode());
        errorResponse.put("message", errorCode.getMessage());
        errorResponse.put("path", request.getRequestURI());

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
