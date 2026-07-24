package com.ssambbong.gymjjak.chatbot.infrastructure.adapter.out.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotAiEvent;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotAiRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ChatbotFastApiClientAdapterTest {

    private MockRestServiceServer server;
    private ChatbotFastApiClientAdapter adapter;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://fastapi.test");
        server = MockRestServiceServer.bindTo(builder).build();
        adapter = new ChatbotFastApiClientAdapter(builder.build(), new ObjectMapper());
    }

    @Test
    void streamsFastApiSseUsingSpringGeneratedActorMemoryAndRequestId() {
        server.expect(requestTo("http://fastapi.test/api/v1/chatbot/messages"))
                .andExpect(header("X-Request-ID", "request-123"))
                .andExpect(content().json("""
                        {
                          "session_id": "session-123",
                          "message": "루틴을 추천해줘",
                          "intent_hint": "ROUTINE_RECOMMENDATION",
                          "actor": {"user_id": 7, "role": "USER"},
                          "memory": {
                            "summary": "주 3회 운동",
                            "recent_messages": [{"role": "user", "content": "이전 질문"}],
                            "contexts": [{"kind": "PAIN", "value": "무릎 통증"}]
                          }
                        }
                        """))
                .andRespond(withSuccess("""
                        event: delta
                        data: {"text":"이번 주는 "}

                        event: done
                        data: {"session_id":"session-123","answer":"하체 운동을 2회로 나눠보세요.","category":"ROUTINE","routine":{"days":2},"sources":[{"title":"ACSM"}],"limited":false,"quick_replies":[{"question_id":"ROUTINE_GOAL","label":"근육 증가","value":"MUSCLE_GAIN"}]}

                        """, MediaType.TEXT_EVENT_STREAM));

        List<ChatbotAiEvent> events = new ArrayList<>();

        adapter.stream(request(), events::add);

        assertThat(events).containsExactly(
                new ChatbotAiEvent.Delta("이번 주는 "),
                new ChatbotAiEvent.Done(
                        "session-123", "하체 운동을 2회로 나눠보세요.", "ROUTINE",
                        "{\"days\":2}", "[{\"title\":\"ACSM\"}]", false,
                        "[{\"question_id\":\"ROUTINE_GOAL\",\"label\":\"근육 증가\",\"value\":\"MUSCLE_GAIN\"}]"
                )
        );
        server.verify();
    }

    private ChatbotAiRequest request() {
        return new ChatbotAiRequest(
                "session-123",
                "루틴을 추천해줘",
                "ROUTINE_RECOMMENDATION",
                new ChatbotAiRequest.Actor(7L, "USER"),
                new ChatbotAiRequest.Memory(
                        "주 3회 운동",
                        List.of(new ChatbotAiRequest.Message("user", "이전 질문")),
                        List.of(new ChatbotAiRequest.Context("PAIN", "무릎 통증"))
                ),
                "request-123"
        );
    }
}
