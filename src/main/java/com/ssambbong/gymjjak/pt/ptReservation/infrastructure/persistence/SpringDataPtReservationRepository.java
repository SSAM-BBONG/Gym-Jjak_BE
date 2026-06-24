package com.ssambbong.gymjjak.pt.ptReservation.infrastructure.persistence;

import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SpringDataPtReservationRepository extends JpaRepository<PtReservationJpaEntity, Long> {

    @Query("""
            SELECT COUNT(r) > 0 FROM PtReservationJpaEntity r
            WHERE r.ptCourseId = :ptCourseId
            AND r.status = com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus.RESERVED
            AND r.reservedStartAt < :reservedEndAt
            AND r.reservedEndAt > :reservedStartAt
            """)
    boolean existsOverlappingReservation(
            @Param("ptCourseId") Long ptCourseId,
            @Param("reservedStartAt") LocalDateTime reservedStartAt,
            @Param("reservedEndAt") LocalDateTime reservedEndAt
    );

    // status 미지정 -> 전체 조회
    List<PtReservationJpaEntity> findAllByUserIdOrderByReservedStartAtDesc(Long userId);

    // status 지정 -> 필터
    List<PtReservationJpaEntity> findAllByUserIdAndStatusOrderByReservedStartAtDesc(Long userId, PtReservationStatus status);

    // ptCourse 도메인 강습별 활성 예약 수 배치 집계 (N+1 방지)
    @Query("""
            SELECT r.ptCourseId, COUNT(r)
            FROM PtReservationJpaEntity r
            WHERE r.ptCourseId IN :ptCourseIds
              AND r.status IN :statuses
            GROUP BY r.ptCourseId
            """)
    List<Object[]> countActiveGroupByPtCourseId(
            @Param("ptCourseIds") List<Long> ptCourseIds,
            @Param("statuses") List<PtReservationStatus> statuses);

    // ptCourse 도메인 강습별 전체 예약 수 배치 집계 (N+1 방지)
    @Query("""
            SELECT r.ptCourseId, COUNT(r)
            FROM PtReservationJpaEntity r
            WHERE r.ptCourseId IN :ptCourseIds
            GROUP BY r.ptCourseId
            """)
    List<Object[]> countTotalGroupByPtCourseId(@Param("ptCourseIds") List<Long> ptCourseIds);

    // 강습별 수강생 목록 조회 (최신 예약일순)
    List<PtReservationJpaEntity> findAllByPtCourseIdOrderByReservedStartAtDesc(Long ptCourseId);

}
