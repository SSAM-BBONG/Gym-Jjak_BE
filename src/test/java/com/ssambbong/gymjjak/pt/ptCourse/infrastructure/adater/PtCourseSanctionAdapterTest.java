package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.adater;

import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseNotFoundException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseStatus;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseRepository;
import com.ssambbong.gymjjak.pt.ptCourse.infrastructure.adapter.PtCourseSanctionAdapter;
import com.ssambbong.gymjjak.report.application.port.ReportSanctionAction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PtCourseSanctionAdapterTest {

    @Mock
    private PtCourseRepository ptCourseRepository;

    @InjectMocks
    private PtCourseSanctionAdapter ptCourseSanctionAdapter;

    @Test
    @DisplayName("블라인드 적용 시 PT 강습 상태가 BLOCKED로 변경되어야 한다")
    void applyAutoBlind_success() {
        // Given
        PtCourse ptCourse = PtCourse.restore(
                1L, 1L, 1L, PartType.CHEST, null,
                "PT 강습", "설명", 50000, 12,
                PtCourseStatus.VISIBLE, null, null
        );
        when(ptCourseRepository.findById(1L)).thenReturn(Optional.of(ptCourse));

        // When
        ptCourseSanctionAdapter.applySanction(1L, ReportSanctionAction.APPLY_AUTO_BLIND);

        // Then
        assertEquals(PtCourseStatus.BLOCKED, ptCourse.getStatus());
        verify(ptCourseRepository).update(ptCourse);
    }

    @Test
    @DisplayName("블라인드 해제 시 PT 강습 상태가 VISIBLE로 복구되어야 한다")
    void releaseAutoBlind_success() {
        // Given
        PtCourse ptCourse = PtCourse.restore(
                1L, 1L, 1L, PartType.CHEST, null,
                "PT 강습", "설명", 50000, 12,
                PtCourseStatus.BLOCKED, null, null
        );
        when(ptCourseRepository.findById(1L)).thenReturn(Optional.of(ptCourse));

        // When
        ptCourseSanctionAdapter.applySanction(1L, ReportSanctionAction.RELEASE_AUTO_BLIND);

        // Then
        assertEquals(PtCourseStatus.VISIBLE, ptCourse.getStatus());
        verify(ptCourseRepository).update(ptCourse);
    }

    @Test
    @DisplayName("존재하지 않는 PT 강습 블라인드 시 PtCourseNotFoundException이 발생해야 한다")
    void applyAutoBlind_ptCourseNotFound_throwsException() {
        // Given
        when(ptCourseRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(PtCourseNotFoundException.class,
                () -> ptCourseSanctionAdapter.applySanction(999L, ReportSanctionAction.APPLY_AUTO_BLIND));

        verify(ptCourseRepository, never()).update(any());
    }
}
