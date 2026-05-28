package com.ssambbong.gymjjak.organization;

import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import com.ssambbong.gymjjak.organization.application.command.OrganizationApplicationCreateCommand;
import com.ssambbong.gymjjak.organization.application.service.OrganizationApplicationCommandService;
import com.ssambbong.gymjjak.organization.domain.repository.OrganizationApplicationRepository;
import com.ssambbong.gymjjak.organization.exception.DuplicateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class OrganizationApplicationCommandServiceTest {

    private OrganizationApplicationRepository organizationApplicationRepository;
    private FileUseCase fileUseCase;
    private OrganizationApplicationCommandService organizationApplicationCommandService;

    private MultipartFile businessLicenseFile;
    private OrganizationApplicationCreateCommand command;

    @BeforeEach
    void setUp() {
        organizationApplicationRepository = mock(OrganizationApplicationRepository.class);
        fileUseCase = mock(FileUseCase.class);
        organizationApplicationCommandService = new OrganizationApplicationCommandService(
                organizationApplicationRepository,
                fileUseCase
        );

        businessLicenseFile = mock(MultipartFile.class);
        command = new OrganizationApplicationCreateCommand(
                1L,
                "gymjjak123",
                "1234567890",
                "짐짝헬스장",
                "홍길동",
                "010-1234-5678",
                LocalDate.of(2024, 1, 1),
                "서울시 강남구 테헤란로 1",
                null,
                null,
                new BigDecimal("37.4979"),
                new BigDecimal("127.0276"),
                null,
                null,
                null,
                "02-1234-5678"
        );
    }

    @Test
    @DisplayName("조직 신청에 성공한다")
    void createOrganizationApplication_success() {
        // given
        Long fileId = 1L;
        Long applicationId = 1L;

        when(organizationApplicationRepository.existsByBusinessRegistrationNumberAndStatus(command.businessRegistrationNumber()))
                .thenReturn(false);
        when(fileUseCase.uploadFile(businessLicenseFile, command.applicantUserId(), FileType.BUSINESS_LICENSE))
                .thenReturn(fileId);
        when(organizationApplicationRepository.save(any()))
                .thenReturn(applicationId);

        // when
        Long result = organizationApplicationCommandService.createOrganizationApplication(businessLicenseFile, command);

        // then
        assertThat(result).isEqualTo(applicationId);

        verify(organizationApplicationRepository).existsByBusinessRegistrationNumberAndStatus(command.businessRegistrationNumber());
        verify(fileUseCase).uploadFile(businessLicenseFile, command.applicantUserId(), FileType.BUSINESS_LICENSE);
        verify(organizationApplicationRepository).save(any());
    }

    @Test
    @DisplayName("이미 승인된 사업자등록번호로 신청하면 실패한다")
    void createOrganizationApplication_fail_duplicateBusinessRegistrationNumber() {
        // given
        when(organizationApplicationRepository.existsByBusinessRegistrationNumberAndStatus(command.businessRegistrationNumber()))
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> organizationApplicationCommandService.createOrganizationApplication(businessLicenseFile, command))
                .isInstanceOf(DuplicateException.class);

        verify(fileUseCase, never()).uploadFile(any(), any(), any());
        verify(organizationApplicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("DB 저장 실패 시 S3 파일을 롤백한다")
    void createOrganizationApplication_fail_dbSaveFailure_rollbackS3() {
        // given
        Long fileId = 1L;

        when(organizationApplicationRepository.existsByBusinessRegistrationNumberAndStatus(command.businessRegistrationNumber()))
                .thenReturn(false);
        when(fileUseCase.uploadFile(businessLicenseFile, command.applicantUserId(), FileType.BUSINESS_LICENSE))
                .thenReturn(fileId);
        when(organizationApplicationRepository.save(any()))
                .thenThrow(new DataIntegrityViolationException("DB 저장 실패"));

        // when & then
        assertThatThrownBy(() -> organizationApplicationCommandService.createOrganizationApplication(businessLicenseFile, command))
                .isInstanceOf(DataAccessException.class);

        verify(fileUseCase).deleteFile(fileId);
    }
}
