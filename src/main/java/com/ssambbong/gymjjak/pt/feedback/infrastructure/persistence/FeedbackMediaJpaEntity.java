package com.ssambbong.gymjjak.pt.feedback.infrastructure.persistence;

import com.ssambbong.gymjjak.global.infrastructure.presentation.CreatedAtEntity;
import com.ssambbong.gymjjak.pt.feedback.domain.model.FeedbackMediaType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "feedback_media")
public class FeedbackMediaJpaEntity extends CreatedAtEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "feedback_media_id")
    private Long id;

    @Column(name = "feedback_id", nullable = false)
    private Long feedbackId;

    @Column(name = "file_id", nullable = false)
    private Long fileId;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false)
    private FeedbackMediaType mediaType;

    @Builder
    private FeedbackMediaJpaEntity(Long id, Long feedbackId, Long fileId, FeedbackMediaType mediaType) {
        this.id = id;
        this.feedbackId = feedbackId;
        this.fileId = fileId;
        this.mediaType = mediaType;
    }
}
