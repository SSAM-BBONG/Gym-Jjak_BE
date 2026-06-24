package com.ssambbong.gymjjak.pt.ptCourse.domain.model;

import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseCannotDeleteException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseInvalidException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseStatusInvalidException;
import java.time.LocalDateTime;

public class PtCourse {

    private final Long id;
    private final Long organizationId;
    private final Long trainerProfileId;
    private Long categoryId;
    private Long tagId;
    private Long thumbnailFileId;
    private String title;
    private String description;
    private int price;
    private int totalSessionCount;

    private PtCourseStatus status;
    private LocalDateTime deletedAt;

    private PtCourse(Long id,
                    Long organizationId,
                    Long trainerProfileId,
                    Long categoryId,
                    Long tagId,
                    Long thumbnailFileId,
                    String title,
                    String description,
                    int price,
                    int totalSessionCount,
                    PtCourseStatus status
    ) {
        // 도메인 불변식 보호
        if (title == null || title.isBlank()) {
            throw new PtCourseInvalidException();
        }
        if (description == null || description.isBlank()) {
            throw new PtCourseInvalidException();
        }
        if (price < 0) {
            throw new PtCourseInvalidException();
        }
        if (totalSessionCount < 1) {
            throw new PtCourseInvalidException();
        }

        this.id = id;
        this.organizationId = organizationId;
        this.trainerProfileId = trainerProfileId;
        this.categoryId = categoryId;
        this.tagId = tagId;
        this.thumbnailFileId = thumbnailFileId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.totalSessionCount = totalSessionCount;
        this.status = status;
    }

    // 새 PT 강습 등록 시 id=null, status=VISIBLE로 고정
    public static PtCourse create(
            Long organizationId,
            Long trainerProfileId,
            Long categoryId,
            Long tagId,
            Long thumbnailFileId,
            String title,
            String description,
            int price,
            int totalSessionCount
    ) {
        return new PtCourse(
                null,
                organizationId,
                trainerProfileId,
                categoryId,
                tagId,
                thumbnailFileId,
                title,
                description,
                price,
                totalSessionCount,
                PtCourseStatus.VISIBLE
        );
    }

    // DB에서 꺼낸 데이터로 도메인 객체 복원 시
    public static PtCourse restore(
            Long id,
            Long organizationId,
            Long trainerProfileId,
            Long categoryId,
            Long tagId,
            Long thumbnailFileId,
            String title,
            String description,
            int price,
            int totalSessionCount,
            PtCourseStatus status,
            LocalDateTime deletedAt
    ) {
        PtCourse ptCourse = new PtCourse(
                id,
                organizationId,
                trainerProfileId,
                categoryId,
                tagId,
                thumbnailFileId,
                title,
                description,
                price,
                totalSessionCount,
                status
        );
        ptCourse.deletedAt = deletedAt;
        return ptCourse;
    }

    // 트레이너가 강습 공개 여부 전환 (VISIBLE/HIDDEN)
    public void changeStatus(PtCourseStatus newStatus) {
        if (newStatus != PtCourseStatus.VISIBLE && newStatus != PtCourseStatus.HIDDEN) {
            throw new PtCourseStatusInvalidException();
        }
        this.status = newStatus;
    }

    // 트레이너가 강습 수정
    public void update(Long categoryId, Long tagId, Long thumbnailFileId,
                       String title, String description, int price, int totalSessionCount) {
        if (title == null || title.isBlank()) throw new PtCourseInvalidException();
        if (description == null || description.isBlank()) throw new PtCourseInvalidException();
        if (price < 0) throw new PtCourseInvalidException();
        if (totalSessionCount < 1) throw new PtCourseInvalidException();

        this.categoryId = categoryId;
        this.tagId = tagId;
        this.thumbnailFileId = thumbnailFileId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.totalSessionCount = totalSessionCount;
    }

    // 트레이너가 강습 삭제 — BLOCKED(관리자 제재 중)이면 거부, deletedAt을 도메인에서 직접 결정
    public void delete() {
        if (this.status == PtCourseStatus.BLOCKED) {
            throw new PtCourseCannotDeleteException();
        }
        this.status = PtCourseStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }

    // 관리자가 강습을 BLOCKED 상태로 전환
    public void blind() {
        this.status = PtCourseStatus.BLOCKED;
    }

    // 관리자가 강습을 VISIBLE 상태로 복원
    public void unblind() {
        this.status = PtCourseStatus.VISIBLE;
    }

    // getter
    public Long getId() { return id; }
    public Long getOrganizationId() { return organizationId; }
    public Long getTrainerProfileId() { return trainerProfileId; }
    public Long getCategoryId() { return categoryId; }
    public Long getTagId() { return tagId; }
    public Long getThumbnailFileId() { return thumbnailFileId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getPrice() { return price; }
    public int getTotalSessionCount() { return totalSessionCount; }
    public PtCourseStatus getStatus() { return status; }
    public LocalDateTime getDeletedAt() { return deletedAt; }

}
