package com.ssambbong.gymjjak.pt.ptReservation.presentation.api.response;

import com.ssambbong.gymjjak.pt.ptReservation.application.usecase.PtReservationQueryUseCase;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;

import java.util.List;

public record MyPtReservationDetailResponse(
        String thumbnailUrl,
        String title,
        String trainerName,
        PtReservationStatus status,
        int progressCount,
        int totalSessionCount,
        List<CurriculumItem> curriculums
) {

    // feedbackId = null -> 아직 피드백 없음
    public record CurriculumItem(Long id, int sessionNo, String title, Long feedbackId) {}

    // PtReservationDetailView(application 레이어 출력) -> 이 응답 형태로 변환
    public static MyPtReservationDetailResponse from(PtReservationQueryUseCase.PtReservationDetailView view) {
        List<CurriculumItem> items = view.curriculums().stream()
                .map(c -> new CurriculumItem(c.id(), c.sessionNo(), c.title(), c.feedbackId()))
                .toList();

        return new MyPtReservationDetailResponse(
                view.thumbnailUrl(),
                view.title(),
                view.trainerName(),
                view.status(),
                view.progressCount(),
                view.totalSessionCount(),
                items
        );
    }
}
