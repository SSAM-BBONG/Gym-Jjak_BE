package com.ssambbong.gymjjak.global.domain.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileType {

    PROFILE_IMAGE("uploads/profiles/trainers"),
    PT_THUMBNAIL("uploads/courses/thumbnails"),
    CERTIFICATION("uploads/certifications"),
    AWARD("uploads/awards"),
    BUSINESS_LICENSE("uploads/organizations"),
    FEEDBACK_VIDEO("uploads/feedbacks/videos");

    private final String path;
}
