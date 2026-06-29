package com.ssambbong.gymjjak.global.infrastructure.security.webSocket;

import com.ssambbong.gymjjak.global.application.auth.port.in.AuthenticateAccessTokenUseCase;
import com.ssambbong.gymjjak.global.domain.auth.AuthException;
import com.ssambbong.gymjjak.global.domain.auth.JwtClaims;
import com.ssambbong.gymjjak.global.presentation.security.JwtAuthenticationConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private static final String COOKIE_NAME = "accessToken";

    private final AuthenticateAccessTokenUseCase authenticateAccessTokenUseCase;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
            String token = sessionAttributes != null ? (String) sessionAttributes.get(COOKIE_NAME) : null;
            if (!StringUtils.hasText(token)) {
                throw new MessageDeliveryException("Access token cookie missing");
            }
            try {
                JwtClaims claims = authenticateAccessTokenUseCase.authenticate(token);
                Authentication authentication = jwtAuthenticationConverter.toAuthentication(claims);
                accessor.setUser(authentication);
            } catch (AuthException e) {
                throw new MessageDeliveryException("Invalid JWT token");
            }
        }

        return message;
    }
}
