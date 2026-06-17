package com.ssambbong.gymjjak.chat.domain.model;

import com.ssambbong.gymjjak.chat.exception.ChatRoomAlreadyLeftException;
import com.ssambbong.gymjjak.chat.exception.ChatRoomClosedException;

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
    private LocalDateTime lastMessageAt;
    private final LocalDateTime updatedAt;

    private ChatRoom(Long id, Long userId, Long trainerProfileId, Long ptCourseId,
                     boolean userLeft, boolean trainerLeft, ChatRoomStatus status,
                     LocalDateTime createdAt, LocalDateTime closedAt,
                     LocalDateTime lastMessageAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.trainerProfileId = trainerProfileId;
        this.ptCourseId = ptCourseId;
        this.userLeft = userLeft;
        this.trainerLeft = trainerLeft;
        this.status = status;
        this.createdAt = createdAt;
        this.closedAt = closedAt;
        this.lastMessageAt = lastMessageAt;
        this.updatedAt = updatedAt;
    }

    public static ChatRoom create(Long userId, Long trainerProfileId, Long ptCourseId) {
        return new ChatRoom(null, userId, trainerProfileId, ptCourseId,
                false, false, ChatRoomStatus.ACTIVE, null, null, null, null);
    }

    public static ChatRoom restore(Long id, Long userId, Long trainerProfileId, Long ptCourseId,
                                   boolean userLeft, boolean trainerLeft, ChatRoomStatus status,
                                   LocalDateTime createdAt, LocalDateTime closedAt,
                                   LocalDateTime lastMessageAt, LocalDateTime updatedAt) {
        return new ChatRoom(id, userId, trainerProfileId, ptCourseId,
                userLeft, trainerLeft, status, createdAt, closedAt, lastMessageAt, updatedAt);
    }

    public void leaveAsUser() {
        if (this.status == ChatRoomStatus.DELETED) throw new ChatRoomClosedException();
        if (this.userLeft) throw new ChatRoomAlreadyLeftException();
        this.userLeft = true;
        updateStatus();
    }

    public void leaveAsTrainer() {
        if (this.status == ChatRoomStatus.DELETED) throw new ChatRoomClosedException();
        if (this.trainerLeft) throw new ChatRoomAlreadyLeftException();
        this.trainerLeft = true;
        updateStatus();
    }

    private void updateStatus() {
        if (this.userLeft && this.trainerLeft) {
            this.status = ChatRoomStatus.DELETED;
            return;
        }
        this.status = ChatRoomStatus.CLOSED;
        if (this.closedAt == null) {
            this.closedAt = LocalDateTime.now();
        }
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
    public LocalDateTime getLastMessageAt() { return lastMessageAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
