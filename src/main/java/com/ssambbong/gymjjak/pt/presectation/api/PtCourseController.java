package com.ssambbong.gymjjak.pt.presectation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.pt.application.command.CreatePtCourseCommand;
import com.ssambbong.gymjjak.pt.application.usecase.PtCourseCommandUseCase;
import com.ssambbong.gymjjak.pt.presectation.api.request.CreatePtCourseRequest;
import com.ssambbong.gymjjak.pt.presectation.api.response.PtCourseResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pt-courses")
@RequiredArgsConstructor
public class PtCourseController {

    private final PtCourseCommandUseCase ptCourseCommandUseCase;

    // 트레이너만 PT 강습 등록 가능
    @PreAuthorize("hasAuthority('TRAINER')")
    @Operation(summary = "PT 강습 등록", description = "조직 소속 트레이너가 PT 강습을 등록한다.")
    @PostMapping
    public ResponseEntity<GlobalApiResponse<?>> createPtCourse(
            @AuthenticationPrincipal Long userId,
            @RequestBody @Valid CreatePtCourseRequest request
    ) {
        // TODO: userId로 trainerProfileId, organizationId 조회 후 수정 예정
        CreatePtCourseCommand command = new CreatePtCourseCommand(
                null,                    // organizationId - 임시
                null,                    // trainerProfileId - 임시
                request.categoryId(),
                request.tagId(),
                request.thumbnailFileId(),
                request.title(),
                request.description(),
                request.price(),
                request.totalSessionCount()
        );

        Long ptCourseId = ptCourseCommandUseCase.createPtCourse(command);
        return ResponseEntity.status(201)
                .body(GlobalApiResponse.created(PtCourseResponseCode.PT_COURSE_CREATED, ptCourseId));
    }
}
