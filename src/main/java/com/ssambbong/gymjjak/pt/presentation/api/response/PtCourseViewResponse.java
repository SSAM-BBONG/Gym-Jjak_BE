package com.ssambbong.gymjjak.pt.presentation.api.response;

import com.ssambbong.gymjjak.pt.application.usecase.PtCourseQueryUseCase;
import com.ssambbong.gymjjak.pt.domain.model.PtCourseStatus;
import io.swagger.v3.oas.annotations.media.Schema;

// PT 강습 목록 카드 응답 DTO
public record PtCourseViewResponse(

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

        @Schema(description = "가격", example = "350000")
        int price,

        @Schema(description = "전체 회차 수", example = "8")
        int totalSessionCount,

        @Schema(description = "상태", example = "VISIBLE")
        PtCourseStatus status,


        String organizationName,


        String organizationAddress,


        Double latitude,


        Double longitude,

        @Schema(description = "트레이너 이름", example = "트레이너01")
        String trainerName,

        @Schema(description = "트레이너 프로필 이미지 파일 ID")
        Long trainerProfileImageFileId,

        @Schema(description = "평균 평점", example = "4.6")
        Double averageRating,

        @Schema(description = "리뷰 수", example = "1")
        int reviewCount

) {
    public static PtCourseViewResponse from(PtCourseQueryUseCase.PtCourseListView view) {
        return new PtCourseViewResponse(
                view.ptCourseId(),
                view.categoryName(),
                view.tagId(),
                view.thumbnailFileId(),
                view.title(),
                view.price(),
                view.totalSessionCount(),
                view.status(),
                view.organizationName(),
                view.organizationAddress(),
                view.latitude(),
                view.longitude(),
                view.trainerName(),
                view.trainerProfileImageFileId(),
                view.averageRating(),
                view.reviewCount()
        );
    }
}
