package com.ssambbong.gymjjak.pt.application.command;

public record CreatePtCourseCommand (
        Long userId,
        Long categoryId,
        Long tagId,
        String title,
        String description,
        int price,
        int totalSessionCount
){
}
