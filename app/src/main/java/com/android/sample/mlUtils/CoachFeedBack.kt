package com.android.sample.mlUtils

data class CoachFeedback(val commentSet: Set<JointFeedback>, val successRate: Float, val repOrDuration: Int, val repOrDurationUnit: String, val exerciseCriterion : ExerciseFeedBack.Companion.ExerciseCriterion)

data class JointFeedback(val comment : String = "", val rate : Float = 0F)