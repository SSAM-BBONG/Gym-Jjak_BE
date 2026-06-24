package com.ssambbong.gymjjak.trainer.trainerprofile.presentation.api;

import com.ssambbong.gymjjak.file.presentation.api.request.UploadedFileMetadataRequest;
import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.command.UpdateProfileImageFileCommand;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.command.UpdateTrainerProfileCommand;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.MyTrainerProfileResult;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.SearchTrainerCondition;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.SearchTrainerListResult;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.TrainerProfileDetailResult;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.usecase.TrainerProfileCommandUseCase;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.usecase.TrainerProfileQueryUseCase;
import com.ssambbong.gymjjak.trainer.trainerprofile.presentation.api.request.SearchTrainerRequest;
import com.ssambbong.gymjjak.trainer.trainerprofile.presentation.api.request.UpdateTrainerProfileRequest;
import com.ssambbong.gymjjak.trainer.trainerprofile.presentation.api.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "트레이너 프로필",
        description = "트레이너 프로필 api (승인 후 등록되는 테이블)"
)
@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
@Validated
public class TrainerProfileController {

    private final TrainerProfileQueryUseCase trainerProfileQueryUseCase;
    private final TrainerProfileCommandUseCase  trainerProfileCommandUseCase;

    @GetMapping("/{trainerProfileId}")
    @Operation(
            summary = "트레이너 프로필 상세 조회",
            description = """
                트레이너 프로필 ID로 공개 프로필 정보를 조회.
                일반 사용자, 트레이너, 관리자들이 조회할 수 있습니다.
                자격증 파일 등 비공개 파일 정보는 반환하지 않습니다.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "트레이너 프로필 상세 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 트레이너 프로필 ID"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "접근 권한 없음"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "트레이너 프로필을 찾을 수 없음"
            )
    })
    public ResponseEntity<GlobalApiResponse<TrainerProfileDetailResponse>
            > getTrainerProfileDetail(
                    @PathVariable
                    @Positive Long trainerProfileId
    ) {
        TrainerProfileDetailResult result =
                trainerProfileQueryUseCase.getTrainerProfileDetail(trainerProfileId);

        return ResponseEntity.status(200).body(
                GlobalApiResponse.ok(
                        TrainerProfileResponseCode.TRAINER_PROFILE_DETAIL_FOUND,
                        TrainerProfileDetailResponse.from(result)
                )
        );
    }


    @GetMapping("/me")
    @PreAuthorize("hasAuthority('TRAINER')")
    @Operation(
            summary = "내 트레이너 프로필 상세 조회",
            description = "로그인한 트레이너가 자신의 프로필, 자격증, 수상 경력을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "내 트레이너 프로필 상세 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "트레이너 권한 없음"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "트레이너 프로필을 찾을 수 없음"
            )
    })
    public ResponseEntity<GlobalApiResponse<MyTrainerProfileResponse>
            > getMyTrainerProfile(
            @AuthenticationPrincipal AuthUser authUser
            ) {
        MyTrainerProfileResult result =
                trainerProfileQueryUseCase.getMyTrainerProfile(
                        authUser.userId()
                );

        return ResponseEntity.status(200).body(
                GlobalApiResponse.ok(
                        TrainerProfileResponseCode.MY_TRAINER_PROFILE_FOUND,
                        MyTrainerProfileResponse.from(result)
                )
        );
    }

    @PatchMapping("/me")
    @PreAuthorize("hasAuthority('TRAINER')")
    @Operation(
            summary = "내 트레이너 프로필 수정",
            description = """
                로그인한 트레이너의 프로필 이미지, 추가 자격증,
                수상 및 대회 경력, 자기소개를 수정한다.
                이름과 필수 자격증은 수정 불가.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "내 트레이너 프로필 수정 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 수정 요청"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "트레이너 권한 없음"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "트레이너 프로필을 찾을 수 없음"
            )
    })
    public ResponseEntity<
            GlobalApiResponse<UpdateTrainerProfileResponse>> updateMyTrainerProfile(
            @AuthenticationPrincipal  AuthUser authUser,
            @RequestBody @Valid UpdateTrainerProfileRequest request
    ) {
        Long trainerProfileId = trainerProfileCommandUseCase.updateMyTrainerProfile(
                new UpdateTrainerProfileCommand(
                        authUser.userId(),
                        request.profileImageAction(),
                        toCommand(request.profileImageFile()),
                        request.additionalCertifications(),
                        request.awardHistories(),
                        request.introduction()
                )
        );

        return ResponseEntity.status(201).body(
                GlobalApiResponse.created(
                        TrainerProfileResponseCode.TRAINER_PROFILE_UPDATED,
                        new UpdateTrainerProfileResponse(trainerProfileId)
                )
        );
    }

    private UpdateProfileImageFileCommand toCommand(
            UploadedFileMetadataRequest request
    ) {
        if (request == null) {
            return null;
        }
        return new UpdateProfileImageFileCommand(
                request.fileKey(),
                request.originalName(),
                request.contentType(),
                request.fileSize()
        );
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ORGANIZATION', 'ADMIN')")
    @Operation(
            summary = "트레이너 검색",
            description = """
                이름, 로그인 아이디, 닉네임으로 활동 중인 트레이너를 검색합니다.
                조직 계정, admin만 사용할 수 있습니다.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "트레이너 검색 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 검색 조건"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 필요"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "조직 또는 관리자 권한 없음"
            )
    })
    public ResponseEntity<GlobalApiResponse<SearchTrainerListResponse>
            > searchTrainers(
            @ModelAttribute @Valid SearchTrainerRequest request
            ) {
        SearchTrainerListResult result =
                trainerProfileQueryUseCase.searchTrainers(
                        new SearchTrainerCondition(
                                request.normalizeKeyword(),
                                request.resolvePage(),
                                request.resolveSize()
                        )
                );

        return ResponseEntity.status(200).body(
                GlobalApiResponse.ok(
                        TrainerProfileResponseCode.TRAINER_PROFILE_SEARCHED,
                        SearchTrainerListResponse.from(result)
                )
        );
    }
}
