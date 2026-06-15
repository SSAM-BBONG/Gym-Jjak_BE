package com.ssambbong.gymjjak.pt.ptCourse.domain.model;

import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseInvalidException;

public class PtCurriculum {

    private final Long id;
    private final Long ptCourseId;
    private final int sessionNo;
    private final String title;
    private final String content;

    // 생성자
    private PtCurriculum(Long id, Long ptCourseId, int sessionNo, String title, String content) {
        this.id = id;
        this.ptCourseId = ptCourseId;
        this.sessionNo = sessionNo;
        this.title = title;
        this.content = content;
    }

    // 신규 생성 시
    public static PtCurriculum create(Long ptCourseId, int sessionNo, String title, String content) {
        if (ptCourseId == null) {
            throw new PtCourseInvalidException();
        }
        if (sessionNo <= 0) {
            throw new PtCourseInvalidException();
        }
        if (title == null || title.isBlank()) {
            throw new PtCourseInvalidException();
        }
        if (content == null || content.isBlank()) {
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
