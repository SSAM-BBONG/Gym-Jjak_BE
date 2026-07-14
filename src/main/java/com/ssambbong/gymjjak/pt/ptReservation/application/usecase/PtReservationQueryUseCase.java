package com.ssambbong.gymjjak.pt.ptReservation.application.usecase;

import com.ssambbong.gymjjak.pt.ptReservation.application.result.MonthlyPtReservationResult;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtSessionStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PtReservationQueryUseCase {

    // 내 PT 예약 기록 목록 조회 - status null이면 전체 조회
    List<MyPtReservationView> findMyReservations(Long userId, PtReservationStatus status);

    // 내 PT 예약 기록 상세 조회 + 본인 확인
    PtReservationDetailView findMyReservationDetail(Long userId, Long ptReservationId);

    // adminDashBoard에서 사용할 월별 예약된 pt 수 조회
    List<MonthlyPtReservationResult> findMonthlyPtReservations();

    // 유저+코스 기준 완료 회차 수
    int countProgressByUserIdAndPtCourseId(Long userId, Long ptCourseId);

    // 내 PT 세션 목록 조회 (예약 탭)
    List<PtSessionView> findMySessions(Long userId);

    record MyPtReservationView(
            Long ptReservationId,
            String thumbnailUrl,
            String title,
            String trainerName,
            PtReservationStatus status,
            LocalDate lastPtDate,
            int progressCount,
            int totalSessionCount
    ) {}

    record PtReservationDetailView(
            String thumbnailUrl,
            String title,
            String trainerName,
            PtReservationStatus status,
            int progressCount,
            int totalSessionCount,
            List<CurriculumView> curriculums   // 회차별 커리큘럼 + 피드백 여부
    ) {}

    // 커리큘럼 1개 회차 (피드백 작성됐으면 feedbackId, 아니면 null)
    record CurriculumView(
            Long id,
            int sessionNo,
            String title,
            Long feedbackId
    ) {}

    record PtSessionView(
            Long ptReservationId,
            Long ptCourseId,
            String ptCourseTitle,
            String trainerName,
            LocalDateTime reservedStartAt,
            LocalDateTime reservedEndAt,
            PtSessionStatus sessionStatus
    ) {}
}
