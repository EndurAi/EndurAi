package com.android.sample.model.workout

/**
 * Class handling the MET values (Metabolic equivalent of task : kcal / hour * kg) which allow
 * to compute burnt calories during an exercise with the time and weight of
 * the user
 */
object WorkoutMetValues {
    //MET constant for every exercise
    private val metValues: Map<ExerciseType, Double> = mapOf(
        ExerciseType.PUSH_UPS to 4.0,
        ExerciseType.SQUATS to 5.0,
        ExerciseType.PLANK to 5.0,
        ExerciseType.CHAIR to 4.5,
        ExerciseType.DOWNWARD_DOG to 2.5,
        ExerciseType.TREE_POSE to 2.0,
        ExerciseType.UPWARD_FACING_DOG to 3.5,
        ExerciseType.WARRIOR_II to 2.8
    )

    /**
     * Get the MET value for a giver workout type.
     */
    fun getMetValue(exerciseType: ExerciseType, defaultValue: Double = 0.0): Double {
        return metValues.getOrDefault(exerciseType, defaultValue)
    }
}