package com.ssambbong.gymjjak.organization.organization.application.query;

import com.ssambbong.gymjjak.organization.organizationTrainer.application.query.TrainerDetailView;

import java.util.List;

public record OrganizationDetailResult(
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
        List<TrainerDetailView> trainers
) {}
