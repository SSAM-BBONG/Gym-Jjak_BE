package com.ssambbong.gymjjak.pt.ptCourse.presentation.api.request;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseStatus;
import jakarta.validation.constraints.NotNull;

public record ChangePtCourseStatusRequest(
        @NotNull PtCourseStatus status
) {}
