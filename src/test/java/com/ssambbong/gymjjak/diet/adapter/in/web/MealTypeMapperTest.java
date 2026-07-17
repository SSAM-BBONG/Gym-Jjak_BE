package com.ssambbong.gymjjak.diet.adapter.in.web;

import com.ssambbong.gymjjak.diet.domain.exception.InvalidMealTypeException;
import com.ssambbong.gymjjak.diet.domain.model.MealType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MealTypeMapperTest {

    private final MealTypeMapper mapper = new MealTypeMapper();

    @Test
    void 한글_식사유형을_enum으로_변환한다() {
        assertThat(mapper.toEnum("아침")).isEqualTo(MealType.BREAKFAST);
        assertThat(mapper.toEnum("점심")).isEqualTo(MealType.LUNCH);
        assertThat(mapper.toEnum("저녁")).isEqualTo(MealType.DINNER);
        assertThat(mapper.toEnum("간식")).isEqualTo(MealType.SNACK);
    }

    @Test
    void enum을_한글_식사유형으로_변환한다() {
        assertThat(mapper.toKorean(MealType.BREAKFAST)).isEqualTo("아침");
        assertThat(mapper.toKorean(MealType.LUNCH)).isEqualTo("점심");
        assertThat(mapper.toKorean(MealType.DINNER)).isEqualTo("저녁");
        assertThat(mapper.toKorean(MealType.SNACK)).isEqualTo("간식");
    }

    @Test
    void 지원하지_않는_한글_식사유형은_예외가_발생한다() {
        assertThatThrownBy(() -> mapper.toEnum("야식"))
                .isInstanceOf(InvalidMealTypeException.class);
    }
}
