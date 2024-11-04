package com.android.sample.model.workout

data class Exercise(val id: String, val type: ExerciseType, val detail: ExerciseDetail) {}

enum class ExerciseType(val workoutType: WorkoutType) {
  DOWNWARD_DOG(WorkoutType.YOGA),
  TREE_POSE(WorkoutType.YOGA),
  SUN_SALUTATION(WorkoutType.YOGA),
  WARRIOR_II(WorkoutType.YOGA),
  PUSH_UPS(WorkoutType.BODY_WEIGHT),
  SQUATS(WorkoutType.BODY_WEIGHT),
  PLANK(WorkoutType.BODY_WEIGHT),
  CHAIR(WorkoutType.BODY_WEIGHT),
  JUMPING_JACKS(WorkoutType.WARMUP),
  LEG_SWINGS(WorkoutType.WARMUP),
  ARM_CIRCLES(WorkoutType.WARMUP),
  ARM_WRIST_CIRCLES(WorkoutType.WARMUP);

  fun getInstruction(): String {
    return when (this) {
      DOWNWARD_DOG -> "Hands shoulder-width, feet hip-width, form an inverted V."
      TREE_POSE -> "Balance on one leg, other foot on inner thigh."
      SUN_SALUTATION -> "Flowing sequence of poses, coordinating breath."
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
