package com.ssambbong.gymjjak.diet.adapter.out.persistence;

import com.ssambbong.gymjjak.diet.application.port.out.MealAccessPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MealAccessAdapter implements MealAccessPort {

    private final MealAccessJpaRepository repository;

    @Override
    public boolean existsActivePtRelation(Long targetUserId, Long trainerUserId) {
        return repository.existsActivePtRelation(targetUserId, trainerUserId);
    }
}
