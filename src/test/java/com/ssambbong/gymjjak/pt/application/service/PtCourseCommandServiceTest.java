package com.ssambbong.gymjjak.pt.application.service;

import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import com.ssambbong.gymjjak.pt.application.command.CreatePtCourseCommand;
import com.ssambbong.gymjjak.pt.domain.exception.PtCourseInvalidException;
import com.ssambbong.gymjjak.pt.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.domain.model.PtCourseStatus;
import com.ssambbong.gymjjak.pt.domain.repository.PtCourseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PtCourseCommandServiceTest {

    @Mock
    private PtCourseRepository ptCourseRepository;

    @Mock
    private FileUseCase fileUseCase;

    @InjectMocks
    private PtCourseCommandService ptCourseCommandService;

    @Test
    @DisplayName("썸네일 없이 PT 강습 등록 시 ptCourseId가 반환되어야 한다")
    void createPtCourse_success_withoutThumbnail() {

        // given
        CreatePtCourseCommand command = new CreatePtCourseCommand(
                1L,                                     // userId
                1L,                                     // organizationId
                1L,                                     // trainerProfileId
                1L,                                     // categoryId
                1L,                                     // tagId
                "체계적인 가슴 집중 PT",                 // title
                "가슴 근육 발달에 특화된 12주 프로그램",  // description
                50000,                                  // price
                12                                      // totalSessionCount
        );

        PtCourse savedPtCourse = PtCourse.restore(
                1L, 1L, 1L, 1L, 1L, null,
                "체계적인 가슴 집중 PT",
                "가슴 근육 발달에 특화된 12주 프로그램",
                50000, 12, false, false, PtCourseStatus.VISIBLE
        );

        when(ptCourseRepository.save(any(PtCourse.class))).thenReturn(savedPtCourse);

        // when
        Long ptCourseId = ptCourseCommandService.createPtCourse(null, command);

        // then
        assertEquals(1L, ptCourseId);
        verify(ptCourseRepository).save(any(PtCourse.class));
        verify(fileUseCase, never()).uploadFile(any(), any(), any()); // 파일 업로드 호출 안 됨
    }

    @Test
    @DisplayName("썸네일 있을 때 PT 강습 등록 시 파일 업로드 후 ptCourseId가 반환되어야 한다")
    void createPtCourse_success_withThumbnail() {

        // given
        MultipartFile thumbnail = mock(MultipartFile.class);
        when(thumbnail.isEmpty()).thenReturn(false);

        CreatePtCourseCommand command = new CreatePtCourseCommand(
                1L, 1L, 1L, 1L, 1L,
                "체계적인 가슴 집중 PT",
                "가슴 근육 발달에 특화된 12주 프로그램",
                50000, 12
        );

        when(fileUseCase.uploadFile(thumbnail, 1L, FileType.COURSE_THUMBNAIL)).thenReturn(99L);

        PtCourse savedPtCourse = PtCourse.restore(
                1L, 1L, 1L, 1L, 1L, 99L,
                "체계적인 가슴 집중 PT",
                "가슴 근육 발달에 특화된 12주 프로그램",
                50000, 12, false, false, PtCourseStatus.VISIBLE
        );

        when(ptCourseRepository.save(any(PtCourse.class))).thenReturn(savedPtCourse);

        // when
        Long ptCourseId = ptCourseCommandService.createPtCourse(thumbnail, command);

        // then
        assertEquals(1L, ptCourseId);
        verify(fileUseCase).uploadFile(thumbnail, 1L, FileType.COURSE_THUMBNAIL); // 파일 업로드 호출됨
        verify(ptCourseRepository).save(any(PtCourse.class));
    }

    @Test
    @DisplayName("title이 비어있으면 PtCourseInvalidException이 발생해야 한다")
    void createPtCourse_emptyTitle_throwsException() {

        // given
        CreatePtCourseCommand command = new CreatePtCourseCommand(
                1L, 1L, 1L, 1L, 1L,
                "",
                "설명", 5000, 12
        );

        // when & then
        assertThrows(PtCourseInvalidException.class,
                () -> ptCourseCommandService.createPtCourse(null, command));

        verify(ptCourseRepository, never()).save(any(PtCourse.class));
    }

    @Test
    @DisplayName("price가 음수이면 PtCourseInvalidException이 발생해야 한다")
    void createPtCourse_negativePrice_throwsException() {

        // given
        CreatePtCourseCommand command = new CreatePtCourseCommand(
                1L, 1L, 1L, 1L, 1L,
                "PT 강습 제목", "설명",
                -1, 12
        );

        // when & then
        assertThrows(PtCourseInvalidException.class,
                () -> ptCourseCommandService.createPtCourse(null, command));

        verify(ptCourseRepository, never()).save(any(PtCourse.class));
    }

    @Test
    @DisplayName("totalSessionCount가 1 미만이면 PtCourseInvalidException이 발생해야 한다")
    void createPtCourse_zeroTotalSessionCount_throwsException() {

        // given
        CreatePtCourseCommand command = new CreatePtCourseCommand(
                1L, 1L, 1L, 1L, 1L,
                "PT 강습 제목", "설명",
                50000, 0
        );

        // when & then
        assertThrows(PtCourseInvalidException.class,
                () -> ptCourseCommandService.createPtCourse(null, command));

        verify(ptCourseRepository, never()).save(any(PtCourse.class));
    }
}
