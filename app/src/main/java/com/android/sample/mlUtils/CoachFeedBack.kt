package com.android.sample.mlUtils

data class CoachFeedback(val commentSet : Set<JointFeedback>, val successRate: Float, val repOrDuration : Long, val repOrDurationUnit : String)

data class JointFeedback(val comment : String = "", val rate : Float = 0F)