package com.ssambbong.gymjjak.calendar.adapter.out.persistence;

import com.ssambbong.gymjjak.global.infrastructure.presentation.BaseCreatedUpdatedEntity;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "workout_diaries")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class WorkoutDiaryJpaEntity extends BaseCreatedUpdatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "part", nullable = false, length = 30)
    private PartType part;

    @Column(name = "exercise", nullable = false, length = 100)
    private String exercise;

    @Column(name = "diary_date", nullable = false)
    private LocalDate diaryDate;

    @OrderBy("setOrder ASC")
    @OneToMany(
            mappedBy = "workoutDiary",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<WorkoutDiarySetJpaEntity> sets = new ArrayList<>();

    public WorkoutDiaryJpaEntity(
            Long userId,
            LocalDate diaryDate,
            PartType part,
            String exercise,
            List<WorkoutDiarySetJpaEntity> sets
    ) {
        this.userId = userId;
        this.diaryDate = diaryDate;
        this.part = part;
        this.exercise = exercise;
        replaceSets(sets);
    }

    public void update(
            PartType part,
            String exercise,
            List<WorkoutDiarySetJpaEntity> sets
    ) {
        this.part = part;
        this.exercise = exercise;
        replaceSets(sets);
    }

    private void replaceSets(List<WorkoutDiarySetJpaEntity> newSets) {
        this.sets.clear();
        for (WorkoutDiarySetJpaEntity set : newSets) {
            addSet(set);
        }
    }

    private void addSet(WorkoutDiarySetJpaEntity set) {
        this.sets.add(set);
        set.assignTo(this);
    }
}
