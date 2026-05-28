package com.ssambbong.gymjjak.pt.application.command;

public record CreatePtCourseCommand (
        Long organizationId,
        Long trainerProfileId,
        Long categoryId,
        Long tagId,
        Long thumbnailFileId,
        String title,
        String description,
        int price,
        int totalSessionCount
){
}
