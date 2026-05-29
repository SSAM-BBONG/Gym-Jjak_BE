package com.ssambbong.gymjjak.pt.application.usecase;

import com.ssambbong.gymjjak.pt.application.command.CreatePtCourseCommand;
import org.springframework.web.multipart.MultipartFile;

public interface PtCourseCommandUseCase {

    // PT 강습 등록 -> 생성된 ptCourseId 반환
    Long createPtCourse(MultipartFile thumbnail, CreatePtCourseCommand command);
}
