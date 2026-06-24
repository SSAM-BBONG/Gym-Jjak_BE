package com.ssambbong.gymjjak.organization.organization.presentation.api.response;

import java.util.List;

public record FindOrganizationDetailResponse(
        String businessName,
        String roadAddress,
        String detailAddress,
        String facilityPhone,
        String instagramUrl,
        String blogUrl,
        String websiteUrl,
        int trainerCount,
        double avgRating,
        long accumulatedMembers,
        List<TrainerSummary> trainers
) {
    public record TrainerSummary(
            String trainerName,
            double averageRating,
            int reviewCount
    ) {}
}
