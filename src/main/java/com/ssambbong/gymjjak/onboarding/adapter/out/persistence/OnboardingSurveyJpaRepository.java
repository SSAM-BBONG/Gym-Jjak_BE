package com.ssambbong.gymjjak.onboarding.adapter.out.persistence;

import com.ssambbong.gymjjak.onboarding.application.port.out.MyOnboardingView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OnboardingSurveyJpaRepository extends JpaRepository<OnboardingSurveyJpaEntity, Long> {

    boolean existsByUser_Id(Long userId);

    Optional<OnboardingSurveyJpaEntity> findByUser_Id(Long userId);

    @Query("""
    select new com.ssambbong.gymjjak.onboarding.application.port.out.MyOnboardingView(
        o.id,
        o.exerciseGoal,
        o.exercisePeriod,
        o.exerciseFrequency,
        o.preferredExercise,
        r.id,
        r.sido,
        r.sigungu,
        r.eupmyeondong,
        r.fullName,
        r.latitude,
        r.longitude,
        o.height,
        o.weight
    )
    from OnboardingSurveyJpaEntity o
    join o.preferredRegion r
    where o.user.id = :userId
""")
    Optional<MyOnboardingView> findMyOnboardingByUserId(@Param("userId") Long userId);

}
