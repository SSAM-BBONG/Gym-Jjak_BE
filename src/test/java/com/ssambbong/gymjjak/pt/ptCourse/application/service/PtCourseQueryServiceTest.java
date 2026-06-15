package com.ssambbong.gymjjak.pt.ptCourse.application.service;

import com.ssambbong.gymjjak.category.application.usecase.CategoryQueryUseCase;
import com.ssambbong.gymjjak.pt.ptCourse.application.port.OrganizationQueryPort;
import com.ssambbong.gymjjak.pt.ptCourse.application.port.TrainerProfileQueryPort;
import com.ssambbong.gymjjak.pt.ptCourse.application.usecase.PtCourseQueryUseCase;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseNotFoundException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseStatus;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCurriculum;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseSchedule;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseRepository;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCurriculumRepository;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseScheduleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PtCourseQueryServiceTest {

    @Mock private PtCourseRepository ptCourseRepository;
    @Mock private PtCurriculumRepository ptCurriculumRepository;
    @Mock private PtCourseScheduleRepository ptCourseScheduleRepository;
    @Mock private CategoryQueryUseCase categoryQueryUseCase;
    @Mock private OrganizationQueryPort organizationQueryPort;
    @Mock private TrainerProfileQueryPort trainerProfileQueryPort;

    @InjectMocks
    private PtCourseQueryService ptCourseQueryService;

    // ──── 공통 픽스처 ────

    private PtCourse stubPtCourse(Long id, PtCourseStatus status) {
        return PtCourse.restore(
                id, 1L, 1L, 1L, 1L, null,
                "맞춤 PT", "PT 소개글", 300000, 8,
                status
        );
    }

    private void stubCategoryAndEnrich() {
        when(categoryQueryUseCase.handle()).thenReturn(
                List.of(new CategoryQueryUseCase.CategoryView(1L, "헬스", null, 0L))
        );

        when(organizationQueryPort.findById(eq(1L))).thenReturn(
                new OrganizationQueryPort.OrganizationInfo(
                        1L, "짐짝피트니스", "서울 강남구", 37.5007, 127.0365,
                        "02-1234-5678", null, null)
        );
        when(trainerProfileQueryPort.findById(eq(1L))).thenReturn(
                new TrainerProfileQueryPort.TrainerDisplayInfo(
                        "트레이너01", "안전하게 지도합니다.", 4.6, 1, null, List.of(), List.of())
        );
    }

    // ──── 목록 조회 ────

    @Test
    @DisplayName("PT 강습 목록 조회 시 전체 목록이 반환되어야 한다")
    void findAllPtCourses_success() {
        // given
        PtCourse ptCourse = stubPtCourse(1L, PtCourseStatus.VISIBLE);
        when(ptCourseRepository.findAllVisible()).thenReturn(List.of(ptCourse));
        stubCategoryAndEnrich();

        // when
        List<PtCourseQueryUseCase.PtCourseListView> result =
                ptCourseQueryService.findAllPtCourses();

        // then
        assertEquals(1, result.size());
        assertEquals("헬스", result.get(0).categoryName());
        assertEquals("짐짝피트니스", result.get(0).organizationBusinessName());
        assertEquals("트레이너01", result.get(0).trainerName());
        verify(ptCourseRepository).findAllVisible();
    }

    @Test
    @DisplayName("PT 강습이 없으면 빈 목록을 반환해야 한다")
    void findAllPtCourses_empty() {
        // given
        when(ptCourseRepository.findAllVisible()).thenReturn(List.of());
        when(categoryQueryUseCase.handle()).thenReturn(List.of());

        // when
        List<PtCourseQueryUseCase.PtCourseListView> result =
                ptCourseQueryService.findAllPtCourses();

        // then
        assertTrue(result.isEmpty());
    }

    // ──── 상세 조회 ────

    @Test
    @DisplayName("PT 강습 상세 조회 시 커리큘럼과 스케줄을 포함한 전체 정보가 반환되어야 한다")
    void findPtCourseDetail_success() {
        // given
        PtCourse ptCourse = stubPtCourse(1L, PtCourseStatus.VISIBLE);
        when(ptCourseRepository.findById(1L)).thenReturn(Optional.of(ptCourse));
        when(trainerProfileQueryPort.findById(eq(1L))).thenReturn(
                new TrainerProfileQueryPort.TrainerDisplayInfo(
                        "트레이너01",
                        "안전하게 지도합니다.",
                        4.6,
                        1,
                        null,
                        List.of(),
                        List.of()
                )
        );

        List<PtCurriculum> curriculums = List.of(
                PtCurriculum.restore(1L, 1L, 1, "기초 체력 평가", "체력 측정 및 목표 설정"),
                PtCurriculum.restore(2L, 1L, 2, "벤치프레스 기초", "올바른 자세 익히기")
        );
        when(ptCurriculumRepository.findAllByPtCourseId(1L)).thenReturn(curriculums);

        List<PtCourseSchedule> schedules = List.of(
                PtCourseSchedule.restore(1L, 1L, DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0))
        );
        when(ptCourseScheduleRepository.findAllByPtCourseId(1L)).thenReturn(schedules);

        // when
        PtCourseQueryUseCase.PtCourseDetailView result =
                ptCourseQueryService.findPtCourseDetail(1L);

        // then
        assertEquals(1L, result.ptCourseId());
        assertEquals("트레이너01", result.trainerName());

        // 커리큘럼 세부 필드 검증
        assertEquals(2, result.curriculums().size());
        assertEquals(1L, result.curriculums().get(0).curriculumId());
        assertEquals(1, result.curriculums().get(0).sessionNo());
        assertEquals("기초 체력 평가", result.curriculums().get(0).title());
        assertEquals("체력 측정 및 목표 설정", result.curriculums().get(0).content());

        // 스케줄 세부 필드 검증
        assertEquals(1, result.schedules().size());
        assertEquals(1L, result.schedules().get(0).scheduleId());
        assertEquals(DayOfWeek.MONDAY, result.schedules().get(0).dayOfWeek());
        assertEquals(LocalTime.of(10, 0), result.schedules().get(0).startTime());
        assertEquals(LocalTime.of(11, 0), result.schedules().get(0).endTime());

        verify(ptCourseRepository).findById(1L);
        verify(ptCurriculumRepository).findAllByPtCourseId(1L);
        verify(ptCourseScheduleRepository).findAllByPtCourseId(1L);
    }

    @Test
    @DisplayName("존재하지 않는 PT 강습 조회 시 PtCourseNotFoundException이 발생해야 한다")
    void findPtCourseDetail_notFound() {
        // given
        when(ptCourseRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(PtCourseNotFoundException.class,
                () -> ptCourseQueryService.findPtCourseDetail(999L));

        verify(ptCourseRepository).findById(999L);
    }

    @Test
    @DisplayName("VISIBLE이 아닌 PT 강습 조회 시 PtCourseNotFoundException이 발생해야 한다")
    void findPtCourseDetail_notVisible() {
        // given
        PtCourse blocked = stubPtCourse(1L, PtCourseStatus.BLOCKED);
        when(ptCourseRepository.findById(1L)).thenReturn(Optional.of(blocked));

        // when & then
        assertThrows(PtCourseNotFoundException.class,
                () -> ptCourseQueryService.findPtCourseDetail(1L));
    }
}
