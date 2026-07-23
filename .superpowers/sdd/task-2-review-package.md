0483fb3a refactor: route chatbot session query through domain repository
 .../service/ChatbotSessionQueryService.java        | 16 +++++-----
 .../persistence/ChatbotPersistenceMapper.java      | 19 ++++++++++++
 .../ChatbotSessionPersistenceAdapter.java          | 36 ++++++++++++++++++++++
 .../service/ChatbotSessionQueryServiceTest.java    | 34 ++++++++++----------
 4 files changed, 79 insertions(+), 26 deletions(-)
diff --git a/src/main/java/com/ssambbong/gymjjak/chatbot/application/service/ChatbotSessionQueryService.java b/src/main/java/com/ssambbong/gymjjak/chatbot/application/service/ChatbotSessionQueryService.java
index 4d335ab8..3775f04e 100644
--- a/src/main/java/com/ssambbong/gymjjak/chatbot/application/service/ChatbotSessionQueryService.java
+++ b/src/main/java/com/ssambbong/gymjjak/chatbot/application/service/ChatbotSessionQueryService.java
@@ -1,56 +1,56 @@
 package com.ssambbong.gymjjak.chatbot.application.service;
 
 import com.ssambbong.gymjjak.chatbot.application.query.FindChatbotSessionsQuery;
 import com.ssambbong.gymjjak.chatbot.application.result.ChatbotSessionListItem;
 import com.ssambbong.gymjjak.chatbot.application.result.ChatbotSessionListResult;
 import com.ssambbong.gymjjak.chatbot.application.usecase.ChatbotSessionQueryUseCase;
-import com.ssambbong.gymjjak.chatbot.infrastructure.persistence.ChatbotSessionListRow;
-import com.ssambbong.gymjjak.chatbot.infrastructure.persistence.SpringDataChatbotSessionRepository;
+import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotSessionSummary;
+import com.ssambbong.gymjjak.chatbot.domain.repository.ChatbotSessionRepository;
 import lombok.RequiredArgsConstructor;
 import org.springframework.stereotype.Service;
 
 import java.time.LocalDateTime;
 import java.util.List;
 
 @Service
 @RequiredArgsConstructor
 public class ChatbotSessionQueryService implements ChatbotSessionQueryUseCase {
 
-    private final SpringDataChatbotSessionRepository sessionRepository;
+    private final ChatbotSessionRepository sessionRepository;
     private final ChatbotSessionCursorCodec cursorCodec;
 
     @Override
     public ChatbotSessionListResult findSessions(FindChatbotSessionsQuery query) {
         ChatbotSessionCursorCodec.CursorPayload cursor = decodeCursor(query.cursor());
-        List<ChatbotSessionListRow> rows = sessionRepository.findSessionList(
+        List<ChatbotSessionSummary> summaries = sessionRepository.findSessionSummaries(
                 query.userId(),
                 cursor == null ? null : cursor.lastActivityAt(),
                 cursor == null ? null : cursor.sessionId(),
                 query.size() + 1
         );
-        boolean hasNext = rows.size() > query.size();
-        List<ChatbotSessionListItem> sessions = rows.stream()
+        boolean hasNext = summaries.size() > query.size();
+        List<ChatbotSessionListItem> sessions = summaries.stream()
                 .limit(query.size())
                 .map(this::toListItem)
                 .toList();
         ChatbotSessionListItem lastSession = hasNext ? sessions.get(sessions.size() - 1) : null;
         String nextCursor = hasNext
                 ? cursorCodec.encode(lastSession.lastActivityAt(), lastSession.sessionId())
                 : null;
 
         return new ChatbotSessionListResult(sessions, nextCursor, hasNext);
     }
 
     private ChatbotSessionCursorCodec.CursorPayload decodeCursor(String cursor) {
         if (cursor == null || cursor.isBlank()) {
             return null;
         }
         return cursorCodec.decode(cursor);
     }
 
-    private ChatbotSessionListItem toListItem(ChatbotSessionListRow row) {
+    private ChatbotSessionListItem toListItem(ChatbotSessionSummary summary) {
         return new ChatbotSessionListItem(
-                row.getSessionId(), row.getTitle(), row.getLastMessage(), row.getLastActivityAt()
+                summary.sessionId(), summary.title(), summary.lastMessage(), summary.lastActivityAt()
         );
     }
 }
diff --git a/src/main/java/com/ssambbong/gymjjak/chatbot/infrastructure/persistence/ChatbotPersistenceMapper.java b/src/main/java/com/ssambbong/gymjjak/chatbot/infrastructure/persistence/ChatbotPersistenceMapper.java
new file mode 100644
index 00000000..d7df23f2
--- /dev/null
+++ b/src/main/java/com/ssambbong/gymjjak/chatbot/infrastructure/persistence/ChatbotPersistenceMapper.java
@@ -0,0 +1,19 @@
+package com.ssambbong.gymjjak.chatbot.infrastructure.persistence;
+
+import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotSession;
+import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotSessionSummary;
+import org.springframework.stereotype.Component;
+
+@Component
+public class ChatbotPersistenceMapper {
+
+    public ChatbotSession toDomain(ChatbotSessionJpaEntity entity) {
+        return new ChatbotSession(entity.getSessionId(), entity.getUserId(), entity.getLastActivityAt());
+    }
+
+    public ChatbotSessionSummary toSummary(ChatbotSessionListRow row) {
+        return new ChatbotSessionSummary(
+                row.getSessionId(), row.getTitle(), row.getLastMessage(), row.getLastActivityAt()
+        );
+    }
+}
diff --git a/src/main/java/com/ssambbong/gymjjak/chatbot/infrastructure/persistence/ChatbotSessionPersistenceAdapter.java b/src/main/java/com/ssambbong/gymjjak/chatbot/infrastructure/persistence/ChatbotSessionPersistenceAdapter.java
new file mode 100644
index 00000000..2cbc79c4
--- /dev/null
+++ b/src/main/java/com/ssambbong/gymjjak/chatbot/infrastructure/persistence/ChatbotSessionPersistenceAdapter.java
@@ -0,0 +1,36 @@
+package com.ssambbong.gymjjak.chatbot.infrastructure.persistence;
+
+import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotSession;
+import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotSessionSummary;
+import com.ssambbong.gymjjak.chatbot.domain.repository.ChatbotSessionRepository;
+import lombok.RequiredArgsConstructor;
+import org.springframework.stereotype.Repository;
+
+import java.time.LocalDateTime;
+import java.util.List;
+import java.util.Optional;
+
+@Repository
+@RequiredArgsConstructor
+public class ChatbotSessionPersistenceAdapter implements ChatbotSessionRepository {
+
+    private final SpringDataChatbotSessionRepository repository;
+    private final ChatbotPersistenceMapper persistenceMapper;
+
+    @Override
+    public Optional<ChatbotSession> findBySessionId(String sessionId) {
+        return repository.findBySessionId(sessionId).map(persistenceMapper::toDomain);
+    }
+
+    @Override
+    public List<ChatbotSessionSummary> findSessionSummaries(
+            Long userId,
+            LocalDateTime cursorLastActivityAt,
+            String cursorSessionId,
+            int limit
+    ) {
+        return repository.findSessionList(userId, cursorLastActivityAt, cursorSessionId, limit).stream()
+                .map(persistenceMapper::toSummary)
+                .toList();
+    }
+}
diff --git a/src/test/java/com/ssambbong/gymjjak/chatbot/application/service/ChatbotSessionQueryServiceTest.java b/src/test/java/com/ssambbong/gymjjak/chatbot/application/service/ChatbotSessionQueryServiceTest.java
index 6bbb2133..29b7ca53 100644
--- a/src/test/java/com/ssambbong/gymjjak/chatbot/application/service/ChatbotSessionQueryServiceTest.java
+++ b/src/test/java/com/ssambbong/gymjjak/chatbot/application/service/ChatbotSessionQueryServiceTest.java
@@ -1,88 +1,86 @@
 package com.ssambbong.gymjjak.chatbot.application.service;
 
 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.ssambbong.gymjjak.chatbot.application.query.FindChatbotSessionsQuery;
 import com.ssambbong.gymjjak.chatbot.application.result.ChatbotSessionListItem;
 import com.ssambbong.gymjjak.chatbot.application.result.ChatbotSessionListResult;
+import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotSessionSummary;
+import com.ssambbong.gymjjak.chatbot.domain.repository.ChatbotSessionRepository;
 import com.ssambbong.gymjjak.chatbot.exception.InvalidChatbotSessionCursorException;
-import com.ssambbong.gymjjak.chatbot.infrastructure.persistence.ChatbotSessionListRow;
-import com.ssambbong.gymjjak.chatbot.infrastructure.persistence.SpringDataChatbotSessionRepository;
 import org.junit.jupiter.api.BeforeEach;
 import org.junit.jupiter.api.Test;
 import org.junit.jupiter.api.extension.ExtendWith;
 import org.mockito.Mock;
 import org.mockito.junit.jupiter.MockitoExtension;
 
 import java.time.LocalDateTime;
 import java.util.List;
 
 import static org.assertj.core.api.Assertions.assertThat;
 import static org.assertj.core.api.Assertions.assertThatThrownBy;
-import static org.mockito.ArgumentMatchers.any;
-import static org.mockito.ArgumentMatchers.eq;
 import static org.mockito.Mockito.verify;
 import static org.mockito.Mockito.verifyNoInteractions;
 import static org.mockito.Mockito.when;
 
 @ExtendWith(MockitoExtension.class)
 class ChatbotSessionQueryServiceTest {
 
-    @Mock private SpringDataChatbotSessionRepository sessionRepository;
-    @Mock private ChatbotSessionListRow row;
-    @Mock private ChatbotSessionListRow nextRow;
+    @Mock private ChatbotSessionRepository sessionRepository;
 
     private ChatbotSessionQueryService service;
 
     @BeforeEach
     void setUp() {
         service = new ChatbotSessionQueryService(
                 sessionRepository,
                 new ChatbotSessionCursorCodec(new ObjectMapper().findAndRegisterModules())
         );
     }
 
     @Test
     void mapsSessionRowAndReturnsNoNextCursorWhenRowsFitRequestedSize() {
         LocalDateTime lastActivityAt = LocalDateTime.of(2026, 7, 23, 10, 0);
-        when(row.getSessionId()).thenReturn("session-1");
-        when(row.getTitle()).thenReturn("first question");
-        when(row.getLastMessage()).thenReturn("latest answer");
-        when(row.getLastActivityAt()).thenReturn(lastActivityAt);
-        when(sessionRepository.findSessionList(7L, null, null, 21)).thenReturn(List.of(row));
+        ChatbotSessionSummary summary = new ChatbotSessionSummary(
+                "session-1", "first question", "latest answer", lastActivityAt
+        );
+        when(sessionRepository.findSessionSummaries(7L, null, null, 21)).thenReturn(List.of(summary));
 
         ChatbotSessionListResult result = service.findSessions(new FindChatbotSessionsQuery(7L, null, 20));
 
         assertThat(result.sessions()).containsExactly(new ChatbotSessionListItem(
                 "session-1", "first question", "latest answer", lastActivityAt
         ));
         assertThat(result.hasNext()).isFalse();
         assertThat(result.nextCursor()).isNull();
+        verify(sessionRepository).findSessionSummaries(7L, null, null, 21);
     }
 
     @Test
     void returnsRequestedSizeAndNextCursorWhenAnExtraRowExists() {
         LocalDateTime firstActivityAt = LocalDateTime.of(2026, 7, 23, 10, 0);
-        when(row.getSessionId()).thenReturn("session-2");
-        when(row.getTitle()).thenReturn("first question");
-        when(row.getLastMessage()).thenReturn("latest answer");
-        when(row.getLastActivityAt()).thenReturn(firstActivityAt);
-        when(sessionRepository.findSessionList(7L, null, null, 2)).thenReturn(List.of(row, nextRow));
+        ChatbotSessionSummary summary = new ChatbotSessionSummary(
+                "session-2", "first question", "latest answer", firstActivityAt
+        );
+        ChatbotSessionSummary nextSummary = new ChatbotSessionSummary(
+                "session-1", "older question", "older answer", firstActivityAt.minusMinutes(1)
+        );
+        when(sessionRepository.findSessionSummaries(7L, null, null, 2)).thenReturn(List.of(summary, nextSummary));
 
         ChatbotSessionListResult result = service.findSessions(new FindChatbotSessionsQuery(7L, null, 1));
 
         assertThat(result.sessions()).containsExactly(new ChatbotSessionListItem(
                 "session-2", "first question", "latest answer", firstActivityAt
         ));
         assertThat(result.hasNext()).isTrue();
         assertThat(result.nextCursor()).isNotBlank();
-        verify(sessionRepository).findSessionList(7L, null, null, 2);
+        verify(sessionRepository).findSessionSummaries(7L, null, null, 2);
     }
 
     @Test
     void throwsInvalidCursorExceptionWithoutQueryingRepositoryForMalformedCursor() {
         assertThatThrownBy(() -> service.findSessions(new FindChatbotSessionsQuery(7L, "not-base64", 20)))
                 .isInstanceOf(InvalidChatbotSessionCursorException.class);
 
         verifyNoInteractions(sessionRepository);
     }
 
