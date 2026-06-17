package com.ssambbong.gymjjak.file;

import com.ssambbong.gymjjak.file.application.command.CreateFileCommand;
import com.ssambbong.gymjjak.file.application.command.GeneratePresignedUrlCommand;
import com.ssambbong.gymjjak.file.application.command.GetPresignedUrlCommand;
import com.ssambbong.gymjjak.file.application.port.FileMetricsPort;
import com.ssambbong.gymjjak.file.application.result.FileRegistrationResult;
import com.ssambbong.gymjjak.file.application.result.PresignedUrlResult;
import com.ssambbong.gymjjak.file.application.service.FileService;
import com.ssambbong.gymjjak.file.domain.model.File;
import com.ssambbong.gymjjak.file.domain.repository.FileRepository;
import com.ssambbong.gymjjak.file.domain.repository.FileStoragePort;
import com.ssambbong.gymjjak.file.exception.FileAccessDeniedException;
import com.ssambbong.gymjjak.file.exception.FileNotFoundException;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock private FileStoragePort fileStoragePort;
    @Mock private FileRepository fileRepository;
    @Mock private FileMetricsPort fileMetricsPort;

    @InjectMocks
    private FileService fileService;

    @Test
    @DisplayName("Presigned Upload URL 일괄 발급에 성공한다")
    void generatePresignedUploadUrls_success() {
        String presignedUrl = "https://s3.amazonaws.com/bucket/key?X-Amz-Signature=abc";
        when(fileStoragePort.generatePresignedUploadUrl(anyString(), eq("application/pdf")))
                .thenReturn(presignedUrl);

        List<PresignedUrlResult> results = fileService.generatePresignedUploadUrls(List.of(
                new GeneratePresignedUrlCommand(1L, FileType.BUSINESS_LICENSE, "application/pdf")));

        assertThat(results).hasSize(1);
        assertThat(results.get(0).presignedUrl()).isEqualTo(presignedUrl);
        assertThat(results.get(0).fileKey()).contains("uploads/organizations");
        verify(fileStoragePort).generatePresignedUploadUrl(anyString(), eq("application/pdf"));
    }

    @Test
    @DisplayName("여러 파일 타입을 한 번에 발급할 수 있다")
    void generatePresignedUploadUrls_multipleTypes() {
        when(fileStoragePort.generatePresignedUploadUrl(anyString(), anyString()))
                .thenReturn("https://s3.amazonaws.com/presigned");

        List<PresignedUrlResult> results = fileService.generatePresignedUploadUrls(List.of(
                new GeneratePresignedUrlCommand(1L, FileType.PROFILE_IMAGE, "image/jpeg"),
                new GeneratePresignedUrlCommand(1L, FileType.CERTIFICATION, "image/jpeg"),
                new GeneratePresignedUrlCommand(1L, FileType.AWARD, "image/png")));

        assertThat(results).hasSize(3);
        verify(fileStoragePort, times(3)).generatePresignedUploadUrl(anyString(), anyString());
    }

    @Test
    @DisplayName("파일 메타데이터 일괄 등록에 성공하고 fileId 목록을 반환한다")
    void registerFiles_success() {
        CreateFileCommand command = new CreateFileCommand(
                1L,
                "uploads/organizations/1/uuid.pdf",
                "사업자등록증.pdf",
                "application/pdf",
                204800L,
                FileType.BUSINESS_LICENSE
        );

        File savedFile = mock(File.class);
        when(savedFile.getFileId()).thenReturn(1L);
        when(fileRepository.save(any())).thenReturn(savedFile);

        List<FileRegistrationResult> results = fileService.registerFiles(List.of(command));

        assertThat(results).hasSize(1);
        assertThat(results.get(0).fileId()).isEqualTo(1L);
        assertThat(results.get(0).fileType()).isEqualTo(FileType.BUSINESS_LICENSE);
        verify(fileRepository).save(any());
    }

    @Test
    @DisplayName("발급된 key는 uploaderId와 fileType 경로를 포함한다")
    void generatePresignedUploadUrls_keyContainsUploaderIdAndPath() {
        when(fileStoragePort.generatePresignedUploadUrl(anyString(), anyString()))
                .thenReturn("https://s3.amazonaws.com/presigned");

        List<PresignedUrlResult> results = fileService.generatePresignedUploadUrls(List.of(
                new GeneratePresignedUrlCommand(42L, FileType.PROFILE_IMAGE, "image/jpeg")));

        assertThat(results.get(0).fileKey()).contains("uploads/profiles/trainers");
        assertThat(results.get(0).fileKey()).contains("/42/");
    }

    @Test
    @DisplayName("여러 파일 등록 시 각 파일의 fileType이 결과에 포함된다")
    void registerFiles_success_multipleFiles() {
        File savedFile1 = mock(File.class);
        File savedFile2 = mock(File.class);
        when(savedFile1.getFileId()).thenReturn(1L);
        when(savedFile2.getFileId()).thenReturn(2L);
        when(fileRepository.save(any()))
                .thenReturn(savedFile1)
                .thenReturn(savedFile2);

        List<FileRegistrationResult> results = fileService.registerFiles(List.of(
                new CreateFileCommand(1L, "uploads/profiles/trainers/1/uuid1", "profile.jpg", "image/jpeg", 102400L, FileType.PROFILE_IMAGE),
                new CreateFileCommand(1L, "uploads/certifications/1/uuid2", "cert.jpg", "image/jpeg", 204800L, FileType.CERTIFICATION)
        ));

        assertThat(results).hasSize(2);
        assertThat(results.get(0).fileId()).isEqualTo(1L);
        assertThat(results.get(0).fileType()).isEqualTo(FileType.PROFILE_IMAGE);
        assertThat(results.get(1).fileId()).isEqualTo(2L);
        assertThat(results.get(1).fileType()).isEqualTo(FileType.CERTIFICATION);
    }

    @Test
    @DisplayName("Presigned URL 발급 시 허용되지 않는 contentType이면 실패한다")
    void generatePresignedUploadUrls_fail_invalidContentType() {
        assertThatThrownBy(() -> fileService.generatePresignedUploadUrls(List.of(
                new GeneratePresignedUrlCommand(1L, FileType.BUSINESS_LICENSE, "video/mp4"))))
                .isInstanceOf(com.ssambbong.gymjjak.file.exception.InvalidFileException.class);
    }

    @Test
    @DisplayName("공개 파일은 소유자가 아니어도 고정 S3 URL을 반환한다")
    void getPresignedUrl_success_publicFile() {
        File file = mock(File.class);
        when(file.getFileUrl()).thenReturn("uploads/pt-thumbnails/1/uuid.jpg");
        when(file.getFileType()).thenReturn(FileType.PT_THUMBNAIL);
        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));
        when(fileStoragePort.getPublicUrl("uploads/pt-thumbnails/1/uuid.jpg"))
                .thenReturn("https://bucket.s3.ap-northeast-2.amazonaws.com/uploads/pt-thumbnails/1/uuid.jpg");

        String url = fileService.getPresignedUrl(new GetPresignedUrlCommand(1L, 99L, false));

        assertThat(url).startsWith("https://");
        verify(fileStoragePort).getPublicUrl(anyString());
        verify(fileStoragePort, never()).getPresignedUrl(anyString());
    }

    @Test
    @DisplayName("비공개 파일은 소유자가 Presigned URL 발급에 성공한다")
    void getPresignedUrl_success_privateFile_owner() {
        File file = mock(File.class);
        when(file.getFileUrl()).thenReturn("uploads/organizations/1/uuid.pdf");
        when(file.getFileType()).thenReturn(FileType.BUSINESS_LICENSE);
        when(file.getUploaderId()).thenReturn(1L);
        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));
        when(fileStoragePort.getPresignedUrl(anyString()))
                .thenReturn("https://s3.amazonaws.com/bucket/key?X-Amz-Signature=xyz");

        String url = fileService.getPresignedUrl(new GetPresignedUrlCommand(1L, 1L, false));

        assertThat(url).startsWith("https://");
    }

    @Test
    @DisplayName("비공개 파일은 ADMIN이면 소유자가 아니어도 Presigned URL 발급에 성공한다")
    void getPresignedUrl_success_privateFile_admin() {
        File file = mock(File.class);
        when(file.getFileUrl()).thenReturn("uploads/organizations/1/uuid.pdf");
        when(file.getFileType()).thenReturn(FileType.BUSINESS_LICENSE);
        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));
        when(fileStoragePort.getPresignedUrl(anyString()))
                .thenReturn("https://s3.amazonaws.com/bucket/key?X-Amz-Signature=xyz");

        String url = fileService.getPresignedUrl(new GetPresignedUrlCommand(1L, 99L, true));

        assertThat(url).startsWith("https://");
    }

    @Test
    @DisplayName("비공개 파일을 타인이 조회하면 FileAccessDeniedException이 발생한다")
    void getPresignedUrl_fail_accessDenied() {
        File file = mock(File.class);
        when(file.getFileType()).thenReturn(FileType.BUSINESS_LICENSE);
        when(file.getUploaderId()).thenReturn(1L);
        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));

        assertThatThrownBy(() -> fileService.getPresignedUrl(new GetPresignedUrlCommand(1L, 99L, false)))
                .isInstanceOf(FileAccessDeniedException.class);
    }

    @Test
    @DisplayName("존재하지 않는 fileId로 Presigned URL 조회 시 FileNotFoundException이 발생한다")
    void getPresignedUrl_fail_notFound() {
        when(fileRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fileService.getPresignedUrl(new GetPresignedUrlCommand(999L, 1L, false)))
                .isInstanceOf(FileNotFoundException.class);
    }

    @Test
    @DisplayName("다른 사용자의 fileKey로 registerFiles 시 FileAccessDeniedException이 발생한다")
    void registerFiles_fail_invalidFileKey() {
        CreateFileCommand command = new CreateFileCommand(
                1L,
                "uploads/organizations/2/uuid.pdf",
                "사업자등록증.pdf",
                "application/pdf",
                204800L,
                FileType.BUSINESS_LICENSE
        );

        assertThatThrownBy(() -> fileService.registerFiles(List.of(command)))
                .isInstanceOf(FileAccessDeniedException.class);
    }

    @Test
    @DisplayName("fileType 경로와 불일치하는 fileKey로 registerFiles 시 FileAccessDeniedException이 발생한다")
    void registerFiles_fail_wrongFileTypePath() {
        CreateFileCommand command = new CreateFileCommand(
                1L,
                "uploads/pt-thumbnails/1/uuid.pdf",
                "사업자등록증.pdf",
                "application/pdf",
                204800L,
                FileType.BUSINESS_LICENSE
        );

        assertThatThrownBy(() -> fileService.registerFiles(List.of(command)))
                .isInstanceOf(FileAccessDeniedException.class);
    }

    @Test
    @DisplayName("파일 삭제 시 DB hard delete 후 S3 파일을 삭제한다")
    void deleteFile_success() {
        File file = mock(File.class);
        when(file.getFileUrl()).thenReturn("uploads/organizations/1/uuid.pdf");
        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));

        fileService.deleteFile(1L);

        var inOrder = inOrder(fileRepository, fileStoragePort);
        inOrder.verify(fileRepository).deleteById(1L);
        inOrder.verify(fileStoragePort).delete("uploads/organizations/1/uuid.pdf");
    }

    @Test
    @DisplayName("존재하지 않는 fileId 삭제 시 FileNotFoundException이 발생한다")
    void deleteFile_fail_notFound() {
        when(fileRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fileService.deleteFile(999L))
                .isInstanceOf(FileNotFoundException.class);

        verify(fileStoragePort, never()).delete(any());
    }

    @Test
    @DisplayName("파일 삭제 성공 시 메트릭이 기록된다")
    void deleteFile_success_recordsMetric() {
        File file = mock(File.class);
        when(file.getFileUrl()).thenReturn("uploads/organizations/1/uuid.pdf");
        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));

        fileService.deleteFile(1L);

        verify(fileMetricsPort).recordFileDeleted();
    }

    @Test
    @DisplayName("파일이 존재하지 않으면 메트릭이 기록되지 않는다")
    void deleteFile_fail_doesNotRecordMetric() {
        when(fileRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fileService.deleteFile(999L))
                .isInstanceOf(FileNotFoundException.class);

        verify(fileMetricsPort, never()).recordFileDeleted();
    }

    @Test
    @DisplayName("메트릭 기록 실패가 deleteFile 비즈니스 로직에 영향을 주지 않는다")
    void deleteFile_metricFailure_doesNotAffectBusiness() {
        File file = mock(File.class);
        when(file.getFileUrl()).thenReturn("uploads/organizations/1/uuid.pdf");
        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));
        doThrow(new RuntimeException("metric error")).when(fileMetricsPort).recordFileDeleted();

        assertThatCode(() -> fileService.deleteFile(1L))
                .doesNotThrowAnyException();

        verify(fileRepository).deleteById(1L);
        verify(fileStoragePort).delete("uploads/organizations/1/uuid.pdf");
    }
}
