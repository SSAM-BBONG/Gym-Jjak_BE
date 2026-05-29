package com.ssambbong.gymjjak.onboarding.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.Optional;

public interface RegionJpaRepository extends JpaRepository<RegionJpaEntity, Long> {

    Optional<RegionJpaEntity> findByFullNameAndLatitudeAndLongitude(
            String fullName,
            BigDecimal latitude,
            BigDecimal longitude
    );
}
