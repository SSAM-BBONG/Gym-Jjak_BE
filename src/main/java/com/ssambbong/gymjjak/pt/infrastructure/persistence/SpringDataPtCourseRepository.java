package com.ssambbong.gymjjak.pt.infrastructure.persistence;

import com.ssambbong.gymjjak.pt.domain.model.PtCourseStatus;
import com.ssambbong.gymjjak.pt.domain.repository.PtCourseRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataPtCourseRepository extends JpaRepository<PtCourseJpaEntity, Long> {
}
