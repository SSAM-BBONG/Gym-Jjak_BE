package com.ssambbong.gymjjak.pt.infrastructure.persistence;

import com.ssambbong.gymjjak.pt.domain.model.PtCourseStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "pt_courses")
@EntityListeners(AuditingEntityListener.class)
public class PtCourseJpaEntity {

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

    @CreatedDate // 최초 저장 시 자동으로 현재 시간 입력
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate // 수정 시 자동으로 현재 시간 입력
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at") // 소프트 딜리트용. null이면 삭제 안 된 것
    private LocalDateTime deletedAt;

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
}
