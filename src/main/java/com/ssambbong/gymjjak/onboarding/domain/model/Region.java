package com.ssambbong.gymjjak.onboarding.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Region {

    private final Long id;
    private final String sido;
    private final String sigungu;
    private final String eupmyeondong;
    private final String fullName;
    private final BigDecimal latitude;
    private final BigDecimal longitude;

    private Region(
            Long id,
            String sido,
            String sigungu,
            String eupmyeondong,
            String fullName,
            BigDecimal latitude,
            BigDecimal longitude
    ) {
        this.id = id;
        this.sido = Objects.requireNonNull(sido, "시/도는 필수입니다.");
        this.sigungu = Objects.requireNonNull(sigungu, "시/군/구는 필수입니다.");
        this.eupmyeondong = Objects.requireNonNull(eupmyeondong, "읍/면/동은 필수입니다.");
        this.fullName = Objects.requireNonNull(fullName, "전체 주소는 필수입니다.");
        this.latitude = Objects.requireNonNull(latitude, "위도는 필수입니다.");
        this.longitude = Objects.requireNonNull(longitude, "경도는 필수입니다.");
    }

    public static Region create(
            String sido,
            String sigungu,
            String eupmyeondong,
            String fullName,
            BigDecimal latitude,
            BigDecimal longitude
    ) {
        return new Region(
                null,
                sido,
                sigungu,
                eupmyeondong,
                fullName,
                latitude,
                longitude
        );
    }

    public static Region reconstruct(
            Long id,
            String sido,
            String sigungu,
            String eupmyeondong,
            String fullName,
            BigDecimal latitude,
            BigDecimal longitude
    ) {
        return new Region(
                id,
                sido,
                sigungu,
                eupmyeondong,
                fullName,
                latitude,
                longitude
        );
    }

    public Long getId() {
        return id;
    }

    public String getSido() {
        return sido;
    }

    public String getSigungu() {
        return sigungu;
    }

    public String getEupmyeondong() {
        return eupmyeondong;
    }

    public String getFullName() {
        return fullName;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }
}