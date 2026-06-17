package com.ssambbong.gymjjak.pt.ptCourse.application.service;

import com.ssambbong.gymjjak.pt.ptCourse.application.command.CreatePtCourseCommand;
import com.ssambbong.gymjjak.pt.ptCourse.application.service.PtCourseCommandService;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseInvalidException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PtCourseCommandServiceTest {

    @Mock private PtCourseRepository ptCourseRepository;
    @Mock private PtCurriculumRepository ptCurriculumRepository;
    @Mock private PtCourseScheduleRepository ptCourseScheduleRepository;
    @Mock private TrainerProfileQueryPort trainerProfileQueryPort;

    @InjectMocks
    private PtCourseCommandService ptCourseCommandService;

    private CreatePtCourseCommand defaultCommand(String title, String description, int price,
                                                  List<CreatePtCourseCommand.CurriculumData> curriculums) {
        return new CreatePtCourseCommand(
                1L, 1L, 1L,
                title, description, price,
                1L,
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

        when(trainerProfileQueryPort.findByUserId(1L))
                .thenReturn(new TrainerProfileQueryPort.TrainerInfo(1L, 1L));

        PtCourse savedPtCourse = PtCourse.restore(
                1L, 1L, 1L, 1L, 1L, 1L,
                "체계적인 가슴 집중 PT",
                "가슴 근육 발달에 특화된 12주 프로그램",
                50000, 2, PtCourseStatus.VISIBLE
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

        when(trainerProfileQueryPort.findByUserId(1L))
                .thenReturn(new TrainerProfileQueryPort.TrainerInfo(1L, 1L));

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

        when(trainerProfileQueryPort.findByUserId(1L))
                .thenReturn(new TrainerProfileQueryPort.TrainerInfo(1L, 1L));

        // when & then
        assertThrows(PtCourseInvalidException.class,
                () -> ptCourseCommandService.createPtCourse(command));

        verify(ptCourseRepository, never()).save(any());
    }

    @Test
    @DisplayName("커리큘럼이 없으면 PtCourseInvalidException이 발생해야 한다")
    void createPtCourse_emptyCurriculums_throwsException() {

        // given
        CreatePtCourseCommand command = defaultCommand(
                "PT 강습 제목", "설명", 50000, List.of()
        );

        // when & then
        assertThrows(PtCourseInvalidException.class,
                () -> ptCourseCommandService.createPtCourse(command));

        verify(ptCourseRepository, never()).save(any());
        verify(ptCurriculumRepository, never()).saveAll(any());
        verify(ptCourseScheduleRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("커리큘럼 내 sessionNo가 중복되면 PtCourseInvalidException이 발생해야 한다")
    void createPtCourse_duplicateSessionNo_throwsException() {

        // given
        List<CreatePtCourseCommand.CurriculumData> curriculums = List.of(
                new CreatePtCourseCommand.CurriculumData(1, "회차1 제목", "회차1 설명"),
                new CreatePtCourseCommand.CurriculumData(1, "회차2 제목", "회차2 설명") // 중복
        );
        CreatePtCourseCommand command = defaultCommand("PT 강습 제목", "설명", 50000, curriculums);

        // when & then
        assertThrows(PtCourseInvalidException.class,
                () -> ptCourseCommandService.createPtCourse(command));

        verify(ptCourseRepository, never()).save(any());
        verify(ptCurriculumRepository, never()).saveAll(any());
        verify(ptCourseScheduleRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("스케줄이 없으면 PtCourseInvalidException이 발생해야 한다")
    void createPtCourse_emptySchedules_throwsException() {

        // given
        CreatePtCourseCommand command = new CreatePtCourseCommand(
                1L, 1L, 1L, "PT 강습 제목", "설명", 50000, 1L,
                List.of(new CreatePtCourseCommand.CurriculumData(1, "회차 제목", "회차 설명")),
                List.of() // 빈 스케줄
        );

        // when & then
        assertThrows(PtCourseInvalidException.class,
                () -> ptCourseCommandService.createPtCourse(command));

        verify(ptCourseRepository, never()).save(any());
        verify(ptCurriculumRepository, never()).saveAll(any());
        verify(ptCourseScheduleRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("스케줄 내 (요일, 시작/종료 시간) 조합이 중복되면 PtCourseInvalidException이 발생해야 한다")
    void createPtCourse_duplicateSchedule_throwsException() {

        // given
        List<CreatePtCourseCommand.CurriculumData> curriculums = List.of(
                new CreatePtCourseCommand.CurriculumData(1, "회차 제목", "회차 설명")
        );
        CreatePtCourseCommand command = new CreatePtCourseCommand(
                1L, 1L, 1L, "PT 강습 제목", "설명", 50000, 1L,
                curriculums,
                List.of(
                        new CreatePtCourseCommand.ScheduleData("MONDAY", "10:00", "11:00"),
                        new CreatePtCourseCommand.ScheduleData("MONDAY", "10:00", "11:00") // 중복
                )
        );

        // when & then
        assertThrows(PtCourseInvalidException.class,
                () -> ptCourseCommandService.createPtCourse(command));

        verify(ptCourseRepository, never()).save(any());
        verify(ptCurriculumRepository, never()).saveAll(any());
        verify(ptCourseScheduleRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("커리큘럼이 null이면 PtCourseInvalidException이 발생해야 한다")
    void createPtCourse_nullCurriculums_throwsException() {

        // given
        CreatePtCourseCommand command = new CreatePtCourseCommand(
                1L, 1L, 1L, "PT 강습 제목", "설명", 50000, 1L,
                null,
                List.of(new CreatePtCourseCommand.ScheduleData("MONDAY", "10:00", "11:00"))
        );

        // when & then
        assertThrows(PtCourseInvalidException.class,
                () -> ptCourseCommandService.createPtCourse(command));

        verify(ptCourseRepository, never()).save(any());
        verify(ptCurriculumRepository, never()).saveAll(any());
        verify(ptCourseScheduleRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("스케줄이 null이면 PtCourseInvalidException이 발생해야 한다")
    void createPtCourse_nullSchedules_throwsException() {

        // given
        CreatePtCourseCommand command = new CreatePtCourseCommand(
                1L, 1L, 1L, "PT 강습 제목", "설명", 50000, 1L,
                List.of(new CreatePtCourseCommand.CurriculumData(1, "회차 제목", "회차 설명")),
                null
        );

        // when & then
        assertThrows(PtCourseInvalidException.class,
                () -> ptCourseCommandService.createPtCourse(command));

        verify(ptCourseRepository, never()).save(any());
        verify(ptCurriculumRepository, never()).saveAll(any());
        verify(ptCourseScheduleRepository, never()).saveAll(any());
    }
}
