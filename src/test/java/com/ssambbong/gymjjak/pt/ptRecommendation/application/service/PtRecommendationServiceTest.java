package com.ssambbong.gymjjak.pt.ptRecommendation.application.service;

import com.ssambbong.gymjjak.pt.ptCourse.application.port.OrganizationQueryPort;
import com.ssambbong.gymjjak.pt.ptCourse.application.port.TrainerProfileQueryPort;
import com.ssambbong.gymjjak.pt.ptCourse.application.port.dto.TrainerSummaryInfo;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseStatus;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseRepository;
import com.ssambbong.gymjjak.pt.ptReservation.application.usecase.PtReservationQueryUseCase;
import com.ssambbong.gymjjak.pt.ptReservation.application.usecase.PtReservationQueryUseCase.MyPtReservationView;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;
import com.ssambbong.gymjjak.pt.ptRecommendation.application.command.PtRecommendationCommand;
import com.ssambbong.gymjjak.pt.ptRecommendation.application.port.OnboardingQueryPort;
import com.ssambbong.gymjjak.pt.ptRecommendation.application.port.PtRecommendationAiPort;
import com.ssambbong.gymjjak.pt.ptRecommendation.application.result.PtRecommendationResult;
import com.ssambbong.gymjjak.pt.ptRecommendation.domain.exception.PtRecommendationNotFoundException;
import com.ssambbong.gymjjak.pt.ptRecommendation.domain.model.PainOnset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PtRecommendationServiceTest {

    private static final Long USER_ID = 1L;

    // 온보딩 기준주소(가상: 강남역 부근)
    private static final BigDecimal USER_LAT = BigDecimal.valueOf(37.4979);
    private static final BigDecimal USER_LNG = BigDecimal.valueOf(127.0276);

    @Mock private PtCourseRepository ptCourseRepository;
    @Mock private OrganizationQueryPort organizationQueryPort;
    @Mock private TrainerProfileQueryPort trainerProfileQueryPort;
    @Mock private OnboardingQueryPort onboardingQueryPort;
    @Mock private PtReservationQueryUseCase ptReservationQueryUseCase;
    @Mock private PtRecommendationAiPort ptRecommendationAiPort;

    private PtRecommendationService service;

    @BeforeEach
    void setUp() {
        service = new PtRecommendationService(
                ptCourseRepository, organizationQueryPort, trainerProfileQueryPort,
                onboardingQueryPort, ptReservationQueryUseCase, ptRecommendationAiPort);
    }

    private PtCourse course(Long id, Long organizationId, Long trainerProfileId, String title) {
        return PtCourse.restore(id, organizationId, trainerProfileId, PartType.LEG, null,
                title, "코스 설명 - " + title, 200_000, 8,
                PtCourseStatus.VISIBLE, null, null);
    }

    private OnboardingQueryPort.MyOnboardingInfo onboardingWithRegion(BigDecimal lat, BigDecimal lng) {
        return new OnboardingQueryPort.MyOnboardingInfo("근비대", "6개월 미만", "주 3회", lat, lng);
    }

    private PtRecommendationCommand command(int distanceLevel) {
        return new PtRecommendationCommand(
                USER_ID, List.of(PartType.LEG), distanceLevel, false, null, null);
    }

    @Test
    @DisplayName("부위+거리 조건에 맞는 후보가 있으면 AI 추천 결과를 그대로 반환한다")
    void recommend_returnsAiResult_whenCandidatesFound() {
        when(onboardingQueryPort.findMyOnboarding(USER_ID)).thenReturn(onboardingWithRegion(USER_LAT, USER_LNG));

        // 가까운 조직(사용자와 거의 같은 좌표) 소속 코스 1개만 매칭 대상
        PtCourse nearCourse = course(101L, 10L, 5L, "무릎 재활 코스");
        when(ptCourseRepository.findAllVisibleByParts(List.of(PartType.LEG))).thenReturn(List.of(nearCourse));

        when(organizationQueryPort.findAllByIds(List.of(10L))).thenReturn(Map.of(
                10L, new OrganizationQueryPort.OrganizationInfo(
                        10L, "가까운 헬스장", "서울 강남구", USER_LAT.doubleValue(), USER_LNG.doubleValue(),
                        null, null, null)
        ));
        when(trainerProfileQueryPort.findSummaryAllByIds(List.of(5L))).thenReturn(Map.of(
                5L, new TrainerSummaryInfo(5L, "김트레이너", 4.8, 12)
        ));
        when(ptReservationQueryUseCase.findMyReservations(USER_ID, null)).thenReturn(List.of());

        PtRecommendationAiPort.RecommendedCourse aiResult = new PtRecommendationAiPort.RecommendedCourse(
                101L, "무릎 재활 코스", 5L, "김트레이너", "무릎 통증에 적합합니다.");
        when(ptRecommendationAiPort.recommend(any(), any(), eq(false), isNull(), isNull()))
                .thenReturn(List.of(aiResult));

        PtRecommendationResult result = service.recommend(command(3));

        assertEquals(1, result.recommendations().size());
        assertEquals(101L, result.recommendations().get(0).courseId());
        assertEquals("김트레이너", result.recommendations().get(0).trainerName());
    }

    @Test
    @DisplayName("부위 조건에 맞는 코스가 하나도 없으면 PtRecommendationNotFoundException이 발생한다")
    void recommend_throwsNotFound_whenNoPartMatch() {
        when(onboardingQueryPort.findMyOnboarding(USER_ID)).thenReturn(onboardingWithRegion(USER_LAT, USER_LNG));
        when(ptCourseRepository.findAllVisibleByParts(List.of(PartType.LEG))).thenReturn(List.of());

        assertThrows(PtRecommendationNotFoundException.class, () -> service.recommend(command(3)));
        verify(ptRecommendationAiPort, never()).recommend(any(), any(), anyBoolean(), any(), any());
    }

    @Test
    @DisplayName("부위는 맞아도 전부 거리 범위 밖이면 PtRecommendationNotFoundException이 발생한다")
    void recommend_throwsNotFound_whenAllOutOfDistanceRange() {
        when(onboardingQueryPort.findMyOnboarding(USER_ID)).thenReturn(onboardingWithRegion(USER_LAT, USER_LNG));

        PtCourse farCourse = course(101L, 10L, 5L, "먼 코스");
        when(ptCourseRepository.findAllVisibleByParts(List.of(PartType.LEG))).thenReturn(List.of(farCourse));

        // 부산 좌표 (서울과 약 300km 이상 떨어짐) — distanceLevel=1(1km 이내)로는 절대 안 걸림
        when(organizationQueryPort.findAllByIds(List.of(10L))).thenReturn(Map.of(
                10L, new OrganizationQueryPort.OrganizationInfo(
                        10L, "부산 헬스장", "부산", 35.1796, 129.0756, null, null, null)
        ));

        assertThrows(PtRecommendationNotFoundException.class, () -> service.recommend(command(1)));
        verify(ptRecommendationAiPort, never()).recommend(any(), any(), anyBoolean(), any(), any());
    }

    @Test
    @DisplayName("PT 이력이 없으면 '이력 없음' 요약을, 있으면 코스별 진행 요약을 프로필에 담아 AI에 전달한다")
    void recommend_buildsPtHistorySummaryFromReservations() {
        when(onboardingQueryPort.findMyOnboarding(USER_ID)).thenReturn(onboardingWithRegion(USER_LAT, USER_LNG));

        PtCourse nearCourse = course(101L, 10L, 5L, "무릎 재활 코스");
        when(ptCourseRepository.findAllVisibleByParts(List.of(PartType.LEG))).thenReturn(List.of(nearCourse));
        when(organizationQueryPort.findAllByIds(List.of(10L))).thenReturn(Map.of(
                10L, new OrganizationQueryPort.OrganizationInfo(
                        10L, "가까운 헬스장", "서울 강남구", USER_LAT.doubleValue(), USER_LNG.doubleValue(),
                        null, null, null)
        ));
        when(trainerProfileQueryPort.findSummaryAllByIds(List.of(5L))).thenReturn(Map.of(
                5L, new TrainerSummaryInfo(5L, "김트레이너", 4.8, 12)
        ));
        when(ptReservationQueryUseCase.findMyReservations(USER_ID, null)).thenReturn(List.of(
                new MyPtReservationView(1L, null, "근비대 코스", "이트레이너",
                        PtReservationStatus.COMPLETED, null, 8, 8)
        ));
        when(ptRecommendationAiPort.recommend(any(), any(), anyBoolean(), any(), any()))
                .thenReturn(List.of(new PtRecommendationAiPort.RecommendedCourse(
                        101L, "무릎 재활 코스", 5L, "김트레이너", "적합합니다.")));

        service.recommend(command(3));

        ArgumentCaptor<PtRecommendationAiPort.Profile> profileCaptor =
                ArgumentCaptor.forClass(PtRecommendationAiPort.Profile.class);
        verify(ptRecommendationAiPort).recommend(any(), profileCaptor.capture(), anyBoolean(), any(), any());
        assertTrue(profileCaptor.getValue().ptHistorySummary().contains("근비대 코스"));
    }
}
