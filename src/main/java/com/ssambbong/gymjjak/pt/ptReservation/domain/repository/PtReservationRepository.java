package com.ssambbong.gymjjak.pt.ptReservation.domain.repository;

import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservation;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PtReservationRepository {

    // PT 예약 저장
    PtReservation save(PtReservation ptReservation);

    // 중복 예약 여부 확인. service 에서 중복이면 예외 발생
    boolean existsByPtCourseIdAndTimeOverlap(
            Long ptCourseId,
            LocalDateTime reservedStartedAt,
            LocalDateTime reservedEndAt
    );

    // status null이면 전체 조회
    List<PtReservation> findAllByUserId(Long userId, PtReservationStatus status);

    // 예약 1건 상세 조회 (본인 확인용)
    Optional<PtReservation> findById(Long id);

    // 강습별 수강생 목록 조회
    List<PtReservation> findAllByPtCourseId(Long ptCourseId);

    // PT 상태 변경
    void updateStatus(PtReservation ptReservation);

    // 특정 상태 예약 수 집계 (통계용)
    long countByStatus(PtReservationStatus status);

    // 특정 강습의 기간 내 RESERVED 예약 시작 시각 목록 (가용 날짜/시간 슬롯 계산용)
    List<LocalDateTime> findReservedStartAtsByPtCourseId(Long ptCourseId, LocalDateTime from, LocalDateTime to);

    // AdminDashboard : 월별 예약된 pt 수 조회 (통계)
    List<MonthlyReservationCount> findMonthlyReservationCounts(
            PtReservationStatus excludedStatus,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
    // 내부 record 사용
    record MonthlyReservationCount(
            String month,
            long count
    ) {
    }
}
