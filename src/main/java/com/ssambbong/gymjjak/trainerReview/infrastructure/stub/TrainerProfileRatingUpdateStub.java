package com.ssambbong.gymjjak.trainerReview.infrastructure.stub;

import com.ssambbong.gymjjak.trainerReview.application.port.TrainerProfileRatingUpdatePort;
import org.springframework.stereotype.Component;

@Component
public class TrainerProfileRatingUpdateStub implements TrainerProfileRatingUpdatePort {

    @Override
    public void updateRatingStats(Long trainerProfileId, double averageRating, long reviewCount) {
        // TODO: 트레이너 프로필 담당자와 협의 후 구현
    }
}
