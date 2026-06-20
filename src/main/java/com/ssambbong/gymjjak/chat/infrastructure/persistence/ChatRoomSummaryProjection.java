package com.ssambbong.gymjjak.chat.infrastructure.persistence;

import java.time.LocalDateTime;

public interface ChatRoomSummaryProjection {
    Long getChatRoomId();
    String getPartnerName();
    String getPartnerRole();
    Long getPartnerProfileFileId();
    String getLastMessage();
    LocalDateTime getLastMessageAt();
    Long getUnreadCount();
}
