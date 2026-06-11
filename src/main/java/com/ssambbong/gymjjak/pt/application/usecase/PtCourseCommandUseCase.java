package com.ssambbong.gymjjak.pt.application.usecase;

import com.ssambbong.gymjjak.pt.application.command.CreatePtCourseCommand;

public interface PtCourseCommandUseCase {

    Long createPtCourse(CreatePtCourseCommand command);
}
