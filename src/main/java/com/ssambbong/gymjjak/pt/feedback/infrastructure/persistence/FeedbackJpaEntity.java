package com.ssambbong.gymjjak.pt.feedback.infrastructure.persistence;

import com.ssambbong.gymjjak.global.infrastructure.presentation.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "feedbacks")
public class FeedbackJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long id;

    @Column(name = "pt_reservation_id", nullable = false)
    private Long ptReservationId;

    @Column(name = "pt_curriculum_id", nullable = false)
    private Long ptCurriculumId;

    @Column(name = "trainer_profile_id", nullable = false)
    private Long trainerProfileId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "status", nullable = false)
    private String status;

    @Builder
    private FeedbackJpaEntity(Long id, Long ptReservationId, Long ptCurriculumId,
                              Long trainerProfileId, Long userId, String content, String status) {
        this.id = id;
        this.ptReservationId = ptReservationId;
        this.ptCurriculumId = ptCurriculumId;
        this.trainerProfileId = trainerProfileId;
        this.userId = userId;
        this.content = content;
        this.status = status;
    }

    // 피드백 내용 수정
    public void update(String content) {
        this.content = content;
    }
}
