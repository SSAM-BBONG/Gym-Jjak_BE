package com.ssambbong.gymjjak.pt.ptCourse.application.service;

import com.ssambbong.gymjjak.file.application.result.FileRegistrationResult;
import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import com.ssambbong.gymjjak.pt.ptCourse.application.command.CreatePtCourseCommand;
import com.ssambbong.gymjjak.pt.ptCourse.application.command.UpdatePtCourseCommand;
import com.ssambbong.gymjjak.pt.ptCourse.application.command.UploadedFileMetadataCommand;
import com.ssambbong.gymjjak.pt.ptCourse.application.port.OrganizationQueryPort;
import com.ssambbong.gymjjak.pt.ptCourse.application.port.PtReservationCountQueryPort;
import com.ssambbong.gymjjak.pt.ptCourse.application.service.PtCourseCommandService;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.CurriculumUpdateNotAllowedException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseForbiddenException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseInvalidException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseRequestInvalidException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseNotFoundException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseRequestInvalidException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseSchedule;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseStatus;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCurriculum;
import com.ssambbong.gymjjak.pt.ptCourse.application.port.TrainerProfileQueryPort;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseRepository;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseScheduleRepository;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCurriculumRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PtCourseCommandServiceTest {

    @Mock private PtCourseRepository ptCourseRepository;
    @Mock private PtCurriculumRepository ptCurriculumRepository;
    @Mock private PtCourseScheduleRepository ptCourseScheduleRepository;
    @Mock private TrainerProfileQueryPort trainerProfileQueryPort;
    @Mock private OrganizationQueryPort organizationQueryPort;
    @Mock private PtReservationCountQueryPort ptReservationCountQueryPort;
    @Mock private FileUseCase fileUseCase;

    @InjectMocks
    private PtCourseCommandService ptCourseCommandService;

    private CreatePtCourseCommand defaultCommand(String title, String description, int price,
                                                  List<CreatePtCourseCommand.CurriculumData> curriculums) {
        return new CreatePtCourseCommand(
                1L, 1L, 1L,
                title, description, price,
                null,
                curriculums,
                List.of(new CreatePtCourseCommand.ScheduleData("MONDAY", "10:00", "11:00"))
        );
    }

    @Test
    @DisplayName("PT 강습 등록 시 ptCourseId가 반환되어야 한다")
    void createPtCourse_success() {

        // given
        List<CreatePtCourseCommand.CurriculumData> curriculums = List.of(
                new CreatePtCourseCommand.CurriculumData(1, "기초 자세 교정", "체력 측정 및 목표 설정"),
                new CreatePtCourseCommand.CurriculumData(2, "벤치프레스 기초", "올바른 자세 익히기")
        );
        CreatePtCourseCommand command = defaultCommand("체계적인 가슴 집중 PT", "가슴 근육 발달에 특화된 12주 프로그램", 50000, curriculums);

        when(trainerProfileQueryPort.findActiveTrainerProfileIdByUserId(1L)).thenReturn(1L);
        when(organizationQueryPort.findOrganizationIdByTrainerProfileId(1L)).thenReturn(1L);

        PtCourse savedPtCourse = PtCourse.restore(
                1L, 1L, 1L, 1L, 1L, 1L,
                "체계적인 가슴 집중 PT",
                "가슴 근육 발달에 특화된 12주 프로그램",
                50000, 2, PtCourseStatus.VISIBLE, null, null
        );
        when(ptCourseRepository.save(any(PtCourse.class))).thenReturn(savedPtCourse);
        when(ptCurriculumRepository.saveAll(any())).thenReturn(List.of());
        when(ptCourseScheduleRepository.saveAll(any())).thenReturn(List.of());

        // when
        Long ptCourseId = ptCourseCommandService.createPtCourse(command);

        // then
        assertEquals(1L, ptCourseId);
        verify(ptCourseRepository).save(any(PtCourse.class));
        verify(ptCurriculumRepository).saveAll(any());
        verify(ptCourseScheduleRepository).saveAll(any());
    }

    @Test
    @DisplayName("title이 비어있으면 PtCourseInvalidException이 발생한다")
    void createPtCourse_emptyTitle_throwsException() {

        // given
        CreatePtCourseCommand command = defaultCommand(
                "", "설명", 50000,
                List.of(new CreatePtCourseCommand.CurriculumData(1, "회차 제목", "회차 설명"))
        );

        when(trainerProfileQueryPort.findActiveTrainerProfileIdByUserId(1L)).thenReturn(1L);
        when(organizationQueryPort.findOrganizationIdByTrainerProfileId(1L)).thenReturn(1L);

        // when & then
        assertThrows(PtCourseInvalidException.class,
                () -> ptCourseCommandService.createPtCourse(command));

        verify(ptCourseRepository, never()).save(any());
    }

    @Test
    @DisplayName("price가 음수이면 PtCourseInvalidException이 발생한다")
    void createPtCourse_negativePrice_throwsException() {

        // given
        CreatePtCourseCommand command = defaultCommand(
                "PT 강습 제목", "설명", -1,
                List.of(new CreatePtCourseCommand.CurriculumData(1, "회차 제목", "회차 설명"))
        );

        when(trainerProfileQueryPort.findActiveTrainerProfileIdByUserId(1L)).thenReturn(1L);
        when(organizationQueryPort.findOrganizationIdByTrainerProfileId(1L)).thenReturn(1L);

        // when & then
        assertThrows(PtCourseInvalidException.class,
                () -> ptCourseCommandService.createPtCourse(command));

        verify(ptCourseRepository, never()).save(any());
    }

    @Test
    @DisplayName("커리큘럼이 없으면 PtCourseRequestInvalidException이 발생해야 한다")
    void createPtCourse_emptyCurriculums_throwsException() {

        // given
        CreatePtCourseCommand command = defaultCommand(
                "PT 강습 제목", "설명", 50000, List.of()
        );

        // when & then
        assertThrows(PtCourseRequestInvalidException.class,
                () -> ptCourseCommandService.createPtCourse(command));

        verify(ptCourseRepository, never()).save(any());
        verify(ptCurriculumRepository, never()).saveAll(any());
        verify(ptCourseScheduleRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("커리큘럼 내 sessionNo가 중복되면 PtCourseRequestInvalidException이 발생해야 한다")
    void createPtCourse_duplicateSessionNo_throwsException() {

        // given
        List<CreatePtCourseCommand.CurriculumData> curriculums = List.of(
                new CreatePtCourseCommand.CurriculumData(1, "회차1 제목", "회차1 설명"),
                new CreatePtCourseCommand.CurriculumData(1, "회차2 제목", "회차2 설명") // 중복
        );
        CreatePtCourseCommand command = defaultCommand("PT 강습 제목", "설명", 50000, curriculums);

        // when & then
        assertThrows(PtCourseRequestInvalidException.class,
                () -> ptCourseCommandService.createPtCourse(command));

        verify(ptCourseRepository, never()).save(any());
        verify(ptCurriculumRepository, never()).saveAll(any());
        verify(ptCourseScheduleRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("스케줄이 없으면 PtCourseRequestInvalidException이 발생해야 한다")
    void createPtCourse_emptySchedules_throwsException() {

        // given
        CreatePtCourseCommand command = new CreatePtCourseCommand(
                1L, 1L, 1L, "PT 강습 제목", "설명", 50000, null,
                List.of(new CreatePtCourseCommand.CurriculumData(1, "회차 제목", "회차 설명")),
                List.of() // 빈 스케줄
        );

        // when & then
        assertThrows(PtCourseRequestInvalidException.class,
                () -> ptCourseCommandService.createPtCourse(command));

        verify(ptCourseRepository, never()).save(any());
        verify(ptCurriculumRepository, never()).saveAll(any());
        verify(ptCourseScheduleRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("스케줄 내 (요일, 시작/종료 시간) 조합이 중복되면 PtCourseRequestInvalidException이 발생해야 한다")
    void createPtCourse_duplicateSchedule_throwsException() {

        // given
        List<CreatePtCourseCommand.CurriculumData> curriculums = List.of(
                new CreatePtCourseCommand.CurriculumData(1, "회차 제목", "회차 설명")
        );
        CreatePtCourseCommand command = new CreatePtCourseCommand(
                1L, 1L, 1L, "PT 강습 제목", "설명", 50000, null,
                curriculums,
                List.of(
                        new CreatePtCourseCommand.ScheduleData("MONDAY", "10:00", "11:00"),
                        new CreatePtCourseCommand.ScheduleData("MONDAY", "10:00", "11:00") // 중복
                )
        );

        // when & then
        assertThrows(PtCourseRequestInvalidException.class,
                () -> ptCourseCommandService.createPtCourse(command));

        verify(ptCourseRepository, never()).save(any());
        verify(ptCurriculumRepository, never()).saveAll(any());
        verify(ptCourseScheduleRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("커리큘럼이 null이면 PtCourseRequestInvalidException이 발생해야 한다")
    void createPtCourse_nullCurriculums_throwsException() {

        // given
        CreatePtCourseCommand command = new CreatePtCourseCommand(
                1L, 1L, 1L, "PT 강습 제목", "설명", 50000, null,
                null,
                List.of(new CreatePtCourseCommand.ScheduleData("MONDAY", "10:00", "11:00"))
        );

        // when & then
        assertThrows(PtCourseRequestInvalidException.class,
                () -> ptCourseCommandService.createPtCourse(command));

        verify(ptCourseRepository, never()).save(any());
        verify(ptCurriculumRepository, never()).saveAll(any());
        verify(ptCourseScheduleRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("thumbnailFile이 있으면 파일 등록 후 ptCourseId가 반환된다")
    void createPtCourse_withThumbnail_success() {

        // given
        UploadedFileMetadataCommand thumbnailFile =
                new UploadedFileMetadataCommand("file-key", "thumb.jpg", "image/jpeg", 1024L);
        CreatePtCourseCommand command = new CreatePtCourseCommand(
                1L, 1L, 1L, "제목", "설명", 50000, thumbnailFile,
                List.of(new CreatePtCourseCommand.CurriculumData(1, "회차 제목", "회차 설명")),
                List.of(new CreatePtCourseCommand.ScheduleData("MONDAY", "10:00", "11:00"))
        );

        when(trainerProfileQueryPort.findActiveTrainerProfileIdByUserId(1L)).thenReturn(1L);
        when(organizationQueryPort.findOrganizationIdByTrainerProfileId(1L)).thenReturn(1L);
        when(fileUseCase.registerFiles(any()))
                .thenReturn(List.of(new FileRegistrationResult(99L, FileType.PT_THUMBNAIL)));

        PtCourse savedPtCourse = PtCourse.restore(
                1L, 1L, 1L, 1L, 1L, 99L, "제목", "설명", 50000, 1, PtCourseStatus.VISIBLE, null, null
        );
        when(ptCourseRepository.save(any(PtCourse.class))).thenReturn(savedPtCourse);
        when(ptCurriculumRepository.saveAll(any())).thenReturn(List.of());
        when(ptCourseScheduleRepository.saveAll(any())).thenReturn(List.of());

        // when
        Long ptCourseId = ptCourseCommandService.createPtCourse(command);

        // then
        assertEquals(1L, ptCourseId);
        verify(fileUseCase).registerFiles(any());
    }

    @Test
    @DisplayName("파일 등록 결과가 비어있으면 IllegalStateException이 발생한다")
    void createPtCourse_thumbnailRegisterFailed_throwsException() {

        // given
        UploadedFileMetadataCommand thumbnailFile =
                new UploadedFileMetadataCommand("file-key", "thumb.jpg", "image/jpeg", 1024L);
        CreatePtCourseCommand command = new CreatePtCourseCommand(
                1L, 1L, 1L, "제목", "설명", 50000, thumbnailFile,
                List.of(new CreatePtCourseCommand.CurriculumData(1, "회차 제목", "회차 설명")),
                List.of(new CreatePtCourseCommand.ScheduleData("MONDAY", "10:00", "11:00"))
        );

        when(trainerProfileQueryPort.findActiveTrainerProfileIdByUserId(1L)).thenReturn(1L);
        when(organizationQueryPort.findOrganizationIdByTrainerProfileId(1L)).thenReturn(1L);
        when(fileUseCase.registerFiles(any())).thenReturn(List.of());

        // when & then
        assertThrows(IllegalStateException.class,
                () -> ptCourseCommandService.createPtCourse(command));

        verify(ptCourseRepository, never()).save(any());
    }

    @Test
    @DisplayName("스케줄이 null이면 PtCourseRequestInvalidException이 발생해야 한다")
    void createPtCourse_nullSchedules_throwsException() {

        // given
        CreatePtCourseCommand command = new CreatePtCourseCommand(
                1L, 1L, 1L, "PT 강습 제목", "설명", 50000, null,
                List.of(new CreatePtCourseCommand.CurriculumData(1, "회차 제목", "회차 설명")),
                null
        );

        // when & then
        assertThrows(PtCourseRequestInvalidException.class,
                () -> ptCourseCommandService.createPtCourse(command));

        verify(ptCourseRepository, never()).save(any());
        verify(ptCurriculumRepository, never()).saveAll(any());
        verify(ptCourseScheduleRepository, never()).saveAll(any());
    }

    // ===== updatePtCourse =====

    private UpdatePtCourseCommand defaultUpdateCommand() {
        return new UpdatePtCourseCommand(
                1L, 1L,
                "수정된 PT 강습 제목", "수정된 설명", 2L, null, 60000,
                null,   // thumbnailFile — null이면 기존 유지
                null,   // curriculums — null이면 변경 없음
                null    // schedules — null이면 변경 없음
        );
    }

    private PtCourse existingPtCourse() {
        return PtCourse.restore(1L, 1L, 1L, 1L, 1L, null, "기존 제목", "기존 설명", 50000, 2, PtCourseStatus.VISIBLE, null, null);
    }

    @Test
    @DisplayName("PT 강습 수정 시 ptCourseId가 반환되어야 한다")
    void updatePtCourse_success() {

        // given
        UpdatePtCourseCommand command = defaultUpdateCommand();

        when(ptCourseRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(existingPtCourse()));
        when(trainerProfileQueryPort.findActiveTrainerProfileIdByUserId(1L)).thenReturn(1L);

        // when
        Long ptCourseId = ptCourseCommandService.updatePtCourse(command);

        // then
        assertEquals(1L, ptCourseId);
        verify(ptCourseRepository).update(any(PtCourse.class));
        verify(ptCurriculumRepository, never()).findAllByPtCourseId(any());
    }

    @Test
    @DisplayName("PT 강습이 존재하지 않으면 PtCourseNotFoundException이 발생한다")
    void updatePtCourse_notFound_throwsException() {

        // given
        when(ptCourseRepository.findByIdForUpdate(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(PtCourseNotFoundException.class,
                () -> ptCourseCommandService.updatePtCourse(defaultUpdateCommand()));

        verify(ptCourseRepository, never()).update(any());
    }

    @Test
    @DisplayName("본인 강습이 아니면 PtCourseForbiddenException이 발생한다")
    void updatePtCourse_forbidden_throwsException() {

        // given — trainerProfileId=2인 강습을 userId=1(trainerProfileId=99)이 수정 시도
        PtCourse other = PtCourse.restore(1L, 1L, 2L, 2L, null, null, "다른 트레이너 강습", "설명", 30000, 1, PtCourseStatus.VISIBLE, null, null);

        when(ptCourseRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(other));
        when(trainerProfileQueryPort.findActiveTrainerProfileIdByUserId(1L)).thenReturn(99L); // 본인 trainerProfileId=99

        // when & then
        assertThrows(PtCourseForbiddenException.class,
                () -> ptCourseCommandService.updatePtCourse(defaultUpdateCommand()));

        verify(ptCourseRepository, never()).update(any());
    }

    @Test
    @DisplayName("활성 수강생이 있을 때 커리큘럼을 수정하면 CurriculumUpdateNotAllowedException이 발생한다")
    void updatePtCourse_curriculumUpdateNotAllowed_throwsException() {

        // given — 커리큘럼 수정 요청 포함
        UpdatePtCourseCommand command = new UpdatePtCourseCommand(
                1L, 1L,
                "수정된 PT 강습 제목", "수정된 설명", 2L, null, 60000, null,
                List.of(new UpdatePtCourseCommand.CurriculumData(1L, 1, "수정된 회차 제목", "수정된 회차 설명")),
                null
        );

        when(ptCourseRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(existingPtCourse()));
        when(trainerProfileQueryPort.findActiveTrainerProfileIdByUserId(1L)).thenReturn(1L);
        when(ptReservationCountQueryPort.countActiveByPtCourseIds(List.of(1L)))
                .thenReturn(Map.of(1L, 3)); // 활성 수강생 3명

        // when & then
        assertThrows(CurriculumUpdateNotAllowedException.class,
                () -> ptCourseCommandService.updatePtCourse(command));

        verify(ptCourseRepository, never()).update(any());
    }

    @Test
    @DisplayName("커리큘럼 수정 시 sessionNo가 중복되면 PtCourseRequestInvalidException이 발생한다")
    void updatePtCourse_duplicateSessionNo_throwsException() {

        // given — 활성 수강생 0명이지만 sessionNo 중복
        UpdatePtCourseCommand command = new UpdatePtCourseCommand(
                1L, 1L,
                "수정된 PT 강습 제목", "수정된 설명", 2L, null, 60000, null,
                List.of(
                        new UpdatePtCourseCommand.CurriculumData(null, 1, "회차1", "설명1"),
                        new UpdatePtCourseCommand.CurriculumData(null, 1, "회차2", "설명2") // 중복 sessionNo
                ),
                null
        );

        when(ptCourseRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(existingPtCourse()));
        when(trainerProfileQueryPort.findActiveTrainerProfileIdByUserId(1L)).thenReturn(1L);
        when(ptReservationCountQueryPort.countActiveByPtCourseIds(List.of(1L)))
                .thenReturn(Map.of(1L, 0));

        // when & then
        assertThrows(PtCourseRequestInvalidException.class,
                () -> ptCourseCommandService.updatePtCourse(command));
    }

    @Test
    @DisplayName("thumbnailFile이 있으면 파일 등록 후 수정이 완료된다")
    void updatePtCourse_withThumbnail_success() {

        // given
        UploadedFileMetadataCommand thumbnailFile =
                new UploadedFileMetadataCommand("file-key", "thumb.jpg", "image/jpeg", 2048L);
        UpdatePtCourseCommand command = new UpdatePtCourseCommand(
                1L, 1L,
                "수정된 제목", "수정된 설명", 2L, null, 60000,
                thumbnailFile, null, null
        );

        when(ptCourseRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(existingPtCourse()));
        when(trainerProfileQueryPort.findActiveTrainerProfileIdByUserId(1L)).thenReturn(1L);
        when(fileUseCase.registerFiles(any()))
                .thenReturn(List.of(new FileRegistrationResult(99L, FileType.PT_THUMBNAIL)));

        // when
        Long ptCourseId = ptCourseCommandService.updatePtCourse(command);

        // then
        assertEquals(1L, ptCourseId);
        verify(fileUseCase).registerFiles(any());
        verify(ptCourseRepository).update(any(PtCourse.class));
    }
}
