package com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out;

public interface TrainerApplicationOrganizationTrainerPort {

    // 승인된 트레이너 조직 등록 가능
    void registerApprovedTrainer(
            Long organizationId,
            Long trainerProfileId,
            Long registeredBy
    );
}
