package com.ssambbong.gymjjak.pt.ptReservation.domain.model;

import com.ssambbong.gymjjak.pt.ptReservation.domain.exception.PtReservationInvalidException;

import java.time.LocalDateTime;

public class PtReservation {

    // DB 저장 후 자동 부여. 새로 만들 때는 null
    private final Long id;

    private final Long userId;
    private final Long ptCourseId;
    private final Long organizationId;
    private final Long trainerProfileId;
    private final LocalDateTime reservedStartAt;
    private final LocalDateTime reservedEndAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime completedAt;
    private int progressCount;
    private final int totalSessionCount;
    // 예약 상태. 생성 시 RESERVED로 고정
    private PtReservationStatus status;

    public PtReservation(Long id,
                         Long userId,
                         Long ptCourseId,
                         Long organizationId,
                         Long trainerProfileId,
                         LocalDateTime reservedStartAt,
                         LocalDateTime reservedEndAt,
                         LocalDateTime cancelledAt,
                         LocalDateTime completedAt,
                         int progressCount,
                         int totalSessionCount,
                         PtReservationStatus status
    ) {
        // 도메인 불변식 보호
        if (reservedStartAt == null) {
            throw new PtReservationInvalidException();
        }
        if (reservedEndAt == null) {
            throw new PtReservationInvalidException();
        }
        // 종료 시간이 시작 시간보다 앞이면 안 됨
        if (!reservedEndAt.isAfter(reservedStartAt)) {
            throw new PtReservationInvalidException();
        }
        this.id = id;
        this.userId = userId;
        this.ptCourseId = ptCourseId;
        this.organizationId = organizationId;
        this.trainerProfileId = trainerProfileId;
        this.reservedStartAt = reservedStartAt;
        this.reservedEndAt = reservedEndAt;
        this.cancelledAt = cancelledAt;
        this.completedAt = completedAt;
        this.progressCount = progressCount;
        this.totalSessionCount = totalSessionCount;
        this.status = status;
    }

    // 새 예약 생성 (신청 완료)
    public static PtReservation create(
            Long userId,
            Long ptCourseId,
            Long organizationId,
            Long trainerProfileId,
            LocalDateTime reservedStartAt,
            LocalDateTime reservedEndAt,
            int totalSessionCount
    ) {
        return new PtReservation(
                null,
                userId,
                ptCourseId,
                organizationId,
                trainerProfileId,
                reservedStartAt,
                reservedEndAt,
                null,   // cancelledAt
                null,   // completedAt
                0,      // progressCount
                totalSessionCount,
                PtReservationStatus.RESERVED
        );
    }

    // 도메인 객체 복원 시 사용
    public static PtReservation restore(
            Long id,
            Long userId,
            Long ptCourseId,
            Long organizationId,
            Long trainerProfileId,
            LocalDateTime reservedStartAt,
            LocalDateTime reservedEndAt,
            LocalDateTime cancelledAt,
            LocalDateTime completedAt,
            int progressCount,
            int totalSessionCount,
            PtReservationStatus status
    ) {
        return new PtReservation(
                id,
                userId,
                ptCourseId,
                organizationId,
                trainerProfileId,
                reservedStartAt,
                reservedEndAt,
                cancelledAt,
                completedAt,
                progressCount,
                totalSessionCount,
                status
        );
    }
    // getter
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getPtCourseId() { return ptCourseId; }
    public Long getOrganizationId() { return organizationId; }
    public Long getTrainerProfileId() { return trainerProfileId; }
    public LocalDateTime getReservedStartAt() { return reservedStartAt; }
    public LocalDateTime getReservedEndAt() { return reservedEndAt; }
    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public int getProgressCount() { return progressCount; }
    public int getTotalSessionCount() { return totalSessionCount; }
    public PtReservationStatus getStatus() { return status; }
}
