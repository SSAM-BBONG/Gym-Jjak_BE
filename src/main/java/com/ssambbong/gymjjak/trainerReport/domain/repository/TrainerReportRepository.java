package com.ssambbong.gymjjak.trainerReport.domain.repository;

import com.ssambbong.gymjjak.trainerReport.domain.model.TrainerReport;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainerReportRepository {

    Long save(TrainerReport trainerReport);

    // 배치 재실행 시 이미 이번 달 리포트를 생성했는지 확인해서 중복 AI 호출을 막는 용도.
    Optional<TrainerReport> findByTrainerProfileIdAndTargetMonth(Long trainerProfileId, LocalDate targetMonth);

    // 목록 조회 — target_month 최신순. hasNext 판단을 위해 size+1건을 요청하는 건 호출부(Service) 책임.
    List<TrainerReport> findAllByTrainerProfileId(Long trainerProfileId, int page, int size);

    // 상세 조회 — id + 소유자(trainerProfileId)를 함께 조회해서, 남의 리포트 존재 여부를 노출하지 않는다.
    Optional<TrainerReport> findByIdAndTrainerProfileId(Long id, Long trainerProfileId);
}
