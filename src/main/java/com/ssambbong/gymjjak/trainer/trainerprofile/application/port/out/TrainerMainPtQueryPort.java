package com.ssambbong.gymjjak.trainer.trainerprofile.application.port.out;

import java.util.List;

public interface TrainerMainPtQueryPort {

    // 트레이너 강습별 현재 수강생 수를 합산해 조회합니다.
    long countCurrentStudents(Long trainerProfileId);

    // 수강생이 없는 강습까지 포함해 현재 수강생 수 기준으로 상위 PT 강습을 조회합니다.
    List<InProgressPtCourse> findTopCoursesByCurrentStudentCount(Long trainerProfileId, int limit);

    record InProgressPtCourse(
            Long ptCourseId,
            Long organizationId,
            Long thumbnailFileId,
            String title,
            int price,
            long currentStudentCount
    ) {
    }
}
