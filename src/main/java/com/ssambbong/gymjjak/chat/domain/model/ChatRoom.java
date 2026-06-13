package com.ssambbong.gymjjak.chat.domain.model;

import java.time.LocalDateTime;

public class ChatRoom {

    private final Long id;
    private final Long userId;
    private final Long trainerProfileId;
    private final Long ptCourseId;
    private boolean userLeft;
    private boolean trainerLeft;
    private ChatRoomStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime closedAt;
    private final LocalDateTime updatedAt;

    private ChatRoom(Long id, Long userId, Long trainerProfileId, Long ptCourseId,
                     boolean userLeft, boolean trainerLeft, ChatRoomStatus status,
                     LocalDateTime createdAt, LocalDateTime closedAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.trainerProfileId = trainerProfileId;
        this.ptCourseId = ptCourseId;
        this.userLeft = userLeft;
        this.trainerLeft = trainerLeft;
        this.status = status;
        this.createdAt = createdAt;
        this.closedAt = closedAt;
        this.updatedAt = updatedAt;
    }

    public static ChatRoom create(Long userId,
                                  Long trainerProfileId,
                                  Long ptCourseId) {
        return new ChatRoom(null,
                userId,
                trainerProfileId,
                ptCourseId,
                false,
                false,
                ChatRoomStatus.ACTIVE,
                null,
                null,
                null);
    }

    public static ChatRoom restore(Long id, Long userId, Long trainerProfileId, Long ptCourseId,
                                   boolean userLeft, boolean trainerLeft, ChatRoomStatus status,
                                   LocalDateTime createdAt, LocalDateTime closedAt, LocalDateTime updatedAt) {
        return new ChatRoom(id, userId, trainerProfileId, ptCourseId,
                userLeft, trainerLeft, status, createdAt, closedAt, updatedAt);
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getTrainerProfileId() { return trainerProfileId; }
    public Long getPtCourseId() { return ptCourseId; }
    public boolean isUserLeft() { return userLeft; }
    public boolean isTrainerLeft() { return trainerLeft; }
    public ChatRoomStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getClosedAt() { return closedAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
