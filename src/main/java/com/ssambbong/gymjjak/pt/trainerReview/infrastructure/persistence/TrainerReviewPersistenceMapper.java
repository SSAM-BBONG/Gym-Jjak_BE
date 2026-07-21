package com.ssambbong.gymjjak.pt.trainerReview.infrastructure.persistence;

import com.ssambbong.gymjjak.pt.trainerReview.domain.model.TrainerReview;
import com.ssambbong.gymjjak.pt.trainerReview.domain.model.TrainerReviewStatus;

import org.springframework.stereotype.Component;

@Component
public class TrainerReviewPersistenceMapper {

    public TrainerReviewJpaEntity toEntity(TrainerReview domain) {
        return TrainerReviewJpaEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .trainerProfileId(domain.getTrainerProfileId())
                .ptCourseId(domain.getPtCourseId())
                .rating(domain.getRating())
                .content(domain.getContent())
                .status(domain.getStatus().name())
                .deletedAt(domain.getDeletedAt())
                .build();
    }

    public TrainerReview toDomain(TrainerReviewJpaEntity entity) {
        return TrainerReview.restore(
                entity.getId(),
                entity.getUserId(),
                entity.getTrainerProfileId(),
                entity.getPtCourseId(),
                entity.getRating(),
                entity.getContent(),
                TrainerReviewStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
