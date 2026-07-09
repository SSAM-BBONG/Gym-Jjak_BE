package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence;

// [dashboard] 조직 PT 목록 집계 쿼리 projection
public interface OrgPtCourseRow {
    Long getPtCourseId();
    String getTitle();
    String getStatus();
    String getTrainerName();
    Long getCurrentStudentCount();
}
