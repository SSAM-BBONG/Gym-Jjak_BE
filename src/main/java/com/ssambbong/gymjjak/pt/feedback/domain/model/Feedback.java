package com.ssambbong.gymjjak.pt.feedback.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Feedback {

    private final Long id;
    private final Long ptReservationId;
    private final Long ptCurriculumId;
    private final Long trainerProfileId;
    private final Long userId;
    private final String content;
    private final String status;
    private final LocalDateTime createdAt;


    public static Feedback create(Long ptReservationId, Long ptCurriculumId, Long trainerProfileId, Long userId, String content)
    {
        return Feedback.builder()
                .ptReservationId(ptReservationId)
                .ptCurriculumId(ptCurriculumId)
                .trainerProfileId(trainerProfileId)
                .userId(userId)
                .content(content)
                .status("ACTIVE")
                .build();
    }

    // DB 복원 시
    public static Feedback restore(Long id, Long ptReservationId, Long ptCurriculumId,
                                   Long trainerProfileId, Long userId, String content,
                                   String status, LocalDateTime createdAt)
    {
        return Feedback.builder()
                .id(id)
                .ptReservationId(ptReservationId)
                .ptCurriculumId(ptCurriculumId)
                .trainerProfileId(trainerProfileId)
                .userId(userId)
                .content(content)
                .status(status)
                .createdAt(createdAt)
                .build();
    }
}





