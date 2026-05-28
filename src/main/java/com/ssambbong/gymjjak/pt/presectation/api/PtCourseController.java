package com.ssambbong.gymjjak.pt.presectation.api;

import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.pt.application.command.CreatePtCourseCommand;
import com.ssambbong.gymjjak.pt.application.usecase.PtCourseCommandUseCase;
import com.ssambbong.gymjjak.pt.presectation.api.request.CreatePtCourseRequest;
import com.ssambbong.gymjjak.pt.presectation.api.response.PtCourseResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/pt-courses")
@RequiredArgsConstructor
public class PtCourseController {

    private final PtCourseCommandUseCase ptCourseCommandUseCase;
    private final FileUseCase fileUseCase;

    // 트레이너만 PT 강습 등록 가능
    @PreAuthorize("hasAuthority('TRAINER')")
    @Operation(summary = "PT 강습 등록", description = "조직 소속 트레이너가 PT 강습을 등록한다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GlobalApiResponse<?>> createPtCourse(
            @AuthenticationPrincipal Long userId,
            @RequestPart("data") @Valid CreatePtCourseRequest request,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail
            ) {


        // TODO: userId로 trainerProfileId, organizationId 조회 후 수정 예정
        CreatePtCourseCommand command = new CreatePtCourseCommand(
                userId,
                null,// organizationId - 임시
                null,                    // trainerProfileId - 임시
                request.categoryId(),
                request.tagId(),
                request.title(),
                request.description(),
                request.price(),
                request.totalSessionCount()
        );

        Long ptCourseId = ptCourseCommandUseCase.createPtCourse(thumbnail,command);

        return ResponseEntity.status(201)
                .body(GlobalApiResponse.created(PtCourseResponseCode.PT_COURSE_CREATED, ptCourseId));
    }
}
