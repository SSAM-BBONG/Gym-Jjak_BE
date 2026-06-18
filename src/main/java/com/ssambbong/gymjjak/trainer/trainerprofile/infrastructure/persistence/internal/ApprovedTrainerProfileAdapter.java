package com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.internal;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.command.CreateApprovedTrainerProfileCommand;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out.ApprovedTrainerProfilePort;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerAward;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerCertification;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfile;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.repository.TrainerAwardRepository;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.repository.TrainerCertificationRepository;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.repository.TrainerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Component
@RequiredArgsConstructor
public class ApprovedTrainerProfileAdapter implements ApprovedTrainerProfilePort {

    // 필수자격증 이름
    private static final String REQUIRED_CERTIFICATION_NAME = "생활스포츠지도사";

    private final TrainerProfileRepository trainerProfileRepository;
    private final TrainerCertificationRepository trainerCertificationRepository;
    private final TrainerAwardRepository trainerAwardRepository;

    @Override
    public Long createApprovedTrainerProfile(CreateApprovedTrainerProfileCommand command) {

        TrainerProfile savedProfile = trainerProfileRepository.save(
                TrainerProfile.create(
                        command.userId(),
                        command.trainerApplicationId(),
                        command.profileFileId(),
                        command.trainerName(),
                        command.introduction()
                )
        );
        // 생성된 트레이너 프로필 id 저장
        Long trainerProfileId = savedProfile.getTrainerProfileId();

        // 트레이너 자격증 테이블 저장
        trainerCertificationRepository.saveAll(
                createCertifications(
                        trainerProfileId,
                        command.certificateFileId(),
                        command.qualifications()
                )
        );
        // 트레이너 수상경력 테이블 저장
        trainerAwardRepository.saveAll(
                createAwards(trainerProfileId, command.awardHistories())
        );

        return trainerProfileId;
    }

    // 수상경력 insert
    private List<TrainerAward> createAwards(Long trainerProfileId, List<String> awardHistories) {

        List<String> safeAwardHistories =
                awardHistories == null ? List.of() : awardHistories;

        return safeAwardHistories.stream()
                .filter(Objects::nonNull) // null 제거
                .map(String::trim) // 공백 제거
                .filter(name -> !name.isBlank()) // 빈 목록 제거
                .distinct() // 동일 항목 제거
                .map(name -> TrainerAward.create(trainerProfileId, name)) // 도메인 객체로 젼경
                .toList();
    }

    // 자격증 insert
    private List<TrainerCertification> createCertifications(
            Long trainerProfileId, Long certificateFileId, List<String> qualifications
    ) {
        List<String> safeQualifications =
                qualifications == null ? List.of() : qualifications;

        List<TrainerCertification> certifications = new ArrayList<>();

        // 필수 자격증 생성 (필수자격증을 먼저 추가함)
        certifications.add(
                TrainerCertification.required(
                        trainerProfileId,
                        REQUIRED_CERTIFICATION_NAME,
                        certificateFileId
                )
        );

        safeQualifications.stream()// 전달받은 자격증 list 순회
                .filter(Objects::nonNull) // list 속 null 있으면 제거
                .map(String::trim) // 문자열 공백 제거
                .filter(name -> !name.isBlank()) // 공백 자격증은 제거
                .filter(name -> !name.contains(REQUIRED_CERTIFICATION_NAME)) // 추가 자격증 속 필수 자격증명 제거
                .distinct() // 중복 자격증명 제거
                .map(name -> TrainerCertification.additional(trainerProfileId, name)) // ADDITIONAL 타입 도메인으로 변환
                .forEach(certifications::add); // 변환딘 추가 자격증들을 리스트에 추가

        return List.copyOf(certifications);
    }
}
