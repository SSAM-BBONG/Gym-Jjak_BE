package com.ssambbong.gymjjak.pt.ptReservation.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseNotFoundException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCurriculumRepository;
import com.ssambbong.gymjjak.pt.ptReservation.application.port.PtCourseQueryPort;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PtCourseQueryAdapter implements PtCourseQueryPort {

    private final EntityManager em;
    private final PtCurriculumRepository ptCurriculumRepository;

    // PT 코스 정보 + 트레이너명 단일 JOIN 쿼리 (N+1 방지)
    @Override
    public PtCourseInfo findPtCourseInfo(Long ptCourseId) {
        List<?> results = em.createNativeQuery("""
                SELECT pc.title, pc.thumbnail_file_id, tp.trainer_name
                FROM pt_courses pc
                JOIN trainer_profiles tp ON pc.trainer_profile_id = tp.trainer_profile_id
                WHERE pc.pt_course_id = ?1
                  AND tp.deleted_at IS NULL
                """)
                .setParameter(1, ptCourseId)
                .getResultList();

        Object[] result = (Object[]) results.stream()
                .findFirst()
                .orElseThrow(PtCourseNotFoundException::new);

        return new PtCourseInfo(
                (String) result[0],
                result[1] != null ? ((Number) result[1]).longValue() : null,
                (String) result[2]
        );
    }

    // 커리큘럼 목록 조회 - sessionNo 기준 오름차순 정렬 보장
    @Override
    public List<CurriculumInfo> findCurriculumsByPtCourseId(Long ptCourseId) {
        return ptCurriculumRepository.findAllByPtCourseId(ptCourseId).stream()
                .sorted(Comparator.comparingInt(c -> c.getSessionNo()))
                .map(c -> new CurriculumInfo(c.getId(), c.getSessionNo(), c.getTitle()))
                .toList();
    }
}
