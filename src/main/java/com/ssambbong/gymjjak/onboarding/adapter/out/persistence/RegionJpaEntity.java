package com.ssambbong.gymjjak.onboarding.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "regions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "region_id", nullable = false)
    private Long id;

    @Column(name = "sido", nullable = false, length = 50)
    private String sido;

    @Column(name = "sigungu", nullable = false, length = 50)
    private String sigungu;

    @Column(name = "eupmyeondong", nullable = false, length = 50)
    private String eupmyeondong;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(name = "latitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    public RegionJpaEntity(
            Long id,
            String sido,
            String sigungu,
            String eupmyeondong,
            String fullName,
            BigDecimal latitude,
            BigDecimal longitude
    ) {
        this.id = id;
        this.sido = sido;
        this.sigungu = sigungu;
        this.eupmyeondong = eupmyeondong;
        this.fullName = fullName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void update(
            String sido,
            String sigungu,
            String eupmyeondong,
            String fullName,
            BigDecimal latitude,
            BigDecimal longitude
    ) {
        this.sido = sido;
        this.sigungu = sigungu;
        this.eupmyeondong = eupmyeondong;
        this.fullName = fullName;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
