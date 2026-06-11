package com.ssambbong.gymjjak.file.presentation.api;

import com.ssambbong.gymjjak.file.application.command.FileUploadCommand;
import com.ssambbong.gymjjak.file.application.result.PresignedUrlResult;
import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
import com.ssambbong.gymjjak.file.presentation.api.request.GeneratePresignedUrlRequest;
import com.ssambbong.gymjjak.file.presentation.api.request.RegisterFileRequest;
import com.ssambbong.gymjjak.file.presentation.api.response.GeneratePresignedUrlResponse;
import com.ssambbong.gymjjak.file.presentation.api.response.RegisterFileResponse;
import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "File", description = "파일 API")
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class FileController {

    private final FileUseCase fileUseCase;

    @Operation(
            summary = "Presigned URL 발급",
            description = """
                    S3 직접 업로드를 위한 Presigned PUT URL을 발급합니다.

                    **업로드 흐름**
                    1. 이 API 호출 → `presignedUrl`, `fileKey` 수신
                    2. `presignedUrl`로 파일을 직접 PUT 업로드 (백엔드 거치지 않음)
                    3. `POST /api/files` 호출로 메타데이터 등록 → `fileId` 수신
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Presigned URL 발급 성공",
                    content = @Content(schema = @Schema(implementation = GeneratePresignedUrlResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping("/presigned-url")
    public ResponseEntity<GlobalApiResponse<GeneratePresignedUrlResponse>> generatePresignedUrl(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody @Valid GeneratePresignedUrlRequest request
    ) {
        PresignedUrlResult result = fileUseCase.generatePresignedUploadUrl(
                authUser.userId(), request.fileType(), request.contentType(), request.originalName());
        return ResponseEntity.ok(
                GlobalApiResponse.ok(FileResponseCode.FILE_PRESIGNED_URL_GENERATED,
                        new GeneratePresignedUrlResponse(result.presignedUrl(), result.fileKey())));
    }

    @Operation(
            summary = "파일 메타데이터 등록",
            description = """
                    S3 업로드 완료 후 파일 메타데이터를 DB에 저장하고 `fileId`를 반환합니다.

                    반환된 `fileId`는 조직 신청, PT 강습 등록 등 다른 API 요청 시 사용합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "파일 등록 성공",
                    content = @Content(schema = @Schema(implementation = RegisterFileResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (필수 값 누락)",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping
    public ResponseEntity<GlobalApiResponse<RegisterFileResponse>> registerFile(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody @Valid RegisterFileRequest request
    ) {
        FileUploadCommand command = new FileUploadCommand(
                authUser.userId(),
                request.fileKey(),
                request.originalName(),
                request.contentType(),
                request.fileSize(),
                request.fileType()
        );
        Long fileId = fileUseCase.registerFile(command);
        return ResponseEntity.status(201)
                .body(GlobalApiResponse.created(FileResponseCode.FILE_REGISTERED,
                        new RegisterFileResponse(fileId)));
    }

    @Operation(
            summary = "파일 조회 URL 발급",
            description = """
                    fileId로 S3 GET Presigned URL을 발급합니다.

                    - 공개 파일(PT 썸네일, 프로필 이미지 등): 인증된 사용자 누구나 접근 가능
                    - 비공개 파일(사업자등록증, 자격증, 수상 이력): 소유자 본인 또는 관리자만 접근 가능
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 URL 발급 성공",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/{fileId}/presigned-url")
    public ResponseEntity<GlobalApiResponse<String>> getPresignedUrl(
            @PathVariable Long fileId,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        boolean isAdmin = authUser.role().equals("ADMIN");
        String url = fileUseCase.getPresignedUrl(fileId, authUser.userId(), isAdmin);
        return ResponseEntity.ok(GlobalApiResponse.ok(FileResponseCode.FILE_PRESIGNED_URL_RETRIEVED, url));
    }

}
