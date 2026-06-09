package com.ssambbong.gymjjak.global.application.scheduler;

// 스케줄 실행 결과
public record RetentionResult(
        String jobName,
        int candidateCount, // 삭제 후보 수
        int deletedChildCount, // 자식 데이터 삭제 수
        int deletedParentCount // 부모 데이터 삭제 수
) {

    /* Comment
    *   pt 도메인 같이, 부모/자식 개념이 없으면 아래처럼 사용
    *   new RetentionJobResult("pt-course-retention", candidateCount, 0, deletedPtCourseCount)
    * */
    public static RetentionResult empty(String jobName) {
        return new RetentionResult(jobName, 0, 0, 0);
    }

}
