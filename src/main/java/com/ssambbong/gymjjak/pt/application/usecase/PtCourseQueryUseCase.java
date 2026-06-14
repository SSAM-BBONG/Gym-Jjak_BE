package com.ssambbong.gymjjak.pt.application.usecase;

import java.util.List;

public interface PtCourseQueryUseCase {

    // 목록 조회
    List<PtCourseListView> findAllPtCourses();

    // 상세 조회
    PtCourseDetailView findPtCourseDetail(Long ptCourseId);

    // ──── 목록 뷰 ────
    record PtCourseListView(
            Long ptCourseId,
            String title,
            Long thumbnailFileId,
            int price,
            // 태그
            Long tagId,
            String tagName,
            // 카테고리
            Long categoryId,
            String categoryName,
            // 트레이너
            String trainerName,
            // 조직
            Long organizationId,
            String organizationBusinessName,
            String organizationRoadAddress,
            Double latitude,
            Double longitude,
            // 리뷰
            int reviewCount
    ) {}

    // ──── 상세 뷰 ────
    record PtCourseDetailView(
            Long ptCourseId,
            Long thumbnailFileId,
            String title,
            String description,
            int price,
            int totalSessionCount,
            Double averageRating,
            int reviewCount,
            // 조직
            Long organizationId,
            // 트레이너
            Long trainerProfileId,
            String trainerName,
            Long trainerProfileFileId,
            String trainerIntroduction,
            List<String> certifications,
            List<String> awards,
            // 미구현 (빈 배열 반환)
            List<Object> curriculums,
            List<Object> schedules,
            List<Object> recentReviews
    ) {}
}
