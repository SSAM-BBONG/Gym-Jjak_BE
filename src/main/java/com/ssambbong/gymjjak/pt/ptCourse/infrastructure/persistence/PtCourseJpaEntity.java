package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence;

import com.ssambbong.gymjjak.global.infrastructure.presentation.BaseTimeEntity;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "pt_courses")
public class PtCourseJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pt_course_id")
    private Long id;

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @Column(name = "trainer_profile_id", nullable = false)
    private Long trainerProfileId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "tag_id", nullable = false)
    private Long tagId;

    @Column(name = "thumbnail_file_id")
    private Long thumbnailFileId;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "total_session_count", nullable = false)
    private int totalSessionCount;

    // 추후 구현 예정. 기본값 false
    @Column(name = "supports_diet_log", nullable = false)
    private boolean supportsDietLog;
    @Column(name = "supports_workout_log", nullable = false)
    private boolean supportsWorkoutLog;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private PtCourseStatus status;

    public PtCourseJpaEntity(Long organizationId,
                             Long trainerProfileId,
                             Long categoryId,
                             Long tagId,
                             Long thumbnailFileId,
                             String title,
                             String description,
                             int price,
                             int totalSessionCount,
                             boolean supportsDietLog,
                             boolean supportsWorkoutLog,
                             PtCourseStatus status
    ) {
        this.organizationId = organizationId;
        this.trainerProfileId = trainerProfileId;
        this.categoryId = categoryId;
        this.tagId = tagId;
        this.thumbnailFileId = thumbnailFileId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.totalSessionCount = totalSessionCount;
        this.supportsDietLog = supportsDietLog;
        this.supportsWorkoutLog = supportsWorkoutLog;
        this.status = status;
    }

    // 상태 변경 (blind/unblind) — 더티체킹으로 UPDATE
    public void updateStatus(PtCourseStatus status) {
        this.status = status;
    }

    // 수동 제재 soft delete — status=DELETED + deletedAt 설정 — 스케줄러 hard delete 대상
    public void softDelete() {
        this.status = PtCourseStatus.DELETED;
        super.delete(); // BaseTimeEntity.delete() → deletedAt = now()
    }
}
