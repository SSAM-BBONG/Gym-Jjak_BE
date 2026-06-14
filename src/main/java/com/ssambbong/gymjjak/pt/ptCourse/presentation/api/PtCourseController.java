package com.ssambbong.gymjjak.pt.ptCourse.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.pt.ptCourse.application.command.CreatePtCourseCommand;
import com.ssambbong.gymjjak.pt.ptCourse.application.usecase.PtCourseCommandUseCase;
import com.ssambbong.gymjjak.pt.ptCourse.application.usecase.PtCourseQueryUseCase;
import com.ssambbong.gymjjak.pt.ptCourse.presentation.api.request.CreatePtCourseRequest;
import com.ssambbong.gymjjak.pt.ptCourse.presentation.api.response.CreatePtCourseResponse;
import com.ssambbong.gymjjak.pt.ptCourse.presentation.api.response.PtCourseDetailResponse;
import com.ssambbong.gymjjak.pt.ptCourse.presentation.api.response.PtCourseViewResponse;
import com.ssambbong.gymjjak.pt.ptCourse.presentation.api.response.PtCourseResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    // 트레이너만 PT 강습 등록 가능
    @PreAuthorize("hasAuthority('TRAINER')")
    @Operation(summary = "PT 강습 등록", description = "조직 소속 트레이너가 PT 강습을 등록한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "등록 성공",
                    content = @Content(schema = @Schema(implementation = CreatePtCourseResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "권한 없음 (TRAINER만 가능)",
                    content = @Content(schema = @Schema()))
    })
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
                request.thumbnailFileId(),
                request.curriculums().stream()
                        .map(c -> new CreatePtCourseCommand.CurriculumData(c.sessionNo(), c.title(), c.content()))
                        .toList(),
                request.schedules().stream()
                        .map(s -> new CreatePtCourseCommand.ScheduleData(s.dayOfWeek(), s.startTime(), s.endTime()))
                        .toList()
        );

        Long ptCourseId = ptCourseCommandUseCase.createPtCourse(command);

        return ResponseEntity.status(201)
                .body(GlobalApiResponse.created(PtCourseResponseCode.PT_COURSE_CREATED,
                        new CreatePtCourseResponse(ptCourseId)));
    }

    // 누구나 목록 조회 가능
    @Operation(summary = "PT 강습 목록 조회", description = "VISIBLE 상태의 PT 강습 목록을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = PtCourseViewResponse.class)))
    })
    @GetMapping
    public ResponseEntity<GlobalApiResponse<List<PtCourseViewResponse>>> findAllPtCourses() {
        List<PtCourseViewResponse> response = ptCourseQueryUseCase.findAllPtCourses().stream()
                .map(PtCourseViewResponse::from)
                .toList();
        return ResponseEntity.ok(
                GlobalApiResponse.ok(PtCourseResponseCode.PT_COURSE_LIST, response));
    }

    // 누구나 상세 조회 가능
    @Operation(summary = "PT 강습 상세 조회",
            description = "VISIBLE 상태의 PT 강습 상세 정보를 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = PtCourseDetailResponse.class))),
            @ApiResponse(responseCode = "404", description = "PT 강습을 찾을 수 없음",
                    content = @Content(schema = @Schema()))
    })
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
