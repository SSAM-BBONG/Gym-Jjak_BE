package com.ssambbong.gymjjak.organization;

import com.ssambbong.gymjjak.file.application.result.FileRegistrationResult;
import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import com.ssambbong.gymjjak.organization.organizationApplication.application.command.OrganizationApplicationCreateCommand;
import com.ssambbong.gymjjak.organization.organizationApplication.application.command.UploadedFileMetadataCommand;
import com.ssambbong.gymjjak.organization.organizationApplication.application.port.OrgApplicationMetricsPort;
import com.ssambbong.gymjjak.organization.organizationApplication.application.port.UserCreationPort;
import com.ssambbong.gymjjak.organization.organizationApplication.application.port.UserLoginIdValidationPort;
import com.ssambbong.gymjjak.organization.organizationApplication.application.service.OrganizationApplicationCommandService;
import com.ssambbong.gymjjak.organization.organizationApplication.domain.repository.OrganizationApplicationRepository;
import com.ssambbong.gymjjak.organization.organization.application.port.OrganizationMetricsPort;
import com.ssambbong.gymjjak.organization.organization.domain.repository.OrganizationRepository;
import com.ssambbong.gymjjak.organization.organizationApplication.exception.DuplicateBusinessRegistrationNumberException;
import com.ssambbong.gymjjak.organization.organizationApplication.exception.DuplicateRequestedLoginIdException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationApplicationCommandServiceTest {

    @Mock private OrganizationApplicationRepository organizationApplicationRepository;
    @Mock private OrganizationRepository organizationRepository;
    @Mock private FileUseCase fileUseCase;
    @Mock private UserCreationPort userCreationPort;
    @Mock private UserLoginIdValidationPort userLoginIdValidationPort;
    @Mock private OrgApplicationMetricsPort orgApplicationMetricsPort;
    @Mock private OrganizationMetricsPort organizationMetricsPort;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private OrganizationApplicationCommandService organizationApplicationCommandService;

    private OrganizationApplicationCreateCommand command(Long fileId) {
        return new OrganizationApplicationCreateCommand(
                1L,
                fileId == null ? null : new UploadedFileMetadataCommand("file-key-" + fileId, "사업자등록증.pdf", "application/pdf", 1024L),
                "gymjjak123",
                "1234567890",
                "짐짝헬스장",
                "홍길동",
                "010-1234-5678",
                LocalDate.of(2024, 1, 1),
                "서울시 강남구 테헤란로 1",
                null, null,
                new BigDecimal("37.4979"),
                new BigDecimal("127.0276"),
                null, null, null,
                "02-1234-5678"
        );
    }

    @Test
    @DisplayName("조직 신청에 성공한다")
    void createOrganizationApplication_success() {
        // given
        OrganizationApplicationCreateCommand command = command(1L);

        when(organizationApplicationRepository.existsByBusinessRegistrationNumberAndStatus(command.businessRegistrationNumber()))
                .thenReturn(false);
        when(organizationApplicationRepository.existsByRequestedLoginId(command.requestedLoginId()))
                .thenReturn(false);
        when(fileUseCase.registerFiles(any()))
                .thenReturn(List.of(new FileRegistrationResult(10L, FileType.BUSINESS_LICENSE)));
        when(organizationApplicationRepository.save(any()))
                .thenReturn(1L);

        // when
        Long result = organizationApplicationCommandService.createOrganizationApplication(command);

        // then
        assertThat(result).isEqualTo(1L);
        verify(organizationApplicationRepository).save(any());
    }

    @Test
    @DisplayName("이미 등록된 사업자등록번호로 신청하면 실패한다")
    void createOrganizationApplication_fail_duplicateBusinessRegistrationNumber() {
        // given
        OrganizationApplicationCreateCommand command = command(1L);

        when(organizationApplicationRepository.existsByBusinessRegistrationNumberAndStatus(command.businessRegistrationNumber()))
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> organizationApplicationCommandService.createOrganizationApplication(command))
                .isInstanceOf(DuplicateBusinessRegistrationNumberException.class);

        verify(organizationApplicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("이미 사용 중인 로그인 ID로 신청하면 실패한다")
    void createOrganizationApplication_fail_duplicateRequestedLoginId() {
        // given
        OrganizationApplicationCreateCommand command = command(1L);

        when(organizationApplicationRepository.existsByBusinessRegistrationNumberAndStatus(command.businessRegistrationNumber()))
                .thenReturn(false);
        when(organizationApplicationRepository.existsByRequestedLoginId(command.requestedLoginId()))
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> organizationApplicationCommandService.createOrganizationApplication(command))
                .isInstanceOf(DuplicateRequestedLoginIdException.class);

        verify(organizationApplicationRepository, never()).save(any());
    }
}
