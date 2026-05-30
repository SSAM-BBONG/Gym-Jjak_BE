package com.ssambbong.gymjjak.pt.application.usecase;

import com.ssambbong.gymjjak.pt.domain.model.PtCourseStatus;

import java.util.List;

public interface PtCourseQueryUseCase {

    // 목록 조회 (페이지네이션)
    PtCoursePageResult findAllPtCourses(int page, int size);

    // 상세 조회
    PtCourseDetailView findPtCourseDetail(Long ptCourseId);

    // ──── 페이지 결과 ────
    record PtCoursePageResult(
            List<PtCourseListView> content,
            long totalElements,
            int totalPages,
            int page,
            int size
    ) {}

    // ──── 목록 뷰 ────
    record PtCourseListView(
            Long ptCourseId,
            String categoryName,
            Long tagId,
            String thumbnailUrl,
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
            Double averageRating,
            int reviewCount
    ) {}

    // ──── 상세 뷰 ────
    record PtCourseDetailView(
            Long ptCourseId,
            String categoryName,
            Long tagId,
            String thumbnailUrl,
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
            String trainerSpec,
            String trainerIntroduction,
            Double averageRating,
            int reviewCount
    ) {}
}
