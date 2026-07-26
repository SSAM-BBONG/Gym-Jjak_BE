package com.ssambbong.gymjjak.chatbot.presentation.internal;

import com.ssambbong.gymjjak.global.infrastructure.config.AiServiceProperties;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockFilterChain;

import static org.assertj.core.api.Assertions.assertThat;

class ChatbotInternalApiKeyFilterTest {

    @Test
    void rejectsAnInternalToolRequestWithoutTheConfiguredApiKey() throws Exception {
        AiServiceProperties properties = new AiServiceProperties();
        properties.setInternalApiKey("tool-key");
        ChatbotInternalApiKeyFilter filter = new ChatbotInternalApiKeyFilter(properties);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/internal/chatbot/tools/inbody/latest");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(401);
    }
}
