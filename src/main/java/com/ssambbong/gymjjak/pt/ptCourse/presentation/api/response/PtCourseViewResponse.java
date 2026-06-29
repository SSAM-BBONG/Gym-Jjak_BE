package com.ssambbong.gymjjak.pt.ptCourse.presentation.api.response;

import com.ssambbong.gymjjak.pt.ptCourse.application.usecase.PtCourseQueryUseCase;
import io.swagger.v3.oas.annotations.media.Schema;

// PT 강습 목록 카드 응답 DTO
public record PtCourseViewResponse(

        @Schema(description = "PT 강습 ID", example = "1")
        Long ptCourseId,

        @Schema(description = "제목", example = "크로스핏 초급 클래스")
        String title,

        @Schema(description = "썸네일 이미지 URL")
        String thumbnailUrl,

        @Schema(description = "가격", example = "45000")
        int price,

        @Schema(description = "태그 ID", example = "1")
        Long tagId,

        @Schema(description = "태그명", example = "전신")
        String tagName,

        @Schema(description = "카테고리 ID", example = "1")
        Long categoryId,

        @Schema(description = "카테고리명", example = "벌크업")
        String categoryName,

        @Schema(description = "트레이너 이름", example = "Ket Trainer")
        String trainerName,

        @Schema(description = "조직 ID", example = "1")
        Long organizationId,

        @Schema(description = "사업장명", example = "짐잭피트니스 본점")
        String businessName,

        @Schema(description = "도로명 주소", example = "서울시 강남구")
        String roadAddress,

        @Schema(description = "위도", example = "37.5012")
        Double latitude,

        @Schema(description = "경도", example = "127.0396")
        Double longitude,

        @Schema(description = "리뷰 수", example = "127")
        int reviewCount

) {
    public static PtCourseViewResponse from(PtCourseQueryUseCase.PtCourseListView view) {
        return new PtCourseViewResponse(
                view.ptCourseId(),
                view.title(),
                view.thumbnailUrl(),
                view.price(),
                view.tagId(),
                view.tagName(),
                view.categoryId(),
                view.categoryName(),
                view.trainerName(),
                view.organizationId(),
                view.organizationBusinessName(),
                view.organizationRoadAddress(),
                view.latitude(),
                view.longitude(),
                view.reviewCount()
        );
    }
}
