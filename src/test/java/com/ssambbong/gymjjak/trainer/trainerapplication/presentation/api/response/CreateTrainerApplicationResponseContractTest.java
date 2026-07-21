package com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.response;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.command.CreateTrainerApplicationCommand;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.usecase.TrainerApplicationCommandUseCase;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CreateTrainerApplicationResponseContractTest {

    @Test
    void 트레이너_신청_생성_UseCase는_신청_ID_목록을_반환한다() throws Exception {
        Method method = TrainerApplicationCommandUseCase.class.getMethod(
                "createTrainerApplication",
                CreateTrainerApplicationCommand.class
        );

        assertThat(method.getReturnType()).isEqualTo(List.class);
    }

    @Test
    void 트레이너_신청_생성_응답은_신청_ID_목록을_포함한다() {
        List<String> componentNames = Arrays.stream(
                        CreateTrainerApplicationResponse.class.getRecordComponents()
                )
                .map(component -> component.getName())
                .toList();

        Class<?> componentType = Arrays.stream(
                        CreateTrainerApplicationResponse.class.getRecordComponents()
                )
                .filter(component -> component.getName().equals("trainerApplicationIds"))
                .findFirst()
                .map(component -> component.getType())
                .orElse(null);

        assertThat(componentNames).containsExactly("trainerApplicationIds");
        assertThat(componentType).isEqualTo(List.class);
    }
}
