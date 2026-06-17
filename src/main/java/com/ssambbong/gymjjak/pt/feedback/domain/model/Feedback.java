package com.ssambbong.gymjjak.pt.feedback.domain.model;

import java.time.LocalDateTime;

public class Feedback {

    private final Long id;
    private final Long ptReservationId;
    private final Long ptCurriculumId;
    private final Long trainerProfileId;
    private final Long userId;
    private final String content;
    private final String status;
    private final LocalDateTime createdAt;


    public Feedback(Long id, Long ptReservationId, Long ptCurriculumId, Long trainerProfileId, Long userId, String content, String status, LocalDateTime createdAt) {
        this.id = id;
        this.ptReservationId = ptReservationId;
        this.ptCurriculumId = ptCurriculumId;
        this.trainerProfileId = trainerProfileId;
        this.userId = userId;
        this.content = content;
        this.status = status;
        this.createdAt = createdAt;
    }

    // DB 복원 시
    public static Feedback restore(Long id, Long ptReservationId, Long ptCurriculumId,
                                   Long trainerProfileId, Long userId, String content,
                                   String status, LocalDateTime createdAt) {
        return new Feedback(id, ptReservationId, ptCurriculumId,
                trainerProfileId, userId, content, status, createdAt);
    }

    public Long getId() {
        return id;
    }

    public Long getPtReservationId() {
        return ptReservationId;
    }

    public Long getPtCurriculumId() {
        return ptCurriculumId;
    }

    public Long getTrainerProfileId() {
        return trainerProfileId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}





