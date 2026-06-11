package com.ssambbong.gymjjak.pt.application.usecase;

import com.ssambbong.gymjjak.pt.domain.model.PtCourseStatus;

import java.util.List;

public interface PtCourseQueryUseCase {

    // 목록 조회
    List<PtCourseListView> findAllPtCourses();

    // 상세 조회
    PtCourseDetailView findPtCourseDetail(Long ptCourseId);

    // ──── 목록 뷰 ────
    record PtCourseListView(
            Long ptCourseId,
            String categoryName,
            Long tagId,
            Long thumbnailFileId,
            String title,
            int price,
            int totalSessionCount,
            PtCourseStatus status,
            // 조직
            String organizationName,
            String organizationAddress,
            Double latitude,
            Double longitude,
            // 트레이너
            String trainerName,
            Long trainerProfileImageFileId,
            Double averageRating,
            int reviewCount
    ) {}

    // ──── 상세 뷰 ────
    record PtCourseDetailView(
            Long ptCourseId,
            String categoryName,
            Long tagId,
            Long thumbnailFileId,
            String title,
            String description,
            int price,
            int totalSessionCount,
            PtCourseStatus status,
            // 조직
            Long organizationId,
            String organizationName,
            String organizationAddress,
            String organizationPhone,
            String websiteUrl,
            String instagramUrl,
            // 트레이너
            Long trainerProfileId,
            String trainerName,
            Long trainerProfileImageFileId,
            String trainerSpec,
            String trainerIntroduction,
            Double averageRating,
            int reviewCount,
            // 미구현 (빈 배열 반환)
            List<Object> certifications,
            List<Object> awards,
            List<Object> curriculums,
            List<Object> recentReviews
    ) {}
}
