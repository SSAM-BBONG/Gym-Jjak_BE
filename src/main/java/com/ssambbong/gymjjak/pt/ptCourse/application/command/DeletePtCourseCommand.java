package com.ssambbong.gymjjak.pt.ptCourse.application.command;

public record DeletePtCourseCommand (
        Long userId,
        Long ptCourseId
){
}
