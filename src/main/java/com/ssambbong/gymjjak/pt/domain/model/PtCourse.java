package com.ssambbong.gymjjak.pt.domain.model;

import com.ssambbong.gymjjak.pt.domain.exception.PtCourseInvalidException;

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

    // 추후 구현 예정. 지금은 false로 유지
    private boolean supportsDietLog;
    private boolean supportsWorkoutLog;

    private PtCourseStatus status;

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
                    boolean supportsDietLog,
                    boolean supportsWorkoutLog,
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
        this.supportsDietLog = supportsDietLog;
        this.supportsWorkoutLog = supportsWorkoutLog;
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
                false,
                false,
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
            boolean supportsDietLog,
            boolean supportsWorkoutLog,
            PtCourseStatus status
    ) {
        return new PtCourse(
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
                supportsDietLog,
                supportsWorkoutLog,
                status
        );
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
    public boolean isSupportsDietLog() { return supportsDietLog; }
    public boolean isSupportsWorkoutLog() { return supportsWorkoutLog; }
    public PtCourseStatus getStatus() { return status; }
}
