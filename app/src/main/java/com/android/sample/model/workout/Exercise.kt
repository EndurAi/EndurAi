package com.android.sample.model.workout

abstract class Exercise(
    val exId: String,
    val exType: ExerciseType,
    val exDetail: ExerciseDetail
) {}

interface ExerciseType {
}