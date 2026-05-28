package com.ssambbong.gymjjak.pt.application.command;

public record CreatePtCourseCommand (
        Long userId,
        Long organizationId,
        Long trainerProfileId,
        Long categoryId,
        Long tagId,
        String title,
        String description,
        int price,
        int totalSessionCount
){
}
