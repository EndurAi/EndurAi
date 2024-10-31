package com.android.sample.model.workout

data class Exercise(val id: String, val type: ExerciseType, val detail: ExerciseDetail) {}

enum class ExerciseType(val workoutType : WorkoutType)  {
  DOWNWARD_DOG(WorkoutType.YOGA),
  TREE_POSE(WorkoutType.YOGA),
  SUN_SALUTATION(WorkoutType.YOGA),
  WARRIOR_II(WorkoutType.YOGA),
  PUSH_UPS(WorkoutType.BODY_WEIGHT),
  SQUATS(WorkoutType.BODY_WEIGHT),
  PLANK(WorkoutType.BODY_WEIGHT),
  CHAIR(WorkoutType.BODY_WEIGHT),
  JUMPING_JACKS(WorkoutType.WARMUP),
  LEG_SWINGS(WorkoutType.BODY_WEIGHT),
  ARM_CIRCLES(WorkoutType.WARMUP),
  ARM_WRIST_CIRCLES(WorkoutType.WARMUP);






  override fun toString(): String {
    return when (this) {
      DOWNWARD_DOG -> "Downward Dog"
      TREE_POSE -> "Tree Pose"
      SUN_SALUTATION -> "Sun Salutation"
      WARRIOR_II -> "Warrior 2 Pose"
      PUSH_UPS -> "Push-ups"
      SQUATS -> "Squats"
      PLANK -> "Plank"
      CHAIR -> "Chair"
      JUMPING_JACKS -> "Jumping-jacks"
      LEG_SWINGS -> "Leg swings"
      ARM_CIRCLES -> "Arm circles"
      ARM_WRIST_CIRCLES -> "Arm wrist circle"
    }
  }
}

