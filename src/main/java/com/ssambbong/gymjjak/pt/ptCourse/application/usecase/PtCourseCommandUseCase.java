package com.ssambbong.gymjjak.pt.ptCourse.application.usecase;

import com.ssambbong.gymjjak.pt.ptCourse.application.command.ChangePtCourseStatusCommand;
import com.ssambbong.gymjjak.pt.ptCourse.application.command.CreatePtCourseCommand;
import com.ssambbong.gymjjak.pt.ptCourse.application.command.DeletePtCourseCommand;
import com.ssambbong.gymjjak.pt.ptCourse.application.command.UpdatePtCourseCommand;

public interface PtCourseCommandUseCase {

    // PT 강습 등록 -> 생성된 ptCourseId 반환
    Long createPtCourse(CreatePtCourseCommand command);

    // PT 강습 수정 -> 수정된 ptCourseId 반환
    Long updatePtCourse(UpdatePtCourseCommand command);

    // PT 강습 상태 변경
    void changePtCourseStatus(ChangePtCourseStatusCommand command);

    // PT 강습 삭제
    void deletePtCourse(DeletePtCourseCommand command);
}
