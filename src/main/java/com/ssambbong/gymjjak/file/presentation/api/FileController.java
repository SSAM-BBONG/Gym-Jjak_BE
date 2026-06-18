package com.ssambbong.gymjjak.file.presentation.api;

import com.ssambbong.gymjjak.file.application.command.GeneratePresignedUrlCommand;
import com.ssambbong.gymjjak.file.application.command.GetPresignedUrlCommand;
import com.ssambbong.gymjjak.file.application.result.PresignedUrlResult;
import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
import com.ssambbong.gymjjak.file.presentation.api.request.GeneratePresignedUrlsRequest;
import com.ssambbong.gymjjak.file.presentation.api.response.FileResponseCode;
import com.ssambbong.gymjjak.file.presentation.api.response.GeneratePresignedUrlResponse;
import com.ssambbong.gymjjak.file.presentation.api.response.GeneratePresignedUrlsResponse;
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

import java.util.List;

@Tag(name = "File", description = "파일 API")
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class FileController {

    private final FileUseCase fileUseCase;

    @Operation(
            summary = "Presigned URL 일괄 발급",
            description = """
                    S3 직접 업로드를 위한 Presigned PUT URL을 여러 개 한 번에 발급합니다. (최대 10개)

                    **업로드 흐름**
                    1. 이 API 호출 → 파일별 `presignedUrl`, `fileKey` 목록 수신
                    2. 각 `presignedUrl`로 파일을 직접 PUT 업로드 (백엔드 거치지 않음)
                    3. 트레이너 신청 등 관련 API 호출 시 `fileKey`와 파일 메타데이터를 함께 전달
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Presigned URL 발급 성공",
                    content = @Content(schema = @Schema(implementation = GeneratePresignedUrlsResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (파일 타입 불일치, 필수 값 누락)",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping("/presigned-urls")
    public ResponseEntity<GlobalApiResponse<GeneratePresignedUrlsResponse>> generatePresignedUrls(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody @Valid GeneratePresignedUrlsRequest request
    ) {
        List<GeneratePresignedUrlCommand> commands = request.files().stream()
                .map(item -> new GeneratePresignedUrlCommand(
                        authUser.userId(), item.fileType(), item.contentType()))
                .toList();

        List<PresignedUrlResult> results = fileUseCase.generatePresignedUploadUrls(commands);

        List<GeneratePresignedUrlResponse> responseItems = results.stream()
                .map(r -> new GeneratePresignedUrlResponse(r.presignedUrl(), r.fileKey()))
                .toList();

        return ResponseEntity.ok(
                GlobalApiResponse.ok(FileResponseCode.FILE_PRESIGNED_URL_GENERATED,
                        new GeneratePresignedUrlsResponse(responseItems)));
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
        String url = fileUseCase.getPresignedUrl(
                new GetPresignedUrlCommand(fileId, authUser.userId(), authUser.role().equals("ADMIN")));
        return ResponseEntity.ok(GlobalApiResponse.ok(FileResponseCode.FILE_PRESIGNED_URL_RETRIEVED, url));
    }
}
