package com.ssambbong.gymjjak.pt.feedback.domain.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Feedback {

    private final Long id;
    private final Long ptReservationId;
    private final Long ptCurriculumId;
    private final Long trainerProfileId;
    private final Long userId;
    private String content;
    private final FeedbackStatus status;
    private final LocalDateTime createdAt;

    @Builder(access = AccessLevel.PUBLIC)
    private Feedback(Long id, Long ptReservationId, Long ptCurriculumId,
                     Long trainerProfileId, Long userId, String content,
                     FeedbackStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.ptReservationId = ptReservationId;
        this.ptCurriculumId = ptCurriculumId;
        this.trainerProfileId = trainerProfileId;
        this.userId = userId;
        this.content = content;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Feedback create(Long ptReservationId, Long ptCurriculumId,
                                  Long trainerProfileId, Long userId, String content) {
        return Feedback.builder()
                .ptReservationId(ptReservationId)
                .ptCurriculumId(ptCurriculumId)
                .trainerProfileId(trainerProfileId)
                .userId(userId)
                .content(content)
                .status(FeedbackStatus.ACTIVE)
                .build();
    }

    public static Feedback restore(Long id, Long ptReservationId, Long ptCurriculumId,
                                   Long trainerProfileId, Long userId, String content,
                                   FeedbackStatus status, LocalDateTime createdAt) {
        return Feedback.builder()
                .id(id)
                .ptReservationId(ptReservationId)
                .ptCurriculumId(ptCurriculumId)
                .trainerProfileId(trainerProfileId)
                .userId(userId)
                .content(content)
                .status(status)
                .createdAt(createdAt)
                .build();
    }

    // 피드백 내용 수정
    public void update(String content) {
        this.content = content;
    }
}
