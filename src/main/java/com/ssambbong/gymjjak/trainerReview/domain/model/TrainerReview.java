package com.ssambbong.gymjjak.trainerReview.domain.model;

import java.time.LocalDateTime;

public class TrainerReview {

    private final Long id;
    private final Long userId;
    private final Long trainerProfileId;
    private final Long ptCourseId;
    private final Long ptReservationId;
    private final int rating;
    private final String content;
    private TrainerReviewStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private TrainerReview(Long id, Long userId, Long trainerProfileId, Long ptCourseId,
                          Long ptReservationId, int rating, String content,
                          TrainerReviewStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.trainerProfileId = trainerProfileId;
        this.ptCourseId = ptCourseId;
        this.ptReservationId = ptReservationId;
        this.rating = rating;
        this.content = content;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static TrainerReview create(Long userId, Long trainerProfileId, Long ptCourseId,
                                       Long ptReservationId, int rating, String content) {
        return new TrainerReview(null, userId, trainerProfileId, ptCourseId,
                ptReservationId, rating, content, TrainerReviewStatus.ACTIVE, null, null);
    }

    public static TrainerReview restore(Long id, Long userId, Long trainerProfileId, Long ptCourseId,
                                        Long ptReservationId, int rating, String content,
                                        TrainerReviewStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new TrainerReview(id, userId, trainerProfileId, ptCourseId,
                ptReservationId, rating, content, status, createdAt, updatedAt);
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getTrainerProfileId() { return trainerProfileId; }
    public Long getPtCourseId() { return ptCourseId; }
    public Long getPtReservationId() { return ptReservationId; }
    public int getRating() { return rating; }
    public String getContent() { return content; }
    public TrainerReviewStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
