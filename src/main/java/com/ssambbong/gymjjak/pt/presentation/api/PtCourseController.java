package com.ssambbong.gymjjak.pt.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.security.principal.AuthUser;
import com.ssambbong.gymjjak.pt.application.command.CreatePtCourseCommand;
import com.ssambbong.gymjjak.pt.application.usecase.PtCourseCommandUseCase;
import com.ssambbong.gymjjak.pt.application.usecase.PtCourseQueryUseCase;
import com.ssambbong.gymjjak.pt.presentation.api.request.CreatePtCourseRequest;
import com.ssambbong.gymjjak.pt.presentation.api.response.CreatePtCourseResponse;
import com.ssambbong.gymjjak.pt.presentation.api.response.PtCourseDetailResponse;
import com.ssambbong.gymjjak.pt.presentation.api.response.PtCoursePageResponse;
import com.ssambbong.gymjjak.pt.presentation.api.response.PtCourseResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GlobalApiResponse<?>> createPtCourse(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestPart("data") @Valid CreatePtCourseRequest request,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail
    ) {
        CreatePtCourseCommand command = new CreatePtCourseCommand(
                authUser.userId(),
                request.categoryId(),
                request.tagId(),
                request.title(),
                request.description(),
                request.price(),
                request.totalSessionCount()
        );

        Long ptCourseId = ptCourseCommandUseCase.createPtCourse(thumbnail, command);

        return ResponseEntity.status(201)
                .body(GlobalApiResponse.created(PtCourseResponseCode.PT_COURSE_CREATED,
                        new CreatePtCourseResponse(ptCourseId)));
    }

    // 누구나 목록 조회 가능
    @Operation(summary = "PT 강습 목록 조회",
            description = "VISIBLE 상태의 PT 강습 목록을 페이지네이션으로 조회한다.")
    @GetMapping
    public ResponseEntity<GlobalApiResponse<?>> findAllPtCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PtCoursePageResponse response = PtCoursePageResponse.from(
                ptCourseQueryUseCase.findAllPtCourses(page, size));
        return ResponseEntity.ok(
                GlobalApiResponse.ok(PtCourseResponseCode.PT_COURSE_LIST, response));
    }

    // 누구나 상세 조회 가능
    @Operation(summary = "PT 강습 상세 조회",
            description = "VISIBLE 상태의 PT 강습 상세 정보를 조회한다.")
    @GetMapping("/{ptCourseId}")
    public ResponseEntity<GlobalApiResponse<?>> findPtCourse(@PathVariable Long ptCourseId) {
        PtCourseDetailResponse response = PtCourseDetailResponse.from(
                ptCourseQueryUseCase.findPtCourseDetail(ptCourseId));
        return ResponseEntity.ok(
                GlobalApiResponse.ok(PtCourseResponseCode.PT_COURSE_DETAIL, response));
    }
}
