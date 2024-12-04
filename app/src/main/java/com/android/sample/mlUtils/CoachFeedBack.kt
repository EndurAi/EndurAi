package com.android.sample.mlUtils

data class CoachFeedback(val commentSet: Set<JointFeedback>, val successRate: Float, val repOrDuration: Int, val repOrDurationUnit: String, val exerciseCriterion : ExerciseFeedBack.Companion.ExerciseCriterion){
  override fun toString(): String {
    val stringBuilder : StringBuilder = StringBuilder()
    stringBuilder.append(exerciseCriterion.name).append("\n")
    commentSet.filter { it.rate>=0.15F}.forEach {comment-> stringBuilder.append(comment.comment).append("\n")}
    if (repOrDurationUnit =="s"){
      stringBuilder.append("Duration: $repOrDuration seconds\n")
    }
    else{
      stringBuilder.append("Repetitions: $repOrDuration repetitions\n")
    }
    return stringBuilder.toString()
  }

}

data class JointFeedback(val comment: String = "", val rate: Float = 0F) {
    override fun toString(): String {
        return comment
    }
}