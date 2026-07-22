package com.ssambbong.gymjjak.pt.ptRecommendation.application.service;

import com.ssambbong.gymjjak.onboarding.application.port.in.OnboardingUsecase;
import com.ssambbong.gymjjak.onboarding.application.result.MyOnboardingResult;
import com.ssambbong.gymjjak.pt.ptCourse.application.port.OrganizationQueryPort;
import com.ssambbong.gymjjak.pt.ptCourse.application.port.OrganizationQueryPort.OrganizationInfo;
import com.ssambbong.gymjjak.pt.ptCourse.application.port.TrainerProfileQueryPort;
import com.ssambbong.gymjjak.pt.ptCourse.application.port.dto.TrainerSummaryInfo;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseRepository;
import com.ssambbong.gymjjak.pt.ptReservation.application.usecase.PtReservationQueryUseCase;
import com.ssambbong.gymjjak.pt.ptReservation.application.usecase.PtReservationQueryUseCase.MyPtReservationView;
import com.ssambbong.gymjjak.pt.ptRecommendation.application.command.PtRecommendationCommand;
import com.ssambbong.gymjjak.pt.ptRecommendation.application.port.PtRecommendationAiPort;
import com.ssambbong.gymjjak.pt.ptRecommendation.application.result.PtRecommendationResult;
import com.ssambbong.gymjjak.pt.ptRecommendation.application.usecase.PtRecommendationUseCase;
import com.ssambbong.gymjjak.pt.ptRecommendation.domain.exception.PtRecommendationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PtRecommendationService implements PtRecommendationUseCase {

    // 당근마켓 스타일 거리 슬라이더(1~5) -> km 임계값 매핑.
    // Region이 읍/면/동 단위 좌표라 정확한 반경이라기보다 "대략적인 범위"로 사용한다.
    private static final Map<Integer, Double> DISTANCE_LEVEL_TO_KM = Map.of(
            1, 1.0,
            2, 3.0,
            3, 5.0,
            4, 10.0,
            5, 30.0
    );
    private static final double DEFAULT_MAX_DISTANCE_KM = 5.0;
    private static final int MAX_CANDIDATES_TO_AI = 10;
    private static final double EARTH_RADIUS_KM = 6371.0;

    private final PtCourseRepository ptCourseRepository;
    private final OrganizationQueryPort organizationQueryPort;
    private final TrainerProfileQueryPort trainerProfileQueryPort;
    private final OnboardingUsecase onboardingUsecase;
    private final PtReservationQueryUseCase ptReservationQueryUseCase;
    private final PtRecommendationAiPort ptRecommendationAiPort;

    @Override
    public PtRecommendationResult recommend(PtRecommendationCommand command) {
        MyOnboardingResult onboarding = onboardingUsecase.getMyOnboarding(command.userId());

        List<PtCourse> candidates = filterByPartAndDistance(command, onboarding);
        if (candidates.isEmpty()) {
            throw new PtRecommendationNotFoundException();
        }

        Map<Long, TrainerSummaryInfo> trainers = trainerProfileQueryPort.findSummaryAllByIds(
                candidates.stream().map(PtCourse::getTrainerProfileId).distinct().toList());

        List<PtRecommendationAiPort.CandidateCourse> aiCandidates = candidates.stream()
                .map(course -> new PtRecommendationAiPort.CandidateCourse(
                        course.getId(),
                        course.getTitle(),
                        course.getTrainerProfileId(),
                        trainerNameOf(trainers, course.getTrainerProfileId()),
                        course.getDescription()))
                .toList();

        PtRecommendationAiPort.Profile profile = new PtRecommendationAiPort.Profile(
                onboarding.exerciseGoal(),
                onboarding.exercisePeriod(),
                onboarding.exerciseFrequency(),
                buildPtHistorySummary(command.userId()));

        String painOnsetValue = command.painOnset() == null ? null : command.painOnset().name();

        List<PtRecommendationAiPort.RecommendedCourse> recommended = ptRecommendationAiPort.recommend(
                aiCandidates, profile, command.hasPain(), command.painArea(), painOnsetValue);

        return new PtRecommendationResult(
                recommended.stream()
                        .map(r -> new PtRecommendationResult.RecommendedCourseResult(
                                r.courseId(), r.courseName(), r.trainerId(), r.trainerName(), r.reason()))
                        .toList());
    }

    // 1차 필터링(비AI): 부위 조건으로 후보를 뽑은 뒤, 온보딩 기준주소 대비 거리로 좁힌다.
    private List<PtCourse> filterByPartAndDistance(PtRecommendationCommand command, MyOnboardingResult onboarding) {
        List<PtCourse> matchedByPart = ptCourseRepository.findAllVisibleByParts(command.targetParts());
        if (matchedByPart.isEmpty()) {
            return List.of();
        }

        Map<Long, OrganizationInfo> organizations = organizationQueryPort.findAllByIds(
                matchedByPart.stream().map(PtCourse::getOrganizationId).distinct().toList());

        double maxDistanceKm = DISTANCE_LEVEL_TO_KM.getOrDefault(command.distanceLevel(), DEFAULT_MAX_DISTANCE_KM);
        MyOnboardingResult.RegionResult region = onboarding.preferredRegion();
        double userLat = region.latitude().doubleValue();
        double userLng = region.longitude().doubleValue();

        return matchedByPart.stream()
                .map(course -> Map.entry(course, organizations.get(course.getOrganizationId())))
                .filter(entry -> entry.getValue() != null
                        && entry.getValue().latitude() != null
                        && entry.getValue().longitude() != null)
                .map(entry -> Map.entry(entry.getKey(),
                        haversineKm(userLat, userLng, entry.getValue().latitude(), entry.getValue().longitude())))
                .filter(entry -> entry.getValue() <= maxDistanceKm)
                .sorted(Comparator.comparingDouble(Map.Entry::getValue))
                .limit(MAX_CANDIDATES_TO_AI)
                .map(Map.Entry::getKey)
                .toList();
    }

    private String trainerNameOf(Map<Long, TrainerSummaryInfo> trainers, Long trainerProfileId) {
        TrainerSummaryInfo info = trainers.get(trainerProfileId);
        return info != null ? info.trainerName() : "알 수 없음";
    }

    private String buildPtHistorySummary(Long userId) {
        List<MyPtReservationView> reservations = ptReservationQueryUseCase.findMyReservations(userId, null);
        if (reservations.isEmpty()) {
            return "PT 이력 없음 (첫 PT 등록 예정)";
        }
        return reservations.stream()
                .map(r -> "%s(%s, %d/%d회차)".formatted(
                        r.title(), r.status(), r.progressCount(), r.totalSessionCount()))
                .collect(Collectors.joining(", "));
    }

    private static double haversineKm(double lat1, double lon1, Double lat2Decimal, Double lon2Decimal) {
        double lat2 = lat2Decimal;
        double lon2 = lon2Decimal;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
}
