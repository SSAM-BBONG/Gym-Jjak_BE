package com.ssambbong.gymjjak.pt.ptCourse.domain.model;

import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseInvalidException;

public class PtCurriculum {

    private final Long id;
    private final Long ptCourseId;
    private final int sessionNo;
    private final String title;
    private final String content;

    // 생성자
    public PtCurriculum(Long id, Long ptCourseId, int sessionNo, String title, String content) {
        this.id = id;
        this.ptCourseId = ptCourseId;
        this.sessionNo = sessionNo;
        this.title = title;
        this.content = content;
    }

    // 신규 생성 시
    public static PtCurriculum create(Long ptCourseId, int sessionNo, String title, String content) {
        if (sessionNo <= 0) { // 커리큘럼 회차 등록되지 않으면
            throw new PtCourseInvalidException();
        }
        return new PtCurriculum(null, ptCourseId, sessionNo, title, content);
    }

    // 데이터 복원 시
    public static PtCurriculum restore(Long id, Long ptCourseId, int sessionNo, String title, String content) {
        return new PtCurriculum(id, ptCourseId, sessionNo, title, content);
    }

    // getter
    public Long getId() {
        return id;
    }

    public Long getPtCourseId() {
        return ptCourseId;
    }

    public int getSessionNo() {
        return sessionNo;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
