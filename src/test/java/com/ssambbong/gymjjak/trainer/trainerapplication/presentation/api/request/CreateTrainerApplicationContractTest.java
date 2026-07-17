package com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.request;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.command.CreateTrainerApplicationCommand;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CreateTrainerApplicationContractTest {

    @Test
    void 트레이너_신청_요청은_복수_조직_ID_목록을_받는다() {
        List<String> componentNames = Arrays.stream(
                        CreateTrainerApplicationRequest.class.getRecordComponents()
                )
                .map(component -> component.getName())
                .toList();

        assertThat(componentNames)
                .contains("organizationIds")
                .doesNotContain("organizationId");
    }

    @Test
    void 트레이너_신청_Command는_복수_조직_ID_목록을_받는다() {
        Class<?> organizationIdsType = CreateTrainerApplicationCommand.class
                .getRecordComponents()[1]
                .getType();

        assertThat(organizationIdsType).isEqualTo(List.class);
    }
}
