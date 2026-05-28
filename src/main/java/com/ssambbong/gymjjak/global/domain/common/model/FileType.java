package com.ssambbong.gymjjak.global.domain.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileType {

    TRAINER_PROFILE("uploads/profiles/trainers"),
    COURSE_THUMBNAIL("uploads/courses/thumbnails"),
    CERTIFICATION("uploads/certifications"),
    AWARD("uploads/awards"),
    BUSINESS_LICENSE("uploads/organizations"),
    FEEDBACK_VIDEO("uploads/feedbacks/videos");

    private final String path;
}
