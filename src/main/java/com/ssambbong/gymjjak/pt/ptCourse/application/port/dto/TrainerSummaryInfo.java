package com.ssambbong.gymjjak.pt.ptCourse.application.port.dto;

public record TrainerSummaryInfo(
        Long trainerProfileId,
        String trainerName,
        Double averageRating,
        int reviewCount
) {}
