package com.ssambbong.gymjjak.trainer.trainerprofile.application.service;

import com.ssambbong.gymjjak.file.application.result.FileUrlResult;
import com.ssambbong.gymjjak.file.application.usecase.FileUrlUseCase;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.port.out.TrainerProfileSearchQueryPort;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.SearchTrainerCondition;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.SearchTrainerListResult;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.SearchTrainerResult;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.TrainerProfileDetailResult;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerAward;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerCertification;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfile;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfileStatus;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.repository.TrainerAwardRepository;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.repository.TrainerCertificationRepository;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.repository.TrainerProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerProfileQueryServiceTest {

    @Mock
    private TrainerProfileRepository trainerProfileRepository;

    @Mock
    private TrainerCertificationRepository trainerCertificationRepository;

    @Mock
    private TrainerAwardRepository trainerAwardRepository;

    @Mock
    private FileUrlUseCase fileUrlUseCase;

    @InjectMocks
    private TrainerProfileQueryService service;

    @Mock
    private TrainerProfileSearchQueryPort trainerProfileSearchQueryPort;

    @Test
    void 프로필_ID로_공개_상세_정보를_조회한다() {
        // given
        TrainerProfile profile = TrainerProfile.restore(
                7L,
                2L,
                9L,
                100L,
                "홍길동",
                "체형 교정 전문 트레이너입니다.",
                new BigDecimal("4.85"),
                32,
                TrainerProfileStatus.ACTIVE
        );

        TrainerCertification requiredCertification =
                TrainerCertification.restore(
                        10L,
                        7L,
                        "생활스포츠지도사",
                        200L,
                        com.ssambbong.gymjjak.trainer.trainerprofile
                                .domain.model.TrainerCertificationType.REQUIRED
                );

        TrainerCertification additionalCertification =
                TrainerCertification.restore(
                        11L,
                        7L,
                        "NSCA-CPT",
                        null,
                        com.ssambbong.gymjjak.trainer.trainerprofile
                                .domain.model.TrainerCertificationType.ADDITIONAL
                );

        TrainerAward award = TrainerAward.restore(
                20L,
                7L,
                "2025 피트니스 대회 우승"
        );

        when(trainerProfileRepository.findById(7L))
                .thenReturn(Optional.of(profile));

        when(trainerCertificationRepository
                .findAllByTrainerProfileId(7L))
                .thenReturn(List.of(
                        requiredCertification,
                        additionalCertification
                ));

        when(trainerAwardRepository
                .findAllByTrainerProfileId(7L))
                .thenReturn(List.of(award));

        when(fileUrlUseCase.getUrl(100L, null, false))
                .thenReturn(
                        new FileUrlResult(
                                "https://example.com/profile.png",
                                "profile.png"
                        )
                );

        // when
        TrainerProfileDetailResult result =
                service.getTrainerProfileDetail(7L);

        // then
        assertThat(result.trainerProfileId()).isEqualTo(7L);
        assertThat(result.profileImageUrl())
                .isEqualTo("https://example.com/profile.png");
        assertThat(result.trainerName()).isEqualTo("홍길동");
        assertThat(result.averageRating())
                .isEqualByComparingTo("4.85");
        assertThat(result.reviewCount()).isEqualTo(32);

        assertThat(result.certifications()).hasSize(2);
        assertThat(result.certifications().get(0).name())
                .isEqualTo("생활스포츠지도사");
        assertThat(result.certifications().get(1).name())
                .isEqualTo("NSCA-CPT");

        assertThat(result.awards()).hasSize(1);
        assertThat(result.awards().get(0).name())
                .isEqualTo("2025 피트니스 대회 우승");

        // 공개 프로필 이미지만 URL 조회
        verify(fileUrlUseCase).getUrl(100L, null, false);

        // 필수 자격증 파일 ID 200은 URL 조회 금지
        verify(fileUrlUseCase, never())
                .getUrl(200L, null, false);
    }

    @Test
    void 조건에_맞는_트레이너를_검색한다() {
        // given
        SearchTrainerCondition condition =
                new SearchTrainerCondition(
                        "trainer",
                        0,
                        10
                );

        SearchTrainerListResult expectedResult =
                new SearchTrainerListResult(
                        List.of(
                                new SearchTrainerResult(
                                        7L,
                                        "홍길동",
                                        "trainer01@test.com",
                                        "운동왕"
                                ),
                                new SearchTrainerResult(
                                        8L,
                                        "김철수",
                                        "trainer02@test.com",
                                        "헬스왕"
                                )
                        ),
                        0,
                        10,
                        false
                );

        when(trainerProfileSearchQueryPort.searchTrainers(condition))
                .thenReturn(expectedResult);

        // when
        SearchTrainerListResult result =
                service.searchTrainers(condition);

        // then
        assertThat(result.content()).hasSize(2);
        assertThat(result.content().get(0).trainerProfileId())
                .isEqualTo(7L);
        assertThat(result.content().get(0).username())
                .isEqualTo("trainer01@test.com");

        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.hasNext()).isFalse();

        verify(trainerProfileSearchQueryPort)
                .searchTrainers(condition);
    }
}