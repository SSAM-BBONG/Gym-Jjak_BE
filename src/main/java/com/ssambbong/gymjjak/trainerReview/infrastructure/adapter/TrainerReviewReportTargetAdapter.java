//package com.ssambbong.gymjjak.trainerReview.infrastructure.adapter;
//
//import com.ssambbong.gymjjak.pt.feedback.domain.repository.FeedbackRepository;
//import com.ssambbong.gymjjak.report.application.port.ReportTargetSnapshot;
//import com.ssambbong.gymjjak.trainerReview.domain.exception.TrainerReviewNotFoundException;
//import com.ssambbong.gymjjak.trainerReview.domain.model.TrainerReview;
//import com.ssambbong.gymjjak.trainerReview.domain.repository.TrainerReviewRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class TrainerReviewReportTargetAdapter {
//
//    private final TrainerReviewRepository trainerReviewRepository;
//
//    @Override
//    public ReportTargetSnapshot getSnapshot(Long targetId) {
//        log.debug("[TrainerReviewSnapshot] trainerReviewId={}", targetId);
//
//        TrainerReview trainerReview = trainerReviewRepository.findActiveById(targetId)
//                .orElseThrow(TrainerReviewNotFoundException::new);
//
//        log.info("[TrainerReviewSnapshot] trainerReviewId={}", targetId);
//
//        return new ReportTargetSnapshot(
//                trainerReview.getId(),
//                trainerReview.getUserId(),
//                null, // 제목 없음
//                trainerReview.getContent(),
//                null // 파일 없음
//        );
//
//    }
//}
