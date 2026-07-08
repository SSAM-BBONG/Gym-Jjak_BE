package com.ssambbong.gymjjak.calendar.adapter.out.persistence;

import com.ssambbong.gymjjak.global.infrastructure.presentation.BaseCreatedUpdatedEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "workout_diaries",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_workout_diaries_user_date",
                        columnNames = {"user_id", "diary_date"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class WorkoutDiaryJpaEntity extends BaseCreatedUpdatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workout_diary_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "feedback_id")
    private Long feedbackId;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "diary_date", nullable = false)
    private LocalDate diaryDate;

    public WorkoutDiaryJpaEntity(
            Long userId,
            String title,
            String content,
            LocalDate diaryDate
    ) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.diaryDate = diaryDate;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
