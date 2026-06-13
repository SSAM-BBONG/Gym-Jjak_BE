package com.ssambbong.gymjjak.pt.presentation.api.response;

import com.ssambbong.gymjjak.pt.application.usecase.PtCourseQueryUseCase;
import com.ssambbong.gymjjak.pt.domain.model.PtCourseStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

// PT 강습 상세 응답 DTO
public record PtCourseDetailResponse(

        // ── 기본 PT 정보 ──
        @Schema(description = "PT 강습 ID", example = "1")
        Long ptCourseId,

        @Schema(description = "카테고리명", example = "헬스")
        String categoryName,

        @Schema(description = "태그 ID", example = "1")
        Long tagId,

        @Schema(description = "썸네일 파일 ID")
        Long thumbnailFileId,

        @Schema(description = "제목", example = "맞춤 PT 1개월 과정")
        String title,

        @Schema(description = "소개")
        String description,

        @Schema(description = "가격", example = "350000")
        int price,

        @Schema(description = "전체 회차 수", example = "8")
        int totalSessionCount,

        @Schema(description = "상태", example = "VISIBLE")
        PtCourseStatus status,

        // ── 조직 정보 ──
        @Schema(description = "조직 ID", example = "1")
        Long organizationId,

        @Schema(description = "조직명", example = "짐짝피트니스 강남점")
        String organizationName,

        @Schema(description = "조직 주소", example = "서울특별시 강남구 테헤란로 123")
        String organizationAddress,

        @Schema(description = "조직 전화번호", example = "02-1234-5678")
        String organizationPhone,

        @Schema(description = "웹사이트 URL")
        String websiteUrl,

        @Schema(description = "인스타그램 URL")
        String instagramUrl,

        // ── 트레이너 정보 ──
        @Schema(description = "트레이너 프로필 ID", example = "1")
        Long trainerProfileId,

        @Schema(description = "트레이너 이름", example = "트레이너01")
        String trainerName,

        @Schema(description = "트레이너 프로필 이미지 파일 ID")
        Long trainerProfileImageFileId,

        /* Comment
        *   이 2개도 List로 수정!
        * */
        @Schema(description = "트레이너 자격증 목록(JSON 문자열)", example = "[\"생활체육지도자 2급\", \"NSCA-CPT\"]")
        List<String> trainerQualifications,

        @Schema(description = "트레이너 수상 경력 목록(JSON 문자열)", example = "[\"2023 피지크 대회 입상\", \"2024 체육인 대회 우승\"]")
        List<String> trainerAwardHistories,

        @Schema(description = "트레이너 소개")
        String trainerIntroduction,

        @Schema(description = "평균 평점", example = "4.6")
        Double averageRating,

        @Schema(description = "리뷰 수", example = "1")
        int reviewCount,

        @Schema(description = "자격증 목록 (미구현, 빈 배열 반환)")
        List<Object> certifications,

        @Schema(description = "수상 목록 (미구현, 빈 배열 반환)")
        List<Object> awards,

        @Schema(description = "커리큘럼 목록 (미구현, 빈 배열 반환)")
        List<Object> curriculums,

        @Schema(description = "최근 리뷰 목록 (미구현, 빈 배열 반환)")
        List<Object> recentReviews

) {
    public static PtCourseDetailResponse from(PtCourseQueryUseCase.PtCourseDetailView view) {
        return new PtCourseDetailResponse(
                view.ptCourseId(),
                view.categoryName(),
                view.tagId(),
                view.thumbnailFileId(),
                view.title(),
                view.description(),
                view.price(),
                view.totalSessionCount(),
                view.status(),
                view.organizationId(),
                view.organizationName(),
                view.organizationAddress(),
                view.organizationPhone(),
                view.websiteUrl(),
                view.instagramUrl(),
                view.trainerProfileId(),
                view.trainerName(),
                view.trainerProfileImageFileId(),
                view.trainerQualifications(),
                view.trainerAwardHistories(),
                view.trainerIntroduction(),
                view.averageRating(),
                view.reviewCount(),
                view.certifications(),
                view.awards(),
                view.curriculums(),
                view.recentReviews()
        );
    }
}
