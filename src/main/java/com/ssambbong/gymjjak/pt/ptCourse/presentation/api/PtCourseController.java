package com.ssambbong.gymjjak.pt.ptCourse.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.pt.ptCourse.application.command.ChangePtCourseStatusCommand;
import com.ssambbong.gymjjak.pt.ptCourse.application.command.CreatePtCourseCommand;
import com.ssambbong.gymjjak.pt.ptCourse.application.command.DeletePtCourseCommand;
import com.ssambbong.gymjjak.pt.ptCourse.application.command.UploadedFileMetadataCommand;
import com.ssambbong.gymjjak.pt.ptCourse.application.usecase.PtCourseCommandUseCase;
import com.ssambbong.gymjjak.pt.ptCourse.application.usecase.PtCourseQueryUseCase;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseStatus;
import com.ssambbong.gymjjak.pt.ptCourse.presentation.api.request.ChangePtCourseStatusRequest;
import com.ssambbong.gymjjak.pt.ptCourse.presentation.api.request.CreatePtCourseRequest;
import com.ssambbong.gymjjak.pt.ptCourse.presentation.api.request.UpdatePtCourseRequest;
import com.ssambbong.gymjjak.file.presentation.api.request.UploadedFileMetadataRequest;
import com.ssambbong.gymjjak.pt.ptCourse.presentation.api.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
                toMetadataCommand(request.thumbnailFile()),
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

    // PT 강습 수정 (트레이너 전용)
    @PreAuthorize("hasAuthority('TRAINER')")
    @Operation(summary = "PT 강습 수정", description = "트레이너가 본인 PT 강습 정보를 수정한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = UpdatePtCourseResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 수강생이 있어 커리큘럼 수정 불가",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "본인 강습 아님",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "PT 강습을 찾을 수 없음",
                    content = @Content(schema = @Schema()))
    })
    @PatchMapping("/{ptCourseId}")
    public ResponseEntity<GlobalApiResponse<UpdatePtCourseResponse>> updatePtCourse(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long ptCourseId,
            @RequestBody @Valid UpdatePtCourseRequest request
    ) {
        Long updatedId = ptCourseCommandUseCase.updatePtCourse(request.toCommand(authUser.userId(), ptCourseId));
        return ResponseEntity.ok(GlobalApiResponse.ok(
                PtCourseResponseCode.PT_COURSE_UPDATED, new UpdatePtCourseResponse(updatedId)));
    }

    // PT 강습 상태 변경
    @PreAuthorize("hasAuthority('TRAINER')")
    @Operation(summary = "PT 강습 상태 변경", description = "트레이너가 본인 PT 강습을 VISIBLE/HIDDEN으로 전환한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상태 변경 성공",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "400", description = "허용되지 않는 상태값 (VISIBLE/HIDDEN만 가능)",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "본인 강습 아님",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "PT 강습을 찾을 수 없음",
                    content = @Content(schema = @Schema()))
    })
    @PatchMapping("/{ptCourseId}/status")
    public ResponseEntity<GlobalApiResponse<Void>> changePtCourseStatus(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long ptCourseId,
            @RequestBody @Valid ChangePtCourseStatusRequest request
    ) {
        ptCourseCommandUseCase.changePtCourseStatus(
                new ChangePtCourseStatusCommand(authUser.userId(), ptCourseId, request.status())
        );
        return ResponseEntity.ok(GlobalApiResponse.ok(PtCourseResponseCode.PT_COURSE_STATUS_UPDATED, null));
    }

    // 내 PT 강습 목록 조회
    @PreAuthorize("hasAuthority('TRAINER')")
    @Operation(summary = "내 PT 강습 목록 조회", description = "트레이너가 본인이 등록한 PT 강습 목록을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = MyPtCourseListResponse.class))),
            @ApiResponse(responseCode = "400", description = "허용되지 않는 status 값 (VISIBLE/HIDDEN만 가능)",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "트레이너 권한 없음",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "트레이너 프로필 없음",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/me")
    public ResponseEntity<GlobalApiResponse<List<MyPtCourseListResponse>>> findMyPtCourses(
            @AuthenticationPrincipal AuthUser authUser,
            @Parameter(
                    description = "강습 상태 필터 (미입력 시 전체)",
                    schema = @Schema(type = "string", allowableValues = {"VISIBLE", "HIDDEN"})
            )
            @RequestParam(required = false) PtCourseStatus status
    ) {
        List<MyPtCourseListResponse> response = ptCourseQueryUseCase
                .findMyPtCourses(authUser.userId(), status)
                .stream()
                .map(MyPtCourseListResponse::from)
                .toList();

        return ResponseEntity.ok(GlobalApiResponse.ok(
                PtCourseResponseCode.MY_PT_COURSES_FETCHED, response));
    }

    // 강습별 수강생 목록 조회 (트레이너 전용)
    @PreAuthorize("hasAuthority('TRAINER')")
    @Operation(summary = "강습별 수강생 목록 조회", description = "트레이너가 본인 강습의 수강생 목록을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = PtCourseReservationListResponse.class))),
            @ApiResponse(responseCode = "403", description = "본인 강습 아님",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "PT 강습을 찾을 수 없음",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/{ptCourseId}/reservations")
    public ResponseEntity<GlobalApiResponse<PtCourseReservationListResponse>> findCourseReservations(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long ptCourseId
    ) {
        PtCourseReservationListResponse response = PtCourseReservationListResponse.from(
                ptCourseQueryUseCase.findCourseReservations(authUser.userId(), ptCourseId)
        );
        return ResponseEntity.ok(GlobalApiResponse.ok(
                PtCourseResponseCode.COURSE_RESERVATIONS_FETCHED, response));
    }

    // 수강생 상세 조회 (트레이너 전용)
    @PreAuthorize("hasAuthority('TRAINER')")
    @Operation(summary = "수강생 상세 조회", description = "트레이너가 본인 강습의 특정 수강생 상세 정보를 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = PtCourseReservationDetailResponse.class))),
            @ApiResponse(responseCode = "403", description = "본인 강습 아님",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "PT 강습 또는 예약을 찾을 수 없음",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/reservations/{reservationId}")
    public ResponseEntity<GlobalApiResponse<PtCourseReservationDetailResponse>> findReservationDetail(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable("reservationId") Long ptReservationId
    ) {
        PtCourseReservationDetailResponse response = PtCourseReservationDetailResponse.from(
                ptCourseQueryUseCase.findReservationDetail(authUser.userId(), ptReservationId)
        );
        return ResponseEntity.ok(GlobalApiResponse.ok(
                PtCourseResponseCode.STUDENT_DETAIL_FETCHED, response));
    }

    // PT 강습 삭제 (트레이너 전용)
    @PreAuthorize("hasAuthority('TRAINER')")
    @Operation(summary = "PT 강습 삭제", description = "트레이너가 본인 PT 강습을 삭제한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "본인 강습 아님",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "PT 강습을 찾을 수 없음",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "409", description = "BLOCKED 상태이거나 활성 예약 존재",
                    content = @Content(schema = @Schema()))
    })
    @DeleteMapping("/{ptCourseId}")
    public ResponseEntity<GlobalApiResponse<Void>> deletePtCourse(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long ptCourseId
    ) {
        ptCourseCommandUseCase.deletePtCourse(new DeletePtCourseCommand(authUser.userId(), ptCourseId));
        return ResponseEntity.ok(
                GlobalApiResponse.ok(
                PtCourseResponseCode.PT_COURSE_DELETED,
                null));
    }

    // 누구나 인기 강습 조회 가능 (PT 메인 페이지용)
    @Operation(summary = "인기 강습 조회", description = "예약 수 기준 상위 8개 VISIBLE 강습을 조회한다.")
    @GetMapping("/popular")
    public ResponseEntity<GlobalApiResponse<List<PopularPtCourseResponse>>> findPopularPtCourses() {
        List<PopularPtCourseResponse> response = ptCourseQueryUseCase.findPopular().stream()
                .map(PopularPtCourseResponse::from)
                .toList();
        return ResponseEntity.ok(
                GlobalApiResponse.ok(PtCourseResponseCode.PT_COURSE_POPULAR, response));
    }


    // UploadedFileMetadataRequest → UploadedFileMetadataCommand 변환 (null 허용)
    private UploadedFileMetadataCommand toMetadataCommand(UploadedFileMetadataRequest request) {
        if (request == null) return null;
        return new UploadedFileMetadataCommand(
                request.fileKey(),
                request.originalName(),
                request.contentType(),
                request.fileSize()
        );
    }
}
