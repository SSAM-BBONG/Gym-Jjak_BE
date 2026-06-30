package com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.adapter;

import com.ssambbong.gymjjak.trainer.trainerprofile.domain.exception.TrainerProfileNotFoundException;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.repository.SpringDataTrainerProfileRepository;
import com.ssambbong.gymjjak.trainerReview.application.port.TrainerProfileRatingUpdatePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

// 강사평 - 트레이너 프로필 평균 평점 & 리뷰 수 갱신하는 어댑터
@Component
@Transactional
@RequiredArgsConstructor
public class TrainerProfileRatingUpdateAdapter implements TrainerProfileRatingUpdatePort {

    private final SpringDataTrainerProfileRepository springDataTrainerProfileRepository;

    @Override
    public void updateRatingStats(Long trainerProfileId, double averageRating, long reviewCount) {
        int updated = springDataTrainerProfileRepository.updateRatingStats(
                trainerProfileId,
                BigDecimal.valueOf(averageRating),
                Math.toIntExact(reviewCount)
        );
        if (updated != 1) throw new TrainerProfileNotFoundException("trainerProfileId", trainerProfileId);
    }
}
