package com.ssambbong.gymjjak.chat.infrastructure.metrics;

import com.ssambbong.gymjjak.chat.application.port.ChatMetricsPort;
import com.ssambbong.gymjjak.chat.domain.model.ChatRoomStatus;
import com.ssambbong.gymjjak.chat.domain.repository.ChatRoomRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class ChatMetrics implements ChatMetricsPort {

    private final MeterRegistry meterRegistry;
    private final Counter messageSentCounter;
    private final Counter chatRoomCreatedCounter;

    public ChatMetrics(MeterRegistry meterRegistry, ChatRoomRepository chatRoomRepository) {
        this.meterRegistry = meterRegistry;

        this.messageSentCounter = Counter.builder("gymjjak.chat.message.sent")
                .description("채팅 메시지 전송 횟수")
                .register(meterRegistry);

        this.chatRoomCreatedCounter = Counter.builder("gymjjak.chat.room.created")
                .description("채팅방 생성 횟수")
                .register(meterRegistry);

        Gauge.builder("gymjjak.chat.room.active", chatRoomRepository, ChatRoomRepository::countActive)
                .description("활성 채팅방 수")
                .register(meterRegistry);
    }

    @Override
    public void recordMessageSent() {
        messageSentCounter.increment();
    }

    @Override
    public void recordChatRoomCreated() {
        chatRoomCreatedCounter.increment();
    }

    @Override
    public void recordWebSocketError(String errorType) {
        Counter.builder("gymjjak.chat.websocket.error")
                .description("WebSocket 에러 횟수")
                .tag("type", errorType)
                .register(meterRegistry)
                .increment();
    }
}
