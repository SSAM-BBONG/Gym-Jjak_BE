package com.ssambbong.gymjjak.trainer.trainerapplication.application.service;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out.TrainerApplicationQueryPort;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.MyTrainerApplicationListResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerApplicationQueryServiceTest {

    @Mock
    private TrainerApplicationQueryPort trainerApplicationQueryPort;

    @InjectMocks
    private TrainerApplicationQueryService trainerApplicationQueryService;

    @Test
    void retrieves_my_trainer_applications_with_fixed_page_size_of_ten() {
        MyTrainerApplicationListResult expected = new MyTrainerApplicationListResult(
                List.of(), 0, 10, 0, 0, false
        );
        when(trainerApplicationQueryPort.findMyTrainerApplications(1L, 0, 10))
                .thenReturn(expected);

        MyTrainerApplicationListResult result = trainerApplicationQueryService
                .findMyTrainerApplications(1L, 0);

        assertThat(result).isSameAs(expected);
        verify(trainerApplicationQueryPort).findMyTrainerApplications(1L, 0, 10);
    }
}
