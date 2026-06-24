package com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api;

import com.ssambbong.gymjjak.file.application.result.FileUrlResult;
import com.ssambbong.gymjjak.file.application.usecase.FileUrlUseCase;
import com.ssambbong.gymjjak.file.exception.FileNotFoundException;
import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.command.CancelTrainerApplicationCommand;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.command.CreateTrainerApplicationCommand;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.command.UpdateTrainerApplicationCommand;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.command.UploadedFileMetadataCommand;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationDetailResult;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.usecase.TrainerApplicationCommandUseCase;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.usecase.TrainerApplicationQueryUseCase;
import com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.request.CreateTrainerApplicationRequest;
import com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.request.UpdateTrainerApplicationRequest;
import com.ssambbong.gymjjak.file.presentation.api.request.UploadedFileMetadataRequest;
import com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.response.CreateTrainerApplicationResponse;
import com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.response.TrainerApplicationDetailResponse;
import com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.response.TrainerApplicationResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Trainer_Application", description = "트레이너 신청 api")
@Slf4j
@RestController
@RequestMapping("/api/trainer-applications")
@RequiredArgsConstructor
public class TrainerApplicationController {

    private final TrainerApplicationCommandUseCase trainerApplicationCommandUseCase;
    private final TrainerApplicationQueryUseCase trainerApplicationQueryUseCase;
    // S3 image URL을 반환하기 위해 의존성 추가
    private final FileUrlUseCase fileUrlUseCase;

    @Operation(
            summary = "트레이너 신청",
            description = "사용자가 트레이너 신청을 요청합니다. 자격증 파일은 OCR 검증에 사용됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "트레이너 신청 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "409", description = "이미 처리 중인 트레이너 신청 존재"),
            @ApiResponse(responseCode = "502", description = "OCR 외부 API 요청 실패")
    })
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping
    public ResponseEntity<GlobalApiResponse<CreateTrainerApplicationResponse>> createTrainerApplication(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody @Valid CreateTrainerApplicationRequest request
            ) {
        Long trainerApplicationId = trainerApplicationCommandUseCase.createTrainerApplication(
                new CreateTrainerApplicationCommand(
                        authUser.userId(),
                        toCommand(request.profileImageFile()),
                        toCommand(request.certificateFile()),
                        request.qualifications(),
                        request.awardHistories(),
                        request.introduction()
                )
        );

        return ResponseEntity.status(201)
                .body(GlobalApiResponse.created(
                        TrainerApplicationResponseCode.TRAINER_APPLICATION_CREATED,
                        new CreateTrainerApplicationResponse(trainerApplicationId)
                ));
    }

    private UploadedFileMetadataCommand toCommand(
            UploadedFileMetadataRequest request
    ) {
        if (request == null) {
            return null;
        }

        return new UploadedFileMetadataCommand(
                request.fileKey(),
                request.originalName(),
                request.contentType(),
                request.fileSize()
        );
    }

    @PatchMapping("/{trainerApplicationId}")
    @Operation(
            summary = "트레이너 신청서 수정",
            description = "사용자가 PENDING 상태의 트레이너 신청을 수정합니다. 필수 자격증 파일은 수정할 수 없습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "트레이너 신청서 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 필수 자격증 파일 수정 시도"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "본인의 트레이너 신청서가 아님"),
            @ApiResponse(responseCode = "404", description = "트레이너 신청서를 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "PENDING 상태가 아니어서 수정할 수 없음")
    })
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<GlobalApiResponse<CreateTrainerApplicationResponse>> updateTrainerApplication(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long trainerApplicationId,
            @RequestBody @Valid UpdateTrainerApplicationRequest request
    ) {
        Long updatedTrainerApplicationId = trainerApplicationCommandUseCase.updateTrainerApplication(
                new UpdateTrainerApplicationCommand(
                        trainerApplicationId,
                        authUser.userId(),
                        request.profileImageFileId(),
                        request.qualifications(),
                        request.awardHistories(),
                        request.introduction()
                )
        );

        return ResponseEntity.status(201)
                .body(GlobalApiResponse.created(
                       TrainerApplicationResponseCode.TRAINER_APPLICATION_UPDATED,
                       new CreateTrainerApplicationResponse(updatedTrainerApplicationId)
                ));
    }

    @GetMapping("/me")
    @Operation(
            summary = "내 트레이너 신청서 상세 조회",
            description = "로그인한 사용자의 최신 트레이너 신청서를 조회합니다. 신청 현황 화면에서 사용합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "트레이너 신청서 상세 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "트레이너 신청서를 찾을 수 없음")
    })
    @PreAuthorize("hasAnyAuthority('USER', 'TRAINER')")
    public ResponseEntity<GlobalApiResponse<TrainerApplicationDetailResponse>> getMyTrainerApplication(
            @AuthenticationPrincipal AuthUser authUser
    ) {

        TrainerApplicationDetailResult result =
                trainerApplicationQueryUseCase.getMyTrainerApplication(authUser.userId());

        FileUrlResult profileImageFile = resolveFileUrl(
                result.profileImageFileId(), authUser.userId(), false
        );

        FileUrlResult certificateFile = resolveFileUrl(
                result.certificateFileId(), authUser.userId(), false
        );

        return ResponseEntity.status(200).body(
                GlobalApiResponse.ok(
                        TrainerApplicationResponseCode.TRAINER_APPLICATION_DETAIL_FOUND,
                        TrainerApplicationDetailResponse.from(
                                result,
                                profileImageFile == null ? null : profileImageFile.url(),
                                profileImageFile == null ? null : profileImageFile.originalName(),
                                certificateFile == null ? null : certificateFile.url(),
                                certificateFile == null ? null : certificateFile.originalName()
                        )
                )
        );
    }

    // File 도메인에서 Id로 FileResult 받기
    private FileUrlResult resolveFileUrl(
            Long fileId,
            Long requesterId,
            boolean isAdmin
    ) {
        if (fileId == null) {
            return null;
        }

        try {
            return fileUrlUseCase.getUrl(
                    fileId,
                    requesterId,
                    isAdmin
            );
        } catch (FileNotFoundException exception) {
            log.warn(
                    "event=trainer_application_file_not_found, " +
                            "fileId={}, requesterId={}, isAdmin={}",
                    fileId,
                    requesterId,
                    isAdmin
            );
            return null;
        } catch (RuntimeException exception) {
            log.error(
                    "event=trainer_application_file_resolve_failed," +
                            "fileId={}, requesterId={}, isAdmin={}",
                    fileId,
                    requesterId,
                    isAdmin,
                    exception
            );
            return null;
        }
    }

    @DeleteMapping("/{trainerApplicationId}")
    @Operation(
            summary = "트레이너 신청 취소",
            description = "사용자가 본인의 PENDING 상태 트레이너 신청을 취소하고 삭제합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "트레이너 신청 취소 성공"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "본인의 트레이너 신청서가 아님"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "트레이너 신청서를 찾을 수 없음"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "PENDING 상태가 아니어서 취소할 수 없음"
            )
    })
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<GlobalApiResponse<Void>>  cancelTrainerApplication(
            @PathVariable Long trainerApplicationId,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        trainerApplicationCommandUseCase.cancelTrainerApplication(
                new CancelTrainerApplicationCommand(
                        trainerApplicationId,
                        authUser.userId()
                )
        );

        return ResponseEntity.status(200).body(
                GlobalApiResponse.ok(
                        TrainerApplicationResponseCode.TRAINER_APPLICATION_CANCELED,
                        null
                )
        );
    }
}
