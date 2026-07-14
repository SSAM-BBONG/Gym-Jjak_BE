package com.ssambbong.gymjjak.exercise.application.port.out;

public interface ExerciseCacheEvictionPort {

    void evictExerciseList();

    void evictExerciseSnapshots();
}
