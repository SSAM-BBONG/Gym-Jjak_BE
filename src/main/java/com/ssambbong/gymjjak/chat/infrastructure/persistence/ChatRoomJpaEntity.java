package com.ssambbong.gymjjak.chat.infrastructure.persistence;

import com.ssambbong.gymjjak.chat.domain.model.ChatRoomStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "chat_rooms")
@EntityListeners(AuditingEntityListener.class)
public class ChatRoomJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "trainer_profile_id", nullable = false)
    private Long trainerProfileId;

    @Column(name = "pt_course_id", nullable = false)
    private Long ptCourseId;

    @Column(name = "user_left", nullable = false)
    private boolean userLeft;

    @Column(name = "trainer_left", nullable = false)
    private boolean trainerLeft;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ChatRoomStatus status;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatRoomJpaEntity that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }

    public ChatRoomJpaEntity(Long userId, Long trainerProfileId, Long ptCourseId, ChatRoomStatus status) {
        this.userId = userId;
        this.trainerProfileId = trainerProfileId;
        this.ptCourseId = ptCourseId;
        this.userLeft = false;
        this.trainerLeft = false;
        this.status = status;
    }
}
