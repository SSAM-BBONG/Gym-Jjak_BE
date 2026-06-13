package com.ssambbong.gymjjak.file;

import com.ssambbong.gymjjak.file.application.command.FileUploadCommand;
import com.ssambbong.gymjjak.file.application.port.FileMetricsPort;
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
    @DisplayName("Presigned Upload URL 발급에 성공한다")
    void generatePresignedUploadUrl_success() {
        // given
        String presignedUrl = "https://s3.amazonaws.com/bucket/key?X-Amz-Signature=abc";
        when(fileStoragePort.generatePresignedUploadUrl(anyString(), eq("application/pdf")))
                .thenReturn(presignedUrl);

        // when
        PresignedUrlResult result = fileService.generatePresignedUploadUrl(
                1L, FileType.BUSINESS_LICENSE, "application/pdf", "사업자등록증.pdf");

        // then
        assertThat(result.presignedUrl()).isEqualTo(presignedUrl);
        assertThat(result.fileKey()).contains("uploads/organizations");
        assertThat(result.fileKey()).endsWith(".pdf");
        verify(fileStoragePort).generatePresignedUploadUrl(anyString(), eq("application/pdf"));
    }

    @Test
    @DisplayName("파일 메타데이터 등록에 성공하고 fileId를 반환한다")
    void registerFile_success() {
        // given
        FileUploadCommand command = new FileUploadCommand(
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

        // when
        Long fileId = fileService.registerFile(command);

        // then
        assertThat(fileId).isEqualTo(1L);
        verify(fileRepository).save(any());
    }

    @Test
    @DisplayName("Presigned URL 발급 시 허용되지 않는 contentType이면 실패한다")
    void generatePresignedUploadUrl_fail_invalidContentType() {
        // when & then
        assertThatThrownBy(() -> fileService.generatePresignedUploadUrl(
                1L, FileType.BUSINESS_LICENSE, "video/mp4", "사업자등록증.pdf"))
                .isInstanceOf(com.ssambbong.gymjjak.file.exception.InvalidFileException.class);
    }

    @Test
    @DisplayName("공개 파일은 소유자가 아니어도 고정 S3 URL을 반환한다")
    void getPresignedUrl_success_publicFile() {
        // given — PT_THUMBNAIL은 requiresOwnershipCheck = false → getPublicUrl 호출
        File file = mock(File.class);
        when(file.getFileUrl()).thenReturn("uploads/pt-thumbnails/1/uuid.jpg");
        when(file.getFileType()).thenReturn(FileType.PT_THUMBNAIL);
        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));
        when(fileStoragePort.getPublicUrl("uploads/pt-thumbnails/1/uuid.jpg"))
                .thenReturn("https://bucket.s3.ap-northeast-2.amazonaws.com/uploads/pt-thumbnails/1/uuid.jpg");

        // when
        String url = fileService.getPresignedUrl(1L, 99L, false);

        // then
        assertThat(url).startsWith("https://");
        verify(fileStoragePort).getPublicUrl(anyString());
        verify(fileStoragePort, never()).getPresignedUrl(anyString());
    }

    @Test
    @DisplayName("비공개 파일은 소유자가 Presigned URL 발급에 성공한다")
    void getPresignedUrl_success_privateFile_owner() {
        // given — BUSINESS_LICENSE는 requiresOwnershipCheck = true
        File file = mock(File.class);
        when(file.getFileUrl()).thenReturn("uploads/organizations/1/uuid.pdf");
        when(file.getFileType()).thenReturn(FileType.BUSINESS_LICENSE);
        when(file.getUploaderId()).thenReturn(1L);
        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));
        when(fileStoragePort.getPresignedUrl(anyString()))
                .thenReturn("https://s3.amazonaws.com/bucket/key?X-Amz-Signature=xyz");

        // when
        String url = fileService.getPresignedUrl(1L, 1L, false);

        // then
        assertThat(url).startsWith("https://");
    }

    @Test
    @DisplayName("비공개 파일은 ADMIN이면 소유자가 아니어도 Presigned URL 발급에 성공한다")
    void getPresignedUrl_success_privateFile_admin() {
        // given
        File file = mock(File.class);
        when(file.getFileUrl()).thenReturn("uploads/organizations/1/uuid.pdf");
        when(file.getFileType()).thenReturn(FileType.BUSINESS_LICENSE);
        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));
        when(fileStoragePort.getPresignedUrl(anyString()))
                .thenReturn("https://s3.amazonaws.com/bucket/key?X-Amz-Signature=xyz");

        // when
        String url = fileService.getPresignedUrl(1L, 99L, true);

        // then
        assertThat(url).startsWith("https://");
    }

    @Test
    @DisplayName("비공개 파일을 타인이 조회하면 FileAccessDeniedException이 발생한다")
    void getPresignedUrl_fail_accessDenied() {
        // given
        File file = mock(File.class);
        when(file.getFileType()).thenReturn(FileType.BUSINESS_LICENSE);
        when(file.getUploaderId()).thenReturn(1L);
        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));

        // when & then
        assertThatThrownBy(() -> fileService.getPresignedUrl(1L, 99L, false))
                .isInstanceOf(FileAccessDeniedException.class);
    }

    @Test
    @DisplayName("존재하지 않는 fileId로 Presigned URL 조회 시 FileNotFoundException이 발생한다")
    void getPresignedUrl_fail_notFound() {
        // given
        when(fileRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> fileService.getPresignedUrl(999L, 1L, false))
                .isInstanceOf(FileNotFoundException.class);
    }

    @Test
    @DisplayName("다른 사용자의 fileKey로 registerFile 시 FileAccessDeniedException이 발생한다")
    void registerFile_fail_invalidFileKey() {
        // given — uploaderId=1 인데 key 경로가 다른 userId(2)로 시작
        FileUploadCommand command = new FileUploadCommand(
                1L,
                "uploads/organizations/2/uuid.pdf",
                "사업자등록증.pdf",
                "application/pdf",
                204800L,
                FileType.BUSINESS_LICENSE
        );

        // when & then
        assertThatThrownBy(() -> fileService.registerFile(command))
                .isInstanceOf(FileAccessDeniedException.class);
    }

    @Test
    @DisplayName("fileType 경로와 불일치하는 fileKey로 registerFile 시 FileAccessDeniedException이 발생한다")
    void registerFile_fail_wrongFileTypePath() {
        // given — BUSINESS_LICENSE 타입인데 key가 pt-thumbnails 경로
        FileUploadCommand command = new FileUploadCommand(
                1L,
                "uploads/pt-thumbnails/1/uuid.pdf",
                "사업자등록증.pdf",
                "application/pdf",
                204800L,
                FileType.BUSINESS_LICENSE
        );

        // when & then
        assertThatThrownBy(() -> fileService.registerFile(command))
                .isInstanceOf(FileAccessDeniedException.class);
    }

    @Test
    @DisplayName("파일 삭제 시 DB hard delete 후 S3 파일을 삭제한다")
    void deleteFile_success() {
        // given
        File file = mock(File.class);
        when(file.getFileUrl()).thenReturn("uploads/organizations/1/uuid.pdf");
        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));

        // when
        fileService.deleteFile(1L);

        // then
        var inOrder = inOrder(fileRepository, fileStoragePort);
        inOrder.verify(fileRepository).deleteById(1L);
        inOrder.verify(fileStoragePort).delete("uploads/organizations/1/uuid.pdf");
    }

    @Test
    @DisplayName("존재하지 않는 fileId 삭제 시 FileNotFoundException이 발생한다")
    void deleteFile_fail_notFound() {
        // given
        when(fileRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> fileService.deleteFile(999L))
                .isInstanceOf(FileNotFoundException.class);

        verify(fileStoragePort, never()).delete(any());
    }

    @Test
    @DisplayName("파일 삭제 성공 시 메트릭이 기록된다")
    void deleteFile_success_recordsMetric() {
        // given
        File file = mock(File.class);
        when(file.getFileUrl()).thenReturn("uploads/organizations/1/uuid.pdf");
        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));

        // when
        fileService.deleteFile(1L);

        // then
        verify(fileMetricsPort).recordFileDeleted();
    }

    @Test
    @DisplayName("파일이 존재하지 않으면 메트릭이 기록되지 않는다")
    void deleteFile_fail_doesNotRecordMetric() {
        // given
        when(fileRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> fileService.deleteFile(999L))
                .isInstanceOf(FileNotFoundException.class);

        verify(fileMetricsPort, never()).recordFileDeleted();
    }

    @Test
    @DisplayName("메트릭 기록 실패가 deleteFile 비즈니스 로직에 영향을 주지 않는다")
    void deleteFile_metricFailure_doesNotAffectBusiness() {
        // given
        File file = mock(File.class);
        when(file.getFileUrl()).thenReturn("uploads/organizations/1/uuid.pdf");
        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));
        doThrow(new RuntimeException("metric error")).when(fileMetricsPort).recordFileDeleted();

        // when & then — 메트릭 예외가 전파되지 않아야 함
        assertThatCode(() -> fileService.deleteFile(1L))
                .doesNotThrowAnyException();

        verify(fileRepository).deleteById(1L);
        verify(fileStoragePort).delete("uploads/organizations/1/uuid.pdf");
    }

}
