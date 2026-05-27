package com.ssambbong.gymjjak.file.infrastructure.s3;

import com.ssambbong.gymjjak.file.exception.FileUploadException;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import com.ssambbong.gymjjak.file.domain.repository.FileStoragePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3FileStorageAdapter implements FileStoragePort {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final S3Properties s3Properties;

    @Override
    public String upload(MultipartFile file, FileType fileType, Long uploaderId) {
        // 1. S3 key 생성
        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String key = ext != null
                ? String.format("%s/%d/%s.%s", fileType.getPath(), uploaderId, UUID.randomUUID(), ext)
                : String.format("%s/%d/%s", fileType.getPath(), uploaderId, UUID.randomUUID());

        // 2. S3 업로드
        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(s3Properties.getS3().getBucket())
                            .key(key)
                            .contentType(file.getContentType())
                            .contentLength(file.getSize())
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
            log.info("S3 업로드 성공 - key: {}", key);
        } catch (IOException e) {
            log.error("S3 업로드 실패 (IO) - uploaderId: {}, fileType: {}", uploaderId, fileType);
            throw new FileUploadException(e);
        } catch (S3Exception e) {
            log.error("S3 업로드 실패 (S3) - uploaderId: {}, fileType: {}, errorCode: {}",
                    uploaderId, fileType, e.awsErrorDetails().errorCode());
            throw new FileUploadException(e);
        }

        return key;
    }

    @Override
    public String getPresignedUrl(String key) {
        return s3Presigner.presignGetObject(
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofHours(1))
                        .getObjectRequest(req -> req
                                .bucket(s3Properties.getS3().getBucket())
                                .key(key)
                                .build())
                        .build()
        ).url().toString();
    }

    @Override
    public void delete(String key) {
        try {
            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(s3Properties.getS3().getBucket())
                            .key(key)
                            .build()
            );
            log.info("S3 파일 삭제 완료 - key: {}", key);
        } catch (S3Exception e) {
            log.error("S3 파일 삭제 실패 - key: {}, errorCode: {}",
                    key, e.awsErrorDetails().errorCode());
            throw new FileUploadException(e);
        }
    }
}
