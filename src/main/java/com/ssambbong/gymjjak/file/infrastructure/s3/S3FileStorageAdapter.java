package com.ssambbong.gymjjak.file.infrastructure.s3;

import com.ssambbong.gymjjak.file.exception.FileUploadException;
import com.ssambbong.gymjjak.file.domain.repository.FileStoragePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3FileStorageAdapter implements FileStoragePort {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final S3Properties s3Properties;

    @Override
    public String generatePresignedUploadUrl(String key, String contentType) {
        return s3Presigner.presignPutObject(
                PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(15))
                        .putObjectRequest(req -> req
                                .bucket(s3Properties.getS3().getBucket())
                                .key(key)
                                .contentType(contentType)
                                .build())
                        .build()
        ).url().toString();
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
