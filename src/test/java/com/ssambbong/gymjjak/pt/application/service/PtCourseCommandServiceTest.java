package com.ssambbong.gymjjak.pt.application.service;

import com.ssambbong.gymjjak.pt.application.command.CreatePtCourseCommand;
import com.ssambbong.gymjjak.pt.domain.exception.PtCourseInvalidException;
import com.ssambbong.gymjjak.pt.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.domain.model.PtCourseStatus;
import com.ssambbong.gymjjak.pt.application.port.TrainerProfileQueryPort;
import com.ssambbong.gymjjak.pt.domain.repository.PtCourseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PtCourseCommandServiceTest {

    @Mock private PtCourseRepository ptCourseRepository;
    @Mock private TrainerProfileQueryPort trainerProfileQueryPort;

    @InjectMocks
    private PtCourseCommandService ptCourseCommandService;

    private PtCourse savedCourse(Long thumbnailFileId) {
        return PtCourse.restore(
                1L, 1L, 1L, 1L, 1L, thumbnailFileId,
                "체계적인 가슴 집중 PT",
                "가슴 근육 발달에 특화된 12주 프로그램",
                50000, 12, false, false, PtCourseStatus.VISIBLE
        );
    }

    @Test
    @DisplayName("썸네일 없이 PT 강습 등록에 성공한다")
    void createPtCourse_success_withoutThumbnail() {
        // given
        CreatePtCourseCommand command = new CreatePtCourseCommand(
                1L, 1L, 1L,
                "체계적인 가슴 집중 PT",
                "가슴 근육 발달에 특화된 12주 프로그램",
                50000, 12,
                null
        );

        when(trainerProfileQueryPort.findByUserId(1L))
                .thenReturn(new TrainerProfileQueryPort.TrainerInfo(1L, 1L));
        when(ptCourseRepository.save(any())).thenReturn(savedCourse(null));

        // when
        Long ptCourseId = ptCourseCommandService.createPtCourse(command);

        // then
        assertEquals(1L, ptCourseId);
        verify(ptCourseRepository).save(any());
    }

    @Test
    @DisplayName("썸네일 fileId가 있을 때 PT 강습 등록에 성공한다")
    void createPtCourse_success_withThumbnailFileId() {
        // given
        CreatePtCourseCommand command = new CreatePtCourseCommand(
                1L, 1L, 1L,
                "체계적인 가슴 집중 PT",
                "가슴 근육 발달에 특화된 12주 프로그램",
                50000, 12,
                99L
        );

        when(trainerProfileQueryPort.findByUserId(1L))
                .thenReturn(new TrainerProfileQueryPort.TrainerInfo(1L, 1L));
        when(ptCourseRepository.save(any())).thenReturn(savedCourse(99L));

        // when
        Long ptCourseId = ptCourseCommandService.createPtCourse(command);

        // then
        assertEquals(1L, ptCourseId);
        verify(ptCourseRepository).save(any());
    }

    @Test
    @DisplayName("title이 비어있으면 PtCourseInvalidException이 발생한다")
    void createPtCourse_emptyTitle_throwsException() {
        // given
        CreatePtCourseCommand command = new CreatePtCourseCommand(
                1L, 1L, 1L,
                "", "설명", 5000, 12, null
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
        CreatePtCourseCommand command = new CreatePtCourseCommand(
                1L, 1L, 1L,
                "PT 강습 제목", "설명", -1, 12, null
        );

        when(trainerProfileQueryPort.findByUserId(1L))
                .thenReturn(new TrainerProfileQueryPort.TrainerInfo(1L, 1L));

        // when & then
        assertThrows(PtCourseInvalidException.class,
                () -> ptCourseCommandService.createPtCourse(command));

        verify(ptCourseRepository, never()).save(any());
    }

    @Test
    @DisplayName("totalSessionCount가 1 미만이면 PtCourseInvalidException이 발생한다")
    void createPtCourse_zeroTotalSessionCount_throwsException() {
        // given
        CreatePtCourseCommand command = new CreatePtCourseCommand(
                1L, 1L, 1L,
                "PT 강습 제목", "설명", 50000, 0, null
        );

        when(trainerProfileQueryPort.findByUserId(1L))
                .thenReturn(new TrainerProfileQueryPort.TrainerInfo(1L, 1L));

        // when & then
        assertThrows(PtCourseInvalidException.class,
                () -> ptCourseCommandService.createPtCourse(command));

        verify(ptCourseRepository, never()).save(any());
    }
}
