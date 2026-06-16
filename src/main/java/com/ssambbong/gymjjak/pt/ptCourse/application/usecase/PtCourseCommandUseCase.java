package com.ssambbong.gymjjak.pt.ptCourse.application.usecase;

import com.ssambbong.gymjjak.pt.ptCourse.application.command.CreatePtCourseCommand;

public interface PtCourseCommandUseCase {

    // PT 강습 등록 -> 생성된 ptCourseId 반환
    Long createPtCourse(CreatePtCourseCommand command);
}
