package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence.SpringDataPtCourseRepository;
import com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence.TrainerMainPtCourseRow;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.port.out.TrainerMainPtQueryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerMainPtQueryAdapterTest {

    @Mock
    private SpringDataPtCourseRepository ptCourseRepository;

    @InjectMocks
    private TrainerMainPtQueryAdapter adapter;

    @Test
    void countCurrentStudents_returnsPerCourseAggregatedStudentCount() {
        when(ptCourseRepository.countCurrentStudentsByTrainerProfileId(10L)).thenReturn(12L);

        long result = adapter.countCurrentStudents(10L);

        assertThat(result).isEqualTo(12L);
    }

    @Test
    void findTopCoursesByCurrentStudentCount_mapsCourseWithoutCurrentStudents() {
        TrainerMainPtCourseRow row = mock(TrainerMainPtCourseRow.class);
        when(row.getPtCourseId()).thenReturn(101L);
        when(row.getOrganizationId()).thenReturn(1L);
        when(row.getThumbnailFileId()).thenReturn(100L);
        when(row.getTitle()).thenReturn("체형 교정 PT");
        when(row.getPrice()).thenReturn(70000);
        when(row.getCurrentStudentCount()).thenReturn(0L);
        when(ptCourseRepository.findTopCoursesByCurrentStudentCount(10L, 4)).thenReturn(List.of(row));

        List<TrainerMainPtQueryPort.InProgressPtCourse> result =
                adapter.findTopCoursesByCurrentStudentCount(10L, 4);

        assertThat(result).containsExactly(new TrainerMainPtQueryPort.InProgressPtCourse(
                101L,
                1L,
                100L,
                "체형 교정 PT",
                70000,
                0L
        ));
        verify(ptCourseRepository).findTopCoursesByCurrentStudentCount(10L, 4);
    }
}
