package com.ssambbong.gymjjak.pt.ptCourse.application.service;

import com.ssambbong.gymjjak.pt.ptCourse.application.command.CreatePtCourseCommand;
import com.ssambbong.gymjjak.pt.ptCourse.application.service.PtCourseCommandService;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseInvalidException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseStatus;
import com.ssambbong.gymjjak.pt.ptCourse.application.port.TrainerProfileQueryPort;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseRepository;
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
                1L, 1L, 1L, 1L, 1L,
                1L,
                "체계적인 가슴 집중 PT",
                "가슴 근육 발달에 특화된 12주 프로그램",
                50000, 2, false, false, PtCourseStatus.VISIBLE
        );

        when(ptCourseRepository.save(any(PtCourse.class))).thenReturn(savedPtCourse);

        // when
        Long ptCourseId = ptCourseCommandService.createPtCourse(command);

        // then
        assertEquals(1L, ptCourseId);
        verify(ptCourseRepository).save(any(PtCourse.class));
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

        // TrainerInfo Mock 설정
        when(trainerProfileQueryPort.findByUserId(1L))
                .thenReturn(new TrainerProfileQueryPort.TrainerInfo(1L, 1L));

        // when & then
        assertThrows(PtCourseInvalidException.class,
                () -> ptCourseCommandService.createPtCourse(command));

        verify(ptCourseRepository, never()).save(any());
    }

    @Test
    @DisplayName("커리큘럼이 없으면 totalSessionCount=0이 되어 PtCourseInvalidException이 발생해야 한다")
    void createPtCourse_emptyCurriculums_throwsException() {

        // given
        CreatePtCourseCommand command = defaultCommand(
                "PT 강습 제목", "설명", 50000, List.of()
        );

        when(trainerProfileQueryPort.findByUserId(1L))
                .thenReturn(new TrainerProfileQueryPort.TrainerInfo(1L, 1L));

        // when & then
        assertThrows(PtCourseInvalidException.class,
                () -> ptCourseCommandService.createPtCourse(command));

        verify(ptCourseRepository, never()).save(any());
    }
}
