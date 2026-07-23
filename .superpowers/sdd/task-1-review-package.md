f0afb988 refactor: add chatbot query domain boundaries
 .../chatbot/domain/model/ChatbotMessage.java       | 17 +++++++++++++++++
 .../chatbot/domain/model/ChatbotSession.java       | 10 ++++++++++
 .../domain/model/ChatbotSessionSummary.java        | 11 +++++++++++
 .../repository/ChatbotMessageRepository.java       | 16 ++++++++++++++++
 .../repository/ChatbotSessionRepository.java       | 20 ++++++++++++++++++++
 .../chatbot/domain/model/ChatbotSessionTest.java   | 22 ++++++++++++++++++++++
 6 files changed, 96 insertions(+)
diff --git a/src/main/java/com/ssambbong/gymjjak/chatbot/domain/model/ChatbotMessage.java b/src/main/java/com/ssambbong/gymjjak/chatbot/domain/model/ChatbotMessage.java
new file mode 100644
index 00000000..3b9fa945
--- /dev/null
+++ b/src/main/java/com/ssambbong/gymjjak/chatbot/domain/model/ChatbotMessage.java
@@ -0,0 +1,17 @@
+package com.ssambbong.gymjjak.chatbot.domain.model;
+
+import java.time.LocalDateTime;
+
+public record ChatbotMessage(
+        Long messageId,
+        String sessionId,
+        ChatbotMessageRole role,
+        String content,
+        String intentHint,
+        String category,
+        String routineJson,
+        String sourcesJson,
+        Boolean limited,
+        LocalDateTime createdAt
+) {
+}
diff --git a/src/main/java/com/ssambbong/gymjjak/chatbot/domain/model/ChatbotSession.java b/src/main/java/com/ssambbong/gymjjak/chatbot/domain/model/ChatbotSession.java
new file mode 100644
index 00000000..6f1a27f2
--- /dev/null
+++ b/src/main/java/com/ssambbong/gymjjak/chatbot/domain/model/ChatbotSession.java
@@ -0,0 +1,10 @@
+package com.ssambbong.gymjjak.chatbot.domain.model;
+
+import java.time.LocalDateTime;
+
+public record ChatbotSession(String sessionId, Long userId, LocalDateTime lastActivityAt) {
+
+    public boolean isOwnedBy(Long userId) {
+        return this.userId.equals(userId);
+    }
+}
diff --git a/src/main/java/com/ssambbong/gymjjak/chatbot/domain/model/ChatbotSessionSummary.java b/src/main/java/com/ssambbong/gymjjak/chatbot/domain/model/ChatbotSessionSummary.java
new file mode 100644
index 00000000..b18f0516
--- /dev/null
+++ b/src/main/java/com/ssambbong/gymjjak/chatbot/domain/model/ChatbotSessionSummary.java
@@ -0,0 +1,11 @@
+package com.ssambbong.gymjjak.chatbot.domain.model;
+
+import java.time.LocalDateTime;
+
+public record ChatbotSessionSummary(
+        String sessionId,
+        String title,
+        String lastMessage,
+        LocalDateTime lastActivityAt
+) {
+}
diff --git a/src/main/java/com/ssambbong/gymjjak/chatbot/domain/repository/ChatbotMessageRepository.java b/src/main/java/com/ssambbong/gymjjak/chatbot/domain/repository/ChatbotMessageRepository.java
new file mode 100644
index 00000000..ff5c84bd
--- /dev/null
+++ b/src/main/java/com/ssambbong/gymjjak/chatbot/domain/repository/ChatbotMessageRepository.java
@@ -0,0 +1,16 @@
+package com.ssambbong.gymjjak.chatbot.domain.repository;
+
+import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotMessage;
+
+import java.time.LocalDateTime;
+import java.util.List;
+
+public interface ChatbotMessageRepository {
+
+    List<ChatbotMessage> findHistory(
+            String sessionId,
+            LocalDateTime cursorCreatedAt,
+            Long cursorMessageId,
+            int limit
+    );
+}
diff --git a/src/main/java/com/ssambbong/gymjjak/chatbot/domain/repository/ChatbotSessionRepository.java b/src/main/java/com/ssambbong/gymjjak/chatbot/domain/repository/ChatbotSessionRepository.java
new file mode 100644
index 00000000..cfc43a37
--- /dev/null
+++ b/src/main/java/com/ssambbong/gymjjak/chatbot/domain/repository/ChatbotSessionRepository.java
@@ -0,0 +1,20 @@
+package com.ssambbong.gymjjak.chatbot.domain.repository;
+
+import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotSession;
+import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotSessionSummary;
+
+import java.time.LocalDateTime;
+import java.util.List;
+import java.util.Optional;
+
+public interface ChatbotSessionRepository {
+
+    Optional<ChatbotSession> findBySessionId(String sessionId);
+
+    List<ChatbotSessionSummary> findSessionSummaries(
+            Long userId,
+            LocalDateTime cursorLastActivityAt,
+            String cursorSessionId,
+            int limit
+    );
+}
diff --git a/src/test/java/com/ssambbong/gymjjak/chatbot/domain/model/ChatbotSessionTest.java b/src/test/java/com/ssambbong/gymjjak/chatbot/domain/model/ChatbotSessionTest.java
new file mode 100644
index 00000000..c729e4a4
--- /dev/null
+++ b/src/test/java/com/ssambbong/gymjjak/chatbot/domain/model/ChatbotSessionTest.java
@@ -0,0 +1,22 @@
+package com.ssambbong.gymjjak.chatbot.domain.model;
+
+import org.junit.jupiter.api.Test;
+
+import java.time.LocalDateTime;
+
+import static org.assertj.core.api.Assertions.assertThat;
+
+class ChatbotSessionTest {
+
+    @Test
+    void returnsTrueOnlyForItsOwner() {
+        ChatbotSession session = new ChatbotSession(
+                "session-uuid",
+                7L,
+                LocalDateTime.of(2026, 7, 23, 10, 0)
+        );
+
+        assertThat(session.isOwnedBy(7L)).isTrue();
+        assertThat(session.isOwnedBy(8L)).isFalse();
+    }
+}
