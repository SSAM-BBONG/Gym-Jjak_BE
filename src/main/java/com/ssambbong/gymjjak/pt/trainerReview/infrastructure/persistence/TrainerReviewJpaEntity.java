package com.ssambbong.gymjjak.pt.trainerReview.infrastructure.persistence;

import com.ssambbong.gymjjak.global.infrastructure.presentation.BaseCreatedUpdatedEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "trainer_reviews")
public class TrainerReviewJpaEntity extends BaseCreatedUpdatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trainer_review_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "trainer_profile_id", nullable = false)
    private Long trainerProfileId;

    @Column(name = "pt_course_id", nullable = false)
    private Long ptCourseId;

    @Column(name = "pt_reservation_id", nullable = false, unique = true)
    private Long ptReservationId;

    @Column(name = "rating", nullable = false, columnDefinition = "TINYINT")
    private int rating;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    private TrainerReviewJpaEntity(Long id, Long userId, Long trainerProfileId, Long ptCourseId,
                                   Long ptReservationId, int rating, String content,
                                   String status, LocalDateTime deletedAt) {
        this.id = id;
        this.userId = userId;
        this.trainerProfileId = trainerProfileId;
        this.ptCourseId = ptCourseId;
        this.ptReservationId = ptReservationId;
        this.rating = rating;
        this.content = content;
        this.status = status;
        this.deletedAt = deletedAt;
    }
}
