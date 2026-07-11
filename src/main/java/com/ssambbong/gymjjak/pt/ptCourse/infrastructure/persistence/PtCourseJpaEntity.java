package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence;

import com.ssambbong.gymjjak.global.infrastructure.presentation.BaseTimeEntity;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "part", nullable = false, length = 30)
    private PartType part;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private PtCourseStatus status;

    public PtCourseJpaEntity(Long organizationId,
                             Long trainerProfileId,
                             PartType part,
                             Long thumbnailFileId,
                             String title,
                             String description,
                             int price,
                             int totalSessionCount,
                             PtCourseStatus status
    ) {
        this.organizationId = organizationId;
        this.trainerProfileId = trainerProfileId;
        this.part = part;
        this.thumbnailFileId = thumbnailFileId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.totalSessionCount = totalSessionCount;
        this.status = status;
    }

    // 상태 변경 (blind/unblind) — 더티체킹으로 UPDATE
    public void updateStatus(PtCourseStatus status) {
        this.status = status;
    }

    // soft delete — status=DELETED + 도메인에서 결정한 deletedAt 반영 — 스케줄러 hard delete 대상
    public void softDelete(LocalDateTime deletedAt) {
        this.status = PtCourseStatus.DELETED;
        super.setDeletedAt(deletedAt);
    }

    // 강습 정보 수정 (제목·설명·파트·가격·썸네일·총 회차) — 더티체킹으로 UPDATE
    public void updateFields(PartType part, Long thumbnailFileId,
                             String title, String description, int price, int totalSessionCount) {
        this.part = part;
        this.thumbnailFileId = thumbnailFileId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.totalSessionCount = totalSessionCount;
    }
}
