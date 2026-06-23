package com.ssambbong.gymjjak.file.presentation.api.request;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UploadedFileMetadataRequest(
        @Schema(
                description = """
                        Presigned URL 발급 응답으로 받은 S3 객체 Key.
                        presignedUrl 전체 주소가 아니라 fileKey 값을 전달합니다.
                        클라이언트에서 임의로 생성하거나 수정하면 안 됩니다.
                        """,
                example = "uploads/profiles/trainers/2/550e8400-e29b-41d4-a716-446655440000"
        )
        @NotBlank(message = "fileKey는 필수입니다.")
        @Size(max = 500, message = "fileKey는 최대 500자까지 입력할 수 있습니다.")
        String fileKey,

        @Schema(
                description = """
                        사용자가 업로드한 파일의 원본 파일명.
                        화면에 파일명을 표시할 때 사용하며 S3 저장 경로로 사용하지 않습니다.
                        """,
                example = "trainer-profile.png"
        )
        @NotBlank(message = "originalName은 필수입니다.")
        @Size(max = 255, message = "originalName은 최대 255자까지 입력할 수 있습니다.")
        String originalName,

        @Schema(
                description = """
                        실제 업로드한 파일의 MIME 타입.
                        Presigned URL 발급 및 S3 PUT 업로드에 사용한 Content-Type과
                        동일한 값을 전달해야 합니다.
                        """,
                example = "image/png"
        )
        @NotBlank(message = "contentType은 필수입니다.")
        @Size(max = 100, message = "contentType은 최대 100자까지 입력할 수 있습니다.")
        String contentType,

        @Schema(description = "파일 크기(byte)", example = "524288")
        @NotNull(message = "fileSize는 필수입니다.")
        @Positive(message = "fileSize는 1 이상이어야 합니다.")
        Long fileSize
) {

}
