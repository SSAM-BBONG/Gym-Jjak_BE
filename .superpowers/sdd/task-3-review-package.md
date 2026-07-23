2488c74a feat: add layered chatbot message history query
 .../query/FindChatbotMessagesQuery.java            |   4 +
 .../result/ChatbotMessageHistoryItem.java          |  19 +++
 .../result/ChatbotMessageHistoryResult.java        |  10 ++
 .../service/ChatbotMessageCursorCodec.java         |  43 ++++++
 .../service/ChatbotMessageQueryService.java        |  89 +++++++++++++
 .../usecase/ChatbotMessageQueryUseCase.java        |   9 ++
 .../InvalidChatbotMessageCursorException.java      |  11 ++
 .../ChatbotMessagePersistenceAdapter.java          |  35 +++++
 .../persistence/ChatbotPersistenceMapper.java      |   8 ++
 .../SpringDataChatbotMessageRepository.java        |  19 +++
 .../service/ChatbotMessageQueryServiceTest.java    | 148 +++++++++++++++++++++
 11 files changed, 395 insertions(+)
diff --git a/src/main/java/com/ssambbong/gymjjak/chatbot/application/query/FindChatbotMessagesQuery.java b/src/main/java/com/ssambbong/gymjjak/chatbot/application/query/FindChatbotMessagesQuery.java
new file mode 100644
index 00000000..415ef058
--- /dev/null
+++ b/src/main/java/com/ssambbong/gymjjak/chatbot/application/query/FindChatbotMessagesQuery.java
@@ -0,0 +1,4 @@
+package com.ssambbong.gymjjak.chatbot.application.query;
+
+public record FindChatbotMessagesQuery(Long userId, String sessionId, String cursor, int size) {
+}
diff --git a/src/main/java/com/ssambbong/gymjjak/chatbot/application/result/ChatbotMessageHistoryItem.java b/src/main/java/com/ssambbong/gymjjak/chatbot/application/result/ChatbotMessageHistoryItem.java
new file mode 100644
index 00000000..1d0d273f
--- /dev/null
+++ b/src/main/java/com/ssambbong/gymjjak/chatbot/application/result/ChatbotMessageHistoryItem.java
@@ -0,0 +1,19 @@
+package com.ssambbong.gymjjak.chatbot.application.result;
+
+import com.fasterxml.jackson.databind.JsonNode;
+import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotMessageRole;
+
+import java.time.LocalDateTime;
+
+public record ChatbotMessageHistoryItem(
+        Long messageId,
+        ChatbotMessageRole role,
+        String content,
+        String intentHint,
+        String category,
+        JsonNode routine,
+        JsonNode sources,
+        Boolean limited,
+        LocalDateTime createdAt
+) {
+}
diff --git a/src/main/java/com/ssambbong/gymjjak/chatbot/application/result/ChatbotMessageHistoryResult.java b/src/main/java/com/ssambbong/gymjjak/chatbot/application/result/ChatbotMessageHistoryResult.java
new file mode 100644
index 00000000..52e040cd
--- /dev/null
+++ b/src/main/java/com/ssambbong/gymjjak/chatbot/application/result/ChatbotMessageHistoryResult.java
@@ -0,0 +1,10 @@
+package com.ssambbong.gymjjak.chatbot.application.result;
+
+import java.util.List;
+
+public record ChatbotMessageHistoryResult(
+        List<ChatbotMessageHistoryItem> messages,
+        String nextCursor,
+        boolean hasNext
+) {
+}
diff --git a/src/main/java/com/ssambbong/gymjjak/chatbot/application/service/ChatbotMessageCursorCodec.java b/src/main/java/com/ssambbong/gymjjak/chatbot/application/service/ChatbotMessageCursorCodec.java
new file mode 100644
index 00000000..5f59034f
--- /dev/null
+++ b/src/main/java/com/ssambbong/gymjjak/chatbot/application/service/ChatbotMessageCursorCodec.java
@@ -0,0 +1,43 @@
+package com.ssambbong.gymjjak.chatbot.application.service;
+
+import com.fasterxml.jackson.core.JsonProcessingException;
+import com.fasterxml.jackson.databind.ObjectMapper;
+import com.ssambbong.gymjjak.chatbot.exception.InvalidChatbotMessageCursorException;
+import lombok.RequiredArgsConstructor;
+import org.springframework.stereotype.Component;
+
+import java.nio.charset.StandardCharsets;
+import java.time.LocalDateTime;
+import java.util.Base64;
+
+@Component
+@RequiredArgsConstructor
+public class ChatbotMessageCursorCodec {
+
+    private final ObjectMapper objectMapper;
+
+    public String encode(LocalDateTime createdAt, Long messageId) {
+        try {
+            String json = objectMapper.writeValueAsString(new CursorPayload(createdAt, messageId));
+            return Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes(StandardCharsets.UTF_8));
+        } catch (JsonProcessingException exception) {
+            throw new IllegalStateException("챗봇 메시지 커서를 생성할 수 없습니다.", exception);
+        }
+    }
+
+    public CursorPayload decode(String cursor) {
+        try {
+            String json = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
+            CursorPayload payload = objectMapper.readValue(json, CursorPayload.class);
+            if (payload == null || payload.createdAt() == null || payload.messageId() == null) {
+                throw new InvalidChatbotMessageCursorException();
+            }
+            return payload;
+        } catch (IllegalArgumentException | JsonProcessingException exception) {
+            throw new InvalidChatbotMessageCursorException();
+        }
+    }
+
+    public record CursorPayload(LocalDateTime createdAt, Long messageId) {
+    }
+}
diff --git a/src/main/java/com/ssambbong/gymjjak/chatbot/application/service/ChatbotMessageQueryService.java b/src/main/java/com/ssambbong/gymjjak/chatbot/application/service/ChatbotMessageQueryService.java
new file mode 100644
index 00000000..03662d12
--- /dev/null
+++ b/src/main/java/com/ssambbong/gymjjak/chatbot/application/service/ChatbotMessageQueryService.java
@@ -0,0 +1,89 @@
+package com.ssambbong.gymjjak.chatbot.application.service;
+
+import com.fasterxml.jackson.core.JsonProcessingException;
+import com.fasterxml.jackson.databind.JsonNode;
+import com.fasterxml.jackson.databind.ObjectMapper;
+import com.ssambbong.gymjjak.chatbot.application.query.FindChatbotMessagesQuery;
+import com.ssambbong.gymjjak.chatbot.application.result.ChatbotMessageHistoryItem;
+import com.ssambbong.gymjjak.chatbot.application.result.ChatbotMessageHistoryResult;
+import com.ssambbong.gymjjak.chatbot.application.usecase.ChatbotMessageQueryUseCase;
+import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotMessage;
+import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotSession;
+import com.ssambbong.gymjjak.chatbot.domain.repository.ChatbotMessageRepository;
+import com.ssambbong.gymjjak.chatbot.domain.repository.ChatbotSessionRepository;
+import com.ssambbong.gymjjak.chatbot.exception.ChatbotErrorCode;
+import com.ssambbong.gymjjak.chatbot.exception.ChatbotSessionException;
+import lombok.RequiredArgsConstructor;
+import org.springframework.stereotype.Service;
+
+import java.util.ArrayList;
+import java.util.Collections;
+import java.util.List;
+
+@Service
+@RequiredArgsConstructor
+public class ChatbotMessageQueryService implements ChatbotMessageQueryUseCase {
+
+    private final ChatbotSessionRepository sessionRepository;
+    private final ChatbotMessageRepository messageRepository;
+    private final ChatbotMessageCursorCodec cursorCodec;
+    private final ObjectMapper objectMapper;
+
+    @Override
+    public ChatbotMessageHistoryResult findMessages(FindChatbotMessagesQuery query) {
+        ChatbotSession session = sessionRepository.findBySessionId(query.sessionId())
+                .orElseThrow(() -> new ChatbotSessionException(ChatbotErrorCode.SESSION_NOT_FOUND));
+        if (!session.isOwnedBy(query.userId())) {
+            throw new ChatbotSessionException(ChatbotErrorCode.SESSION_ACCESS_DENIED);
+        }
+
+        ChatbotMessageCursorCodec.CursorPayload cursor = decodeCursor(query.cursor());
+        List<ChatbotMessage> rows = messageRepository.findHistory(
+                query.sessionId(),
+                cursor == null ? null : cursor.createdAt(),
+                cursor == null ? null : cursor.messageId(),
+                query.size() + 1
+        );
+        boolean hasNext = rows.size() > query.size();
+        List<ChatbotMessage> retainedRows = new ArrayList<>(rows.subList(0, Math.min(rows.size(), query.size())));
+        ChatbotMessage oldestRetainedMessage = retainedRows.get(retainedRows.size() - 1);
+        String nextCursor = hasNext
+                ? cursorCodec.encode(oldestRetainedMessage.createdAt(), oldestRetainedMessage.messageId())
+                : null;
+
+        Collections.reverse(retainedRows);
+        List<ChatbotMessageHistoryItem> messages = retainedRows.stream().map(this::toHistoryItem).toList();
+        return new ChatbotMessageHistoryResult(messages, nextCursor, hasNext);
+    }
+
+    private ChatbotMessageCursorCodec.CursorPayload decodeCursor(String cursor) {
+        if (cursor == null || cursor.isBlank()) {
+            return null;
+        }
+        return cursorCodec.decode(cursor);
+    }
+
+    private ChatbotMessageHistoryItem toHistoryItem(ChatbotMessage message) {
+        return new ChatbotMessageHistoryItem(
+                message.messageId(), message.role(), message.content(), message.intentHint(), message.category(),
+                parseNullableJson(message.routineJson()), parseSources(message.sourcesJson()),
+                message.limited(), message.createdAt()
+        );
+    }
+
+    private JsonNode parseNullableJson(String value) {
+        return value == null ? null : readJson(value);
+    }
+
+    private JsonNode parseSources(String value) {
+        return value == null ? objectMapper.createArrayNode() : readJson(value);
+    }
+
+    private JsonNode readJson(String value) {
+        try {
+            return objectMapper.readTree(value);
+        } catch (JsonProcessingException exception) {
+            throw new IllegalStateException("저장된 챗봇 메시지 메타데이터를 읽을 수 없습니다.", exception);
+        }
+    }
+}
diff --git a/src/main/java/com/ssambbong/gymjjak/chatbot/application/usecase/ChatbotMessageQueryUseCase.java b/src/main/java/com/ssambbong/gymjjak/chatbot/application/usecase/ChatbotMessageQueryUseCase.java
new file mode 100644
index 00000000..d905fba8
--- /dev/null
+++ b/src/main/java/com/ssambbong/gymjjak/chatbot/application/usecase/ChatbotMessageQueryUseCase.java
@@ -0,0 +1,9 @@
+package com.ssambbong.gymjjak.chatbot.application.usecase;
+
+import com.ssambbong.gymjjak.chatbot.application.query.FindChatbotMessagesQuery;
+import com.ssambbong.gymjjak.chatbot.application.result.ChatbotMessageHistoryResult;
+
+public interface ChatbotMessageQueryUseCase {
+
+    ChatbotMessageHistoryResult findMessages(FindChatbotMessagesQuery query);
+}
diff --git a/src/main/java/com/ssambbong/gymjjak/chatbot/exception/InvalidChatbotMessageCursorException.java b/src/main/java/com/ssambbong/gymjjak/chatbot/exception/InvalidChatbotMessageCursorException.java
new file mode 100644
index 00000000..10847289
--- /dev/null
+++ b/src/main/java/com/ssambbong/gymjjak/chatbot/exception/InvalidChatbotMessageCursorException.java
@@ -0,0 +1,11 @@
+package com.ssambbong.gymjjak.chatbot.exception;
+
+import com.ssambbong.gymjjak.global.domain.common.exception.BadRequestException;
+import com.ssambbong.gymjjak.global.domain.common.exception.CommonErrorCode;
+
+public class InvalidChatbotMessageCursorException extends BadRequestException {
+
+    public InvalidChatbotMessageCursorException() {
+        super(CommonErrorCode.INVALID_INPUT);
+    }
+}
diff --git a/src/main/java/com/ssambbong/gymjjak/chatbot/infrastructure/persistence/ChatbotMessagePersistenceAdapter.java b/src/main/java/com/ssambbong/gymjjak/chatbot/infrastructure/persistence/ChatbotMessagePersistenceAdapter.java
new file mode 100644
index 00000000..e1d35689
--- /dev/null
+++ b/src/main/java/com/ssambbong/gymjjak/chatbot/infrastructure/persistence/ChatbotMessagePersistenceAdapter.java
@@ -0,0 +1,35 @@
+package com.ssambbong.gymjjak.chatbot.infrastructure.persistence;
+
+import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotMessage;
+import com.ssambbong.gymjjak.chatbot.domain.repository.ChatbotMessageRepository;
+import lombok.RequiredArgsConstructor;
+import org.springframework.data.domain.PageRequest;
+import org.springframework.stereotype.Repository;
+
+import java.time.LocalDateTime;
+import java.util.List;
+
+@Repository
+@RequiredArgsConstructor
+public class ChatbotMessagePersistenceAdapter implements ChatbotMessageRepository {
+
+    private final SpringDataChatbotMessageRepository springDataChatbotMessageRepository;
+    private final ChatbotPersistenceMapper mapper;
+
+    @Override
+    public List<ChatbotMessage> findHistory(
+            String sessionId,
+            LocalDateTime cursorCreatedAt,
+            Long cursorMessageId,
+            int limit
+    ) {
+        return springDataChatbotMessageRepository.findHistory(
+                        sessionId,
+                        cursorCreatedAt,
+                        cursorMessageId,
+                        PageRequest.of(0, limit)
+                ).stream()
+                .map(mapper::toDomain)
+                .toList();
+    }
+}
diff --git a/src/main/java/com/ssambbong/gymjjak/chatbot/infrastructure/persistence/ChatbotPersistenceMapper.java b/src/main/java/com/ssambbong/gymjjak/chatbot/infrastructure/persistence/ChatbotPersistenceMapper.java
index d7df23f2..7ed7e76c 100644
--- a/src/main/java/com/ssambbong/gymjjak/chatbot/infrastructure/persistence/ChatbotPersistenceMapper.java
+++ b/src/main/java/com/ssambbong/gymjjak/chatbot/infrastructure/persistence/ChatbotPersistenceMapper.java
@@ -1,19 +1,27 @@
 package com.ssambbong.gymjjak.chatbot.infrastructure.persistence;
 
 import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotSession;
 import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotSessionSummary;
+import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotMessage;
 import org.springframework.stereotype.Component;
 
 @Component
 public class ChatbotPersistenceMapper {
 
     public ChatbotSession toDomain(ChatbotSessionJpaEntity entity) {
         return new ChatbotSession(entity.getSessionId(), entity.getUserId(), entity.getLastActivityAt());
     }
 
     public ChatbotSessionSummary toSummary(ChatbotSessionListRow row) {
         return new ChatbotSessionSummary(
                 row.getSessionId(), row.getTitle(), row.getLastMessage(), row.getLastActivityAt()
         );
     }
+
+    public ChatbotMessage toDomain(ChatbotMessageJpaEntity entity) {
+        return new ChatbotMessage(
+                entity.getId(), entity.getSessionId(), entity.getRole(), entity.getContent(), entity.getIntentHint(),
+                entity.getCategory(), entity.getRoutineJson(), entity.getSourcesJson(), entity.getLimited(), entity.getCreatedAt()
+        );
+    }
 }
diff --git a/src/main/java/com/ssambbong/gymjjak/chatbot/infrastructure/persistence/SpringDataChatbotMessageRepository.java b/src/main/java/com/ssambbong/gymjjak/chatbot/infrastructure/persistence/SpringDataChatbotMessageRepository.java
index cb62c97e..2b6edd03 100644
--- a/src/main/java/com/ssambbong/gymjjak/chatbot/infrastructure/persistence/SpringDataChatbotMessageRepository.java
+++ b/src/main/java/com/ssambbong/gymjjak/chatbot/infrastructure/persistence/SpringDataChatbotMessageRepository.java
@@ -1,10 +1,29 @@
 package com.ssambbong.gymjjak.chatbot.infrastructure.persistence;
 
+import org.springframework.data.domain.Pageable;
 import org.springframework.data.jpa.repository.JpaRepository;
+import org.springframework.data.jpa.repository.Query;
+import org.springframework.data.repository.query.Param;
 
+import java.time.LocalDateTime;
 import java.util.List;
 
 public interface SpringDataChatbotMessageRepository extends JpaRepository<ChatbotMessageJpaEntity, Long> {
 
     List<ChatbotMessageJpaEntity> findTop12BySessionIdOrderByCreatedAtDesc(String sessionId);
+
+    @Query("""
+            SELECT m FROM ChatbotMessageJpaEntity m
+            WHERE m.sessionId = :sessionId
+              AND (:cursorCreatedAt IS NULL
+                   OR m.createdAt < :cursorCreatedAt
+                   OR (m.createdAt = :cursorCreatedAt AND m.id < :cursorMessageId))
+            ORDER BY m.createdAt DESC, m.id DESC
+            """)
+    List<ChatbotMessageJpaEntity> findHistory(
+            @Param("sessionId") String sessionId,
+            @Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
+            @Param("cursorMessageId") Long cursorMessageId,
+            Pageable pageable
+    );
 }
diff --git a/src/test/java/com/ssambbong/gymjjak/chatbot/application/service/ChatbotMessageQueryServiceTest.java b/src/test/java/com/ssambbong/gymjjak/chatbot/application/service/ChatbotMessageQueryServiceTest.java
new file mode 100644
index 00000000..37f64bc5
--- /dev/null
+++ b/src/test/java/com/ssambbong/gymjjak/chatbot/application/service/ChatbotMessageQueryServiceTest.java
@@ -0,0 +1,148 @@
+package com.ssambbong.gymjjak.chatbot.application.service;
+
+import com.fasterxml.jackson.databind.ObjectMapper;
+import com.ssambbong.gymjjak.chatbot.application.query.FindChatbotMessagesQuery;
+import com.ssambbong.gymjjak.chatbot.application.result.ChatbotMessageHistoryResult;
+import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotMessage;
+import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotMessageRole;
+import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotSession;
+import com.ssambbong.gymjjak.chatbot.domain.repository.ChatbotMessageRepository;
+import com.ssambbong.gymjjak.chatbot.domain.repository.ChatbotSessionRepository;
+import com.ssambbong.gymjjak.chatbot.exception.ChatbotSessionException;
+import com.ssambbong.gymjjak.chatbot.exception.InvalidChatbotMessageCursorException;
+import org.junit.jupiter.api.BeforeEach;
+import org.junit.jupiter.api.Test;
+import org.junit.jupiter.api.extension.ExtendWith;
+import org.mockito.Mock;
+import org.mockito.junit.jupiter.MockitoExtension;
+
+import java.time.LocalDateTime;
+import java.util.List;
+import java.util.Optional;
+
+import static org.assertj.core.api.Assertions.assertThat;
+import static org.assertj.core.api.Assertions.assertThatThrownBy;
+import static org.mockito.Mockito.verify;
+import static org.mockito.Mockito.verifyNoInteractions;
+import static org.mockito.Mockito.when;
+
+@ExtendWith(MockitoExtension.class)
+class ChatbotMessageQueryServiceTest {
+
+    private static final long USER_ID = 7L;
+    private static final String SESSION_ID = "session-1";
+
+    @Mock private ChatbotSessionRepository sessionRepository;
+    @Mock private ChatbotMessageRepository messageRepository;
+
+    private ChatbotMessageQueryService service;
+
+    @BeforeEach
+    void setUp() {
+        service = new ChatbotMessageQueryService(
+                sessionRepository,
+                messageRepository,
+                new ChatbotMessageCursorCodec(new ObjectMapper().findAndRegisterModules()),
+                new ObjectMapper().findAndRegisterModules()
+        );
+    }
+
+    @Test
+    void returnsMessagesOldestFirstAndNextCursorWhenAnExtraMessageExists() {
+        LocalDateTime createdAt = LocalDateTime.of(2026, 7, 23, 10, 0);
+        ChatbotMessage newest = message(3L, createdAt.plusMinutes(2), "newest");
+        ChatbotMessage middle = message(2L, createdAt.plusMinutes(1), "middle");
+        ChatbotMessage oldest = message(1L, createdAt, "oldest");
+        ownedSession();
+        when(messageRepository.findHistory(SESSION_ID, null, null, 3))
+                .thenReturn(List.of(newest, middle, oldest));
+
+        ChatbotMessageHistoryResult result = service.findMessages(
+                new FindChatbotMessagesQuery(USER_ID, SESSION_ID, null, 2)
+        );
+
+        assertThat(result.messages()).extracting(item -> item.messageId()).containsExactly(2L, 3L);
+        assertThat(result.hasNext()).isTrue();
+        assertThat(result.nextCursor()).isNotBlank();
+        verify(messageRepository).findHistory(SESSION_ID, null, null, 3);
+    }
+
+    @Test
+    void returnsNoNextCursorWhenMessagesFitRequestedSize() {
+        ChatbotMessage onlyMessage = message(1L, LocalDateTime.of(2026, 7, 23, 10, 0), "only");
+        ownedSession();
+        when(messageRepository.findHistory(SESSION_ID, null, null, 3)).thenReturn(List.of(onlyMessage));
+
+        ChatbotMessageHistoryResult result = service.findMessages(
+                new FindChatbotMessagesQuery(USER_ID, SESSION_ID, null, 2)
+        );
+
+        assertThat(result.messages()).extracting(item -> item.messageId()).containsExactly(1L);
+        assertThat(result.hasNext()).isFalse();
+        assertThat(result.nextCursor()).isNull();
+    }
+
+    @Test
+    void throwsSessionNotFoundWithoutQueryingMessageHistory() {
+        when(sessionRepository.findBySessionId(SESSION_ID)).thenReturn(Optional.empty());
+
+        assertThatThrownBy(() -> service.findMessages(new FindChatbotMessagesQuery(USER_ID, SESSION_ID, null, 20)))
+                .isInstanceOf(ChatbotSessionException.class)
+                .hasMessageContaining("챗봇 세션");
+
+        verifyNoInteractions(messageRepository);
+    }
+
+    @Test
+    void throwsAccessDeniedWithoutQueryingMessageHistoryForAnotherUsersSession() {
+        when(sessionRepository.findBySessionId(SESSION_ID))
+                .thenReturn(Optional.of(new ChatbotSession(SESSION_ID, USER_ID + 1, LocalDateTime.now())));
+
+        assertThatThrownBy(() -> service.findMessages(new FindChatbotMessagesQuery(USER_ID, SESSION_ID, null, 20)))
+                .isInstanceOf(ChatbotSessionException.class);
+
+        verifyNoInteractions(messageRepository);
+    }
+
+    @Test
+    void throwsInvalidCursorExceptionWithoutQueryingMessageHistoryForMalformedCursor() {
+        ownedSession();
+
+        assertThatThrownBy(() -> service.findMessages(new FindChatbotMessagesQuery(USER_ID, SESSION_ID, "not-base64", 20)))
+                .isInstanceOf(InvalidChatbotMessageCursorException.class);
+        assertThatThrownBy(() -> service.findMessages(new FindChatbotMessagesQuery(USER_ID, SESSION_ID, "bnVsbA", 20)))
+                .isInstanceOf(InvalidChatbotMessageCursorException.class);
+
+        verifyNoInteractions(messageRepository);
+    }
+
+    @Test
+    void mapsAssistantMetadataAndUsesEmptyArrayForNullSources() {
+        ChatbotMessage assistant = new ChatbotMessage(
+                1L, SESSION_ID, ChatbotMessageRole.ASSISTANT, "answer", null, "ROUTINE",
+                "{\"name\":\"routine\"}", null, false, LocalDateTime.of(2026, 7, 23, 10, 0)
+        );
+        ownedSession();
+        when(messageRepository.findHistory(SESSION_ID, null, null, 21)).thenReturn(List.of(assistant));
+
+        ChatbotMessageHistoryResult result = service.findMessages(
+                new FindChatbotMessagesQuery(USER_ID, SESSION_ID, null, 20)
+        );
+
+        assertThat(result.messages().get(0).routine().get("name").asText()).isEqualTo("routine");
+        assertThat(result.messages().get(0).sources().isArray()).isTrue();
+        assertThat(result.messages().get(0).sources()).isEmpty();
+    }
+
+    private void ownedSession() {
+        when(sessionRepository.findBySessionId(SESSION_ID))
+                .thenReturn(Optional.of(new ChatbotSession(SESSION_ID, USER_ID, LocalDateTime.now())));
+    }
+
+    private ChatbotMessage message(Long id, LocalDateTime createdAt, String content) {
+        return new ChatbotMessage(
+                id, SESSION_ID, ChatbotMessageRole.USER, content, null, null,
+                null, null, null, createdAt
+        );
+    }
+}
