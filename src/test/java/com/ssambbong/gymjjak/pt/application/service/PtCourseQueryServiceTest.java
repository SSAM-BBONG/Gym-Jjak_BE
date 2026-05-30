package com.ssambbong.gymjjak.pt.application.service;

import com.ssambbong.gymjjak.category.application.usecase.CategoryQueryUseCase;
import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
import com.ssambbong.gymjjak.pt.application.port.PtCourseEnrichQueryPort;
import com.ssambbong.gymjjak.pt.application.usecase.PtCourseQueryUseCase;
import com.ssambbong.gymjjak.pt.domain.exception.PtCourseNotFoundException;
import com.ssambbong.gymjjak.pt.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.domain.model.PtCourseStatus;
import com.ssambbong.gymjjak.pt.domain.repository.PtCourseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PtCourseQueryServiceTest {

    @Mock private PtCourseRepository ptCourseRepository;
    @Mock private CategoryQueryUseCase categoryQueryUseCase;
    @Mock private PtCourseEnrichQueryPort enrichQueryPort;
    @Mock private FileUseCase fileUseCase;

    @InjectMocks
    private PtCourseQueryService ptCourseQueryService;

    // ──── 공통 픽스처 ────

    private PtCourse stubPtCourse(Long id, PtCourseStatus status) {
        return PtCourse.restore(
                id, 1L, 1L, 1L, 1L, null,
                "맞춤 PT", "PT 소개글", 300000, 8,
                false, false, status
        );
    }

    private void stubCategoryAndEnrich() {
        when(categoryQueryUseCase.handle()).thenReturn(
                List.of(new CategoryQueryUseCase.CategoryView(1L, "헬스"))
        );
        when(enrichQueryPort.findOrganizationById(anyLong())).thenReturn(
                new PtCourseEnrichQueryPort.OrganizationInfo(
                        "짐짝피트니스", "서울 강남구", 37.5007, 127.0365,
                        "02-1234-5678", null, null)
        );
        when(enrichQueryPort.findTrainerProfileById(anyLong())).thenReturn(
                new PtCourseEnrichQueryPort.TrainerDisplayInfo(
                        "트레이너01", "4년차", "안전하게 지도합니다.", 4.6, 1)
        );
    }

    // ──── 목록 조회 ────

    @Test
    @DisplayName("PT 강습 목록 조회 시 페이지 정보와 함께 반환되어야 한다")
    void findAllPtCourses_success() {
        // given
        PtCourse ptCourse = stubPtCourse(1L, PtCourseStatus.VISIBLE);
        when(ptCourseRepository.findAllVisible(null, null, 0, 20))
                .thenReturn(new PtCourseRepository.PtCoursePage(List.of(ptCourse), 1L));
        stubCategoryAndEnrich();

        // when
        PtCourseQueryUseCase.PtCoursePageResult result =
                ptCourseQueryService.findAllPtCourses(null, null, 0, 20);

        // then
        assertEquals(1, result.content().size());
        assertEquals(1L, result.totalElements());
        assertEquals(1, result.totalPages());
        assertEquals("헬스", result.content().get(0).categoryName());
        assertEquals("짐짝피트니스", result.content().get(0).organizationName());
        assertEquals("트레이너01", result.content().get(0).trainerName());
        verify(ptCourseRepository).findAllVisible(null, null, 0, 20);
    }

    @Test
    @DisplayName("PT 강습이 없으면 빈 목록을 반환해야 한다")
    void findAllPtCourses_empty() {
        // given
        when(ptCourseRepository.findAllVisible(null, null, 0, 20))
                .thenReturn(new PtCourseRepository.PtCoursePage(List.of(), 0L));
        when(categoryQueryUseCase.handle()).thenReturn(List.of());

        // when
        PtCourseQueryUseCase.PtCoursePageResult result =
                ptCourseQueryService.findAllPtCourses(null, null, 0, 20);

        // then
        assertTrue(result.content().isEmpty());
        assertEquals(0L, result.totalElements());
        assertEquals(0, result.totalPages());
    }

    // ──── 상세 조회 ────

    @Test
    @DisplayName("PT 강습 상세 조회 시 전체 정보가 반환되어야 한다")
    void findPtCourseDetail_success() {
        // given
        PtCourse ptCourse = stubPtCourse(1L, PtCourseStatus.VISIBLE);
        when(ptCourseRepository.findById(1L)).thenReturn(Optional.of(ptCourse));
        stubCategoryAndEnrich();

        // when
        PtCourseQueryUseCase.PtCourseDetailView result =
                ptCourseQueryService.findPtCourseDetail(1L);

        // then
        assertEquals(1L, result.ptCourseId());
        assertEquals("헬스", result.categoryName());
        assertEquals("짐짝피트니스", result.organizationName());
        assertEquals("트레이너01", result.trainerName());
        verify(ptCourseRepository).findById(1L);
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
        verify(categoryQueryUseCase, never()).handle();
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

        verify(categoryQueryUseCase, never()).handle();
    }
}
