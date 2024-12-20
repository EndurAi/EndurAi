package com.android.sample.model.workout

data class Exercise(val id: String, val type: ExerciseType, val detail: ExerciseDetail) {}

enum class ExerciseType(
    val workoutType: WorkoutType,
    val detail: ExerciseDetail,
    val hasMlFeedback: Boolean = false
) {
  DOWNWARD_DOG(
      WorkoutType.YOGA, detail = ExerciseDetail.TimeBased(30, sets = 3), hasMlFeedback = true),
  TREE_POSE(WorkoutType.YOGA, detail = ExerciseDetail.TimeBased(30, sets = 3)),
  UPWARD_FACING_DOG(WorkoutType.YOGA, detail = ExerciseDetail.TimeBased(60, sets = 2)),
  WARRIOR_II(
      WorkoutType.YOGA, detail = ExerciseDetail.TimeBased(30, sets = 3), hasMlFeedback = true),
  PUSH_UPS(
      WorkoutType.BODY_WEIGHT, detail = ExerciseDetail.RepetitionBased(10), hasMlFeedback = true),
  SQUATS(WorkoutType.BODY_WEIGHT, detail = ExerciseDetail.RepetitionBased(15)),
  PLANK(
      WorkoutType.BODY_WEIGHT,
      detail = ExerciseDetail.TimeBased(60, sets = 1),
      hasMlFeedback = true),
  CHAIR(
      WorkoutType.BODY_WEIGHT,
      detail = ExerciseDetail.TimeBased(60, sets = 1),
      hasMlFeedback = true),
  JUMPING_JACKS(
      WorkoutType.WARMUP, detail = ExerciseDetail.RepetitionBased(20), hasMlFeedback = true),
  LEG_SWINGS(WorkoutType.WARMUP, detail = ExerciseDetail.RepetitionBased(15)),
  ARM_CIRCLES(WorkoutType.WARMUP, detail = ExerciseDetail.RepetitionBased(20)),
  ARM_WRIST_CIRCLES(WorkoutType.WARMUP, detail = ExerciseDetail.RepetitionBased(20));

  fun getInstruction(): String {
    return when (this) {
      DOWNWARD_DOG -> "Hands shoulder-width, feet hip-width, form an inverted V."
      TREE_POSE -> "Balance on one leg, other foot on inner thigh."
      UPWARD_FACING_DOG -> "Lie face down, lift chest and thighs off floor, straight arms."
      WARRIOR_II -> "Lunge pose, arms parallel to the floor."
      PUSH_UPS -> "Lower chest to floor, push back up with arms."
      SQUATS -> "Stand with feet apart, lower hips as if sitting."
      PLANK -> "Hold body straight, forearms and toes on floor."
      CHAIR -> "Lean against wall, thighs parallel to floor."
      JUMPING_JACKS -> "Jump, spread legs and clap arms overhead."
      LEG_SWINGS -> "Swing leg forward and backward, keeping it straight."
      ARM_CIRCLES -> "Rotate arms in circles forwards and backwards."
      ARM_WRIST_CIRCLES -> "Rotate wrists in circles forwards and backwards."
    }
  }

  override fun toString(): String {
    return when (this) {
      DOWNWARD_DOG -> "Downward Dog"
      TREE_POSE -> "Tree Pose"
      UPWARD_FACING_DOG -> "Sun Salutation"
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
