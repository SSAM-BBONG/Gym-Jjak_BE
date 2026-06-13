package com.ssambbong.gymjjak.pt.presentation.api.response;

import com.ssambbong.gymjjak.pt.application.usecase.PtCourseQueryUseCase;
import io.swagger.v3.oas.annotations.media.Schema;

// PT 강습 목록 카드 응답 DTO
public record PtCourseViewResponse(

        @Schema(description = "PT 강습 ID", example = "1")
        Long ptCourseId,

        @Schema(description = "제목", example = "크로스핏 초급 클래스")
        String title,

        @Schema(description = "썸네일 파일 ID")
        Long thumbnailFileId,

        @Schema(description = "가격", example = "45000")
        int price,

        @Schema(description = "태그")
        TagInfo tag,

        @Schema(description = "카테고리")
        CategoryInfo category,

        @Schema(description = "트레이너 표시명", example = "Ket Trainer")
        String displayName,

        @Schema(description = "조직 정보")
        OrganizationInfo organization,

        @Schema(description = "리뷰 수", example = "127")
        int reviewCount

) {
    public record TagInfo(
            @Schema(description = "태그 ID", example = "1") Long tagId,
            @Schema(description = "태그명", example = "전신") String name
    ) {}

    public record CategoryInfo(
            @Schema(description = "카테고리 ID", example = "1") Long categoryId,
            @Schema(description = "카테고리명", example = "벌크업") String name
    ) {}

    public record OrganizationInfo(
            @Schema(description = "조직 ID", example = "1") Long organizationId,
            @Schema(description = "사업장명", example = "짐잭피트니스 본점") String businessName,
            @Schema(description = "도로명 주소", example = "서울시 강남구") String roadAddress,
            @Schema(description = "위도", example = "37.5012") Double latitude,
            @Schema(description = "경도", example = "127.0396") Double longitude
    ) {}

    public static PtCourseViewResponse from(PtCourseQueryUseCase.PtCourseListView view) {
        return new PtCourseViewResponse(
                view.ptCourseId(),
                view.title(),
                view.categoryName(),
                view.thumbnailFileId(),
                view.price(),
                new TagInfo(view.tagId(), view.tagName()),
                new CategoryInfo(view.categoryId(), view.categoryName()),
                view.displayName(),
                new OrganizationInfo(
                        view.organizationId(),
                        view.organizationBusinessName(),
                        view.organizationRoadAddress(),
                        view.latitude(),
                        view.longitude()
                ),
                view.reviewCount()
        );
    }
}
