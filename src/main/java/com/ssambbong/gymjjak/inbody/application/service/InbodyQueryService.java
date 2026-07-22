package com.ssambbong.gymjjak.inbody.application.service;

import com.ssambbong.gymjjak.inbody.application.query.GetInbodyListQuery;
import com.ssambbong.gymjjak.inbody.application.result.InbodyItemResult;
import com.ssambbong.gymjjak.inbody.application.result.InbodyListResult;
import com.ssambbong.gymjjak.inbody.application.usecase.InbodyQueryUseCase;
import com.ssambbong.gymjjak.inbody.domain.model.BmiStatus;
import com.ssambbong.gymjjak.inbody.domain.model.Inbody;
import com.ssambbong.gymjjak.inbody.domain.repository.InbodyRepository;
import com.ssambbong.gymjjak.inbody.domain.repository.InbodySlice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InbodyQueryService implements InbodyQueryUseCase {

    // page 사이즈
    private static final int PAGE_SIZE = 2;
    // 변화량 계산 시 사용할 소수점 스케일
    private static final int MEASUREMENT_CHANGE_SCALE = 2;
    // 소수점 첫째 자리
    private static final int BMI_CHANGE_SCALE = 1;

    private final InbodyRepository inbodyRepository;

    @Override
    public InbodyListResult getInbodyList(GetInbodyListQuery query) {

        // 커서 페이지 조회 결과 묶음
        InbodySlice inbodySlice = inbodyRepository.findInbodySlice(
                query.userId(),
                query.measuredDate(),
                query.inbodyId(),
                PAGE_SIZE
        );

        List<Inbody> inbodies = inbodySlice.inbodies();
        // result List 객체 생성
        List<InbodyItemResult> results = new ArrayList<>(inbodies.size());

        for (int index = 0; index < inbodies.size(); index++) {
            Inbody inbody = inbodies.get(index);

            // 최신 기록만 직전 기록과 변화량 비교
            Inbody previousInbody = index == 0 && inbodies.size() > 1
                    ? inbodies.get(1)
                    : null;

            // 도메인 inbody 를 result로 변환
            // 이전 측정값 있으면, 변화량 함께 계산
            results.add(toInbodyItemResult(inbody, previousInbody));
        }

        Inbody lastInbody = inbodySlice.hasNext()
                ? inbodies.get(inbodies.size() - 1)
                : null;

        log.info(
                "event=inbody_list_fetched userId={}, resultCount={}, hasNext={}",
                query.userId(),
                results.size(),
                inbodySlice.hasNext()
        );

        return new InbodyListResult(
                results,
                lastInbody == null ? null : lastInbody.getMeasuredDate(),
                lastInbody == null ? null : lastInbody.getId(),
                inbodySlice.hasNext()
        );
    }

    private InbodyItemResult toInbodyItemResult(Inbody inbody, Inbody previousInbody) {
        BigDecimal bmi = inbody.calculateBmi();
        BmiStatus bmiStatus = BmiStatus.from(bmi);

        BigDecimal previousBmi = previousInbody == null
                ? null
                : previousInbody.calculateBmi();

        return new InbodyItemResult(
                inbody.getId(),
                inbody.getMeasuredDate(),
                inbody.getHeight(),
                inbody.getWeight(),
                inbody.getBodyFatPercentage(),
                inbody.getSkeletalMuscleMass(),
                inbody.getBmr(),
                bmi,
                bmiStatus,
                bmiStatus.getDescription(),
                calculateChange(inbody.getWeight(), getWeight(previousInbody), MEASUREMENT_CHANGE_SCALE),
                calculateChange(
                        inbody.getSkeletalMuscleMass(),
                        getSkeletalMuscleMass(previousInbody),
                        MEASUREMENT_CHANGE_SCALE
                ),
                calculateChange(
                        inbody.getBodyFatPercentage(),
                        getBodyFatPercentage(previousInbody),
                        MEASUREMENT_CHANGE_SCALE
                ),
                calculateChange(bmi, previousBmi, BMI_CHANGE_SCALE)
        );
    }

    // 절대 변화량 계산
    private BigDecimal calculateChange(
            BigDecimal currentValue,
            BigDecimal previousValue,
            int scale
    ) {
        if (currentValue == null || previousValue == null) {
            return null;
        }

        return currentValue.subtract(previousValue)
                .setScale(scale, RoundingMode.HALF_UP);
    }

    private BigDecimal getWeight(Inbody inbody) {
        return inbody == null ? null : inbody.getWeight();
    }

    private BigDecimal getSkeletalMuscleMass(Inbody inbody) {
        return inbody == null ? null : inbody.getSkeletalMuscleMass();
    }

    private BigDecimal getBodyFatPercentage(Inbody inbody) {
        return inbody == null ? null : inbody.getBodyFatPercentage();
    }
}
