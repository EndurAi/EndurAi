package com.android.sample.model.workout

class WarmUpViewModel(val repository: WorkoutRepository<WarmUp>) :
    WorkoutViewModel<WarmUp>(repository) {

  private val default_exercise_id = "default"
  private val DEFAULT_WARM_UP_EXERCISES: MutableList<Exercise> =
      mutableListOf(
          Exercise(
              default_exercise_id,
              ExerciseType.LEG_SWINGS,
              ExerciseDetail.RepetitionBased(15)),
          Exercise(
              default_exercise_id,
              ExerciseType.JUMPING_JACKS,
              ExerciseDetail.RepetitionBased(25)),
          Exercise(
              default_exercise_id, ExerciseType.ARM_CIRCLES, ExerciseDetail.TimeBased(30, 2)),
          Exercise(
              default_exercise_id,
              ExerciseType.ARM_WRIST_CIRCLES,
              ExerciseDetail.TimeBased(15, 2)))

  val DEFAULT_WARMUP: WarmUp =
      WarmUp(
          workoutId = "default",
          name = "Default Warmup",
          userIdSet = mutableSetOf(),
          exercises = DEFAULT_WARM_UP_EXERCISES,
          description = "This is the default warmup",
      )

  init {
    repository.init { getWorkouts() }
    if (workouts_.value.isEmpty()) {
      selectWorkout(DEFAULT_WARMUP)
    }
  }
}
