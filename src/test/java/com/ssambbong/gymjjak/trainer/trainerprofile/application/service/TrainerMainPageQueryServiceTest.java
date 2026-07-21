package com.ssambbong.gymjjak.trainer.trainerprofile.application.service;

import com.ssambbong.gymjjak.file.application.result.FileUrlResult;
import com.ssambbong.gymjjak.file.application.usecase.FileUrlUseCase;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.port.out.TrainerMainOrganizationQueryPort;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.port.out.TrainerMainPtQueryPort;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.TrainerMainPageResult;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfile;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfileStatus;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.repository.TrainerProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerMainPageQueryServiceTest {

    @Mock
    private TrainerProfileRepository trainerProfileRepository;

    @Mock
    private TrainerMainOrganizationQueryPort organizationQueryPort;

    @Mock
    private TrainerMainPtQueryPort ptQueryPort;

    @Mock
    private FileUrlUseCase fileUrlUseCase;

    @InjectMocks
    private TrainerMainPageQueryService trainerMainPageQueryService;

    @Test
    void findMainPage_combinesDashboardDataAndUsesBatchThumbnailLookup() {
        // 대시보드 카드의 조직명과 썸네일 URL은 각각 한 번의 배치 조회로 조합합니다.
        TrainerProfile trainerProfile = activeTrainerProfile();
        TrainerMainPtQueryPort.InProgressPtCourse course =
                new TrainerMainPtQueryPort.InProgressPtCourse(
                        101L,
                        1L,
                        100L,
                        "체형 교정 PT",
                        70000,
                        5L
                );

        when(trainerProfileRepository.findByUserId(7L)).thenReturn(Optional.of(trainerProfile));
        when(organizationQueryPort.countActiveOrganizations(10L)).thenReturn(2L);
        when(ptQueryPort.countCurrentStudents(10L)).thenReturn(12L);
        when(ptQueryPort.findTopCoursesByCurrentStudentCount(10L, 4)).thenReturn(List.of(course));
        when(organizationQueryPort.findOrganizationNamesByIds(List.of(1L)))
                .thenReturn(Map.of(1L, "짐잭 피트니스"));
        when(fileUrlUseCase.getUrls(List.of(100L), null, false))
                .thenReturn(Map.of(100L, new FileUrlResult("https://example.com/thumbnail.jpg", "thumbnail.jpg")));

        TrainerMainPageResult result = trainerMainPageQueryService.findMainPage(7L);

        assertThat(result.organizationCount()).isEqualTo(2L);
        assertThat(result.currentStudentCount()).isEqualTo(12L);
        assertThat(result.averageRating()).isEqualByComparingTo("4.8");
        assertThat(result.reviewCount()).isEqualTo(15);
        assertThat(result.inProgressPtCourses()).singleElement().satisfies(card -> {
            assertThat(card.ptCourseId()).isEqualTo(101L);
            assertThat(card.organizationName()).isEqualTo("짐잭 피트니스");
            assertThat(card.thumbnailUrl()).isEqualTo("https://example.com/thumbnail.jpg");
            assertThat(card.currentStudentCount()).isEqualTo(5L);
        });
        verify(organizationQueryPort).findOrganizationNamesByIds(List.of(1L));
        verify(fileUrlUseCase).getUrls(List.of(100L), null, false);
    }

    @Test
    void findMainPage_returnsEmptyCardsWithoutFileLookupWhenNoCourseExists() {
        when(trainerProfileRepository.findByUserId(7L)).thenReturn(Optional.of(activeTrainerProfileWithoutReviews()));
        when(organizationQueryPort.countActiveOrganizations(10L)).thenReturn(0L);
        when(ptQueryPort.countCurrentStudents(10L)).thenReturn(0L);
        when(ptQueryPort.findTopCoursesByCurrentStudentCount(10L, 4)).thenReturn(List.of());

        TrainerMainPageResult result = trainerMainPageQueryService.findMainPage(7L);

        assertThat(result.organizationCount()).isZero();
        assertThat(result.currentStudentCount()).isZero();
        assertThat(result.averageRating()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.reviewCount()).isZero();
        assertThat(result.inProgressPtCourses()).isEmpty();
        verify(organizationQueryPort, never()).findOrganizationNamesByIds(anyList());
        verifyNoInteractions(fileUrlUseCase);
    }

    private TrainerProfile activeTrainerProfile() {
        return TrainerProfile.restore(
                10L,
                7L,
                20L,
                null,
                "홍길동",
                "트레이너 소개",
                new BigDecimal("4.8"),
                15,
                TrainerProfileStatus.ACTIVE
        );
    }

    private TrainerProfile activeTrainerProfileWithoutReviews() {
        return TrainerProfile.restore(
                10L,
                7L,
                20L,
                null,
                "홍길동",
                "트레이너 소개",
                BigDecimal.ZERO,
                0,
                TrainerProfileStatus.ACTIVE
        );
    }
}
