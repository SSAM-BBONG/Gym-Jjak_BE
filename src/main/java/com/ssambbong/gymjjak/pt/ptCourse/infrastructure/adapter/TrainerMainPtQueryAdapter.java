package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence.SpringDataPtCourseRepository;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.port.out.TrainerMainPtQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TrainerMainPtQueryAdapter implements TrainerMainPtQueryPort {

    private final SpringDataPtCourseRepository ptCourseRepository;

    @Override
    // 트레이너 강습별 RESERVED, IN_PROGRESS 수강생 수를 합산한 값을 반환
    public long countCurrentStudents(Long trainerProfileId) {
        return ptCourseRepository.countCurrentStudentsByTrainerProfileId(trainerProfileId);
    }

    @Override
    // 수강생이 없는 강습까지 포함한 상위 PT 카드 정보를 반환합니다.
    public List<InProgressPtCourse> findTopCoursesByCurrentStudentCount(Long trainerProfileId, int limit) {
        return ptCourseRepository.findTopCoursesByCurrentStudentCount(trainerProfileId, limit).stream()
                // persistence row -> 외부 port 반환 타입으로 전환
                .map(course -> new InProgressPtCourse(
                        course.getPtCourseId(),
                        course.getOrganizationId(),
                        course.getThumbnailFileId(),
                        course.getTitle(),
                        course.getPrice(),
                        course.getCurrentStudentCount()
                ))
                .toList();
    }
}
