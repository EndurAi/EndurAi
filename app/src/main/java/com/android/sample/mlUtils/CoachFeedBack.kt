package com.android.sample.mlUtils

enum class ExerciseFeedBackUnit(val valuePrefix: String, val stringRepresentation: String) {
  REPETITION("Counts", "Rep."),
  SECONDS("Duration", "s")
}

/**
 * Data class representing feedback from a coach for an exercise.
 *
 * @property commentSet A set of `JointFeedback` objects containing comments and ratings for joints.
 * @property successRate The success rate of the exercise.
 * @property feedbackValue The number of repetitions or duration of the exercise.
 * @property feedbackUnit The unit for repetitions or duration (e.g., "s" for seconds).
 * @property exerciseCriterion The criteria for the exercise.
 */
data class CoachFeedback(
    val commentSet: Set<JointFeedback>,
    val successRate: Float,
    val feedbackValue: Int,
    val feedbackUnit: ExerciseFeedBackUnit,
    val exerciseCriterion: ExerciseFeedBack.Companion.ExerciseCriterion
) {
  /**
   * Converts the `CoachFeedback` object to a string representation.
   *
   * @return A string representation of the `CoachFeedback` object.
   */
  override fun toString(): String {
    val stringBuilder: StringBuilder = StringBuilder()
    stringBuilder.append(exerciseCriterion.name).append("\n")
    commentSet
        .filter { it.rate >= 0.15F }
        .forEach { comment -> stringBuilder.append(comment.comment).append("\n") }

    stringBuilder.append(
        "${feedbackUnit.valuePrefix}: $feedbackValue ${feedbackUnit.stringRepresentation}\n")

    return stringBuilder.toString()
  }
}

/**
 * Data class representing feedback for a specific joint.
 *
 * @property comment A comment about the joint.
 * @property rate A rating for the joint.
 */
data class JointFeedback(val comment: String = "", val rate: Float = 0F) {
  override fun toString(): String {
    return comment
  }
}
