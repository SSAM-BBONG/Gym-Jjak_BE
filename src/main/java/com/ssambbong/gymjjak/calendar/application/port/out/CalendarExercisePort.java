package com.ssambbong.gymjjak.calendar.application.port.out;

import com.ssambbong.gymjjak.calendar.application.result.CalendarExerciseSnapshot;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;

public interface CalendarExercisePort {

    CalendarExerciseSnapshot findExerciseByIdAndPart(
            Long exerciseId,
            PartType part
    );
}
