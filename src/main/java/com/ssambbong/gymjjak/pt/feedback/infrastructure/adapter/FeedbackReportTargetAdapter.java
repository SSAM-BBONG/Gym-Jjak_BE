//package com.ssambbong.gymjjak.pt.feedback.infrastructure.adapter;
//
//import com.ssambbong.gymjjak.pt.feedback.domain.exception.FeedbackNotFoundException;
//import com.ssambbong.gymjjak.pt.feedback.domain.model.Feedback;
//import com.ssambbong.gymjjak.pt.feedback.domain.repository.FeedbackRepository;
//import com.ssambbong.gymjjak.report.application.port.ReportTargetSnapshot;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class FeedbackReportTargetAdapter{
//
//    private final FeedbackRepository feedbackRepository;
//
//    @Override
//    public ReportTargetSnapshot getSnapshot(Long targetId) {
//        log.debug("[FeedbackSnapshot] feedbackId={}", targetId);
//
//        Feedback feedback = feedbackRepository.findById(targetId)
//                .orElseThrow(FeedbackNotFoundException::new);
//
//        log.info("[FeedbackSnapshot] feedbackId={}", targetId);
//
//        return new ReportTargetSnapshot(
//                feedback.getId(),
//                feedback.getTrainerProfileId(),
//                null, // 피드백 제목 없음
//                feedback.getContent(),
//                null // TODO : 피드백 미디어까지 변환하는지, 파일은 안 보여주는지 확인하기
//        );
//    }
//}
