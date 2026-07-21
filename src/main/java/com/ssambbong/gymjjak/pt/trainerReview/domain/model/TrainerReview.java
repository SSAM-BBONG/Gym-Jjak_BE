package com.ssambbong.gymjjak.pt.trainerReview.domain.model;

import java.time.LocalDateTime;

public class TrainerReview {

    private final Long id;
    private final Long userId;
    private final Long trainerProfileId;
    private final Long ptCourseId;
    private final int rating;
    private final String content;
    private final TrainerReviewStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime deletedAt;

    private TrainerReview(Long id, Long userId, Long trainerProfileId, Long ptCourseId,
                          int rating, String content,
                          TrainerReviewStatus status, LocalDateTime createdAt, LocalDateTime updatedAt,
                          LocalDateTime deletedAt) {
        this.id = id;
        this.userId = userId;
        this.trainerProfileId = trainerProfileId;
        this.ptCourseId = ptCourseId;
        this.rating = rating;
        this.content = content;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static TrainerReview create(Long userId, Long trainerProfileId, Long ptCourseId,
                                       int rating, String content) {
        return new TrainerReview(null, userId, trainerProfileId, ptCourseId,
                rating, content, TrainerReviewStatus.ACTIVE, null, null, null);
    }

    public static TrainerReview restore(Long id, Long userId, Long trainerProfileId, Long ptCourseId,
                                        int rating, String content,
                                        TrainerReviewStatus status, LocalDateTime createdAt,
                                        LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return new TrainerReview(id, userId, trainerProfileId, ptCourseId,
                rating, content, status, createdAt, updatedAt, deletedAt);
    }

    public TrainerReview update(int rating, String content) {
        return new TrainerReview(id, userId, trainerProfileId, ptCourseId,
                rating, content, status, createdAt, updatedAt, deletedAt);
    }

    public TrainerReview delete(LocalDateTime deletedAt) {
        return new TrainerReview(id, userId, trainerProfileId, ptCourseId,
                rating, content, TrainerReviewStatus.DELETED, createdAt, updatedAt,
                deletedAt);
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getTrainerProfileId() { return trainerProfileId; }
    public Long getPtCourseId() { return ptCourseId; }
    public int getRating() { return rating; }
    public String getContent() { return content; }
    public TrainerReviewStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
}
