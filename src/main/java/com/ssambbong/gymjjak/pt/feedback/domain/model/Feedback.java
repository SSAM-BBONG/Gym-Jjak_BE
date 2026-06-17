package com.ssambbong.gymjjak.pt.feedback.domain.model;

import java.time.LocalDateTime;

public class Feedback {

    private final Long id;
    private final Long ptReservationId;
    private final Long ptCurriculumId;
    private final String content;
    private final LocalDateTime createdAt;


    public Feedback(Long id, Long ptReservationId, Long ptCurriculumId, String content, LocalDateTime createdAt) {
        this.id = id;
        this.ptReservationId = ptReservationId;
        this.ptCurriculumId = ptCurriculumId;
        this.content = content;
        this.createdAt = createdAt;
    }

    // DB 복원 시
    public static Feedback restore(Long id, Long ptReservationId, Long ptCurriculumId, String content, LocalDateTime createdAt) {

        return new Feedback(id, ptReservationId, ptCurriculumId, content, createdAt);
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

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}





