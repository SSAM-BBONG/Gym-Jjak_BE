package com.ssambbong.gymjjak.pt.ptReservation.infrastructure.persistence;

import com.ssambbong.gymjjak.global.infrastructure.presentation.BaseLifecycleTimeEntity;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "pt_reservations")
@EntityListeners(AuditingEntityListener.class)
public class PtReservationJpaEntity extends BaseLifecycleTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pt_reservation_id")
    private Long id;

    // 예약한 사용자 ID
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 예약한 PT 강습 ID
    @Column(name = "pt_course_id", nullable = false)
    private Long ptCourseId;

    // PT 강습 소속 조직 ID
    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    // PT 강습 트레이너 프로필 ID
    @Column(name = "trainer_profile_id", nullable = false)
    private Long trainerProfileId;

    // 예약 시작 시간
    @Column(name = "reserved_start_at", nullable = false)
    private LocalDateTime reservedStartAt;

    // 예약 종료 시간
    @Column(name = "reserved_end_at", nullable = false)
    private LocalDateTime reservedEndAt;

    // 완료된 회차 수. 생성 시 0
    @Column(name = "progress_count", nullable = false)
    private int progressCount;

    // PT 강습 전체 회차 수
    @Column(name = "total_session_count", nullable = false)
    private int totalSessionCount;

    // 예약 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private PtReservationStatus status;

    // repoAdapter에서 새 예약 저장할 떄 사용
    public PtReservationJpaEntity(
            Long userId,
            Long ptCourseId,
            Long organizationId,
            Long trainerProfileId,
            LocalDateTime reservedStartAt,
            LocalDateTime reservedEndAt,
            int progressCount,
            int totalSessionCount,
            PtReservationStatus status
    ) {
        this.userId = userId;
        this.ptCourseId = ptCourseId;
        this.organizationId = organizationId;
        this.trainerProfileId = trainerProfileId;
        this.reservedStartAt = reservedStartAt;
        this.reservedEndAt = reservedEndAt;
        this.progressCount = progressCount;
        this.totalSessionCount = totalSessionCount;
        this.status = status;
    }

    public void updateStatus(PtReservationStatus status) {
        this.status = status;
        if (status == PtReservationStatus.CANCELLED) {
            super.cancel();
        }
        if (status == PtReservationStatus.COMPLETED) {
            super.complete();
        }
    }
}
