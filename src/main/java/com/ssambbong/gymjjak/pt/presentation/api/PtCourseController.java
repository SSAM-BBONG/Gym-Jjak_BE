package com.ssambbong.gymjjak.pt.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.pt.application.command.CreatePtCourseCommand;
import com.ssambbong.gymjjak.pt.application.usecase.PtCourseCommandUseCase;
import com.ssambbong.gymjjak.pt.application.usecase.PtCourseQueryUseCase;
import com.ssambbong.gymjjak.pt.presentation.api.request.CreatePtCourseRequest;
import com.ssambbong.gymjjak.pt.presentation.api.response.CreatePtCourseResponse;
import com.ssambbong.gymjjak.pt.presentation.api.response.PtCourseDetailResponse;
import com.ssambbong.gymjjak.pt.presentation.api.response.PtCourseViewResponse;
import com.ssambbong.gymjjak.pt.presentation.api.response.PtCourseResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "PT", description = "PT 관련 API")
@RestController
@RequestMapping("/api/pt-courses")
@RequiredArgsConstructor
public class PtCourseController {

    private final PtCourseCommandUseCase ptCourseCommandUseCase;
    private final PtCourseQueryUseCase ptCourseQueryUseCase;

    @PreAuthorize("hasAuthority('TRAINER')")
    @Operation(summary = "PT 강습 등록", description = "조직 소속 트레이너가 PT 강습을 등록한다.")
    @PostMapping
    public ResponseEntity<GlobalApiResponse<CreatePtCourseResponse>> createPtCourse(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody @Valid CreatePtCourseRequest request
    ) {
        CreatePtCourseCommand command = new CreatePtCourseCommand(
                authUser.userId(),
                request.categoryId(),
                request.tagId(),
                request.title(),
                request.description(),
                request.price(),
                request.totalSessionCount(),
                request.thumbnailFileId()
        );

        Long ptCourseId = ptCourseCommandUseCase.createPtCourse(command);

        return ResponseEntity.status(201)
                .body(GlobalApiResponse.created(PtCourseResponseCode.PT_COURSE_CREATED,
                        new CreatePtCourseResponse(ptCourseId)));
    }

    @Operation(summary = "PT 강습 목록 조회", description = "VISIBLE 상태의 PT 강습 목록을 조회한다.")
    @GetMapping
    public ResponseEntity<GlobalApiResponse<List<PtCourseViewResponse>>> findAllPtCourses() {
        List<PtCourseViewResponse> response = ptCourseQueryUseCase.findAllPtCourses().stream()
                .map(PtCourseViewResponse::from)
                .toList();
        return ResponseEntity.ok(
                GlobalApiResponse.ok(PtCourseResponseCode.PT_COURSE_LIST, response));
    }

    @Operation(summary = "PT 강습 상세 조회",
            description = "VISIBLE 상태의 PT 강습 상세 정보를 조회한다.")
    @GetMapping("/{ptCourseId}")
    public ResponseEntity<GlobalApiResponse<PtCourseDetailResponse>> findPtCourse(@PathVariable Long ptCourseId) {
        PtCourseDetailResponse response = PtCourseDetailResponse.from(
                ptCourseQueryUseCase.findPtCourseDetail(ptCourseId));
        return ResponseEntity.ok(
                GlobalApiResponse.ok(
                        PtCourseResponseCode.PT_COURSE_DETAIL,
                        response));
    }
}
