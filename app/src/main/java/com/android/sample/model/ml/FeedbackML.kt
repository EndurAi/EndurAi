package com.android.sample.model.ml

/**
 * Abstract base class for feedback
 *
 * @property exerciseName The name of the exercise
 * @property duration Duration in seconds
 * @property numberOfRepetitions Number of repetitions
 * @property accuracyScore Percentage of how well it was performed
 */
abstract class ExerciseFeedback(
    open val exerciseName: String,
    open val duration: Double,
    open val numberOfRepetitions: Int,
    open val accuracyScore: Double
)

/**
 * Data class representing feedback for pose-related exercises.
 *
 * @property balanceScore Balance metric for poses like tree pose
 * @property alignmentFeedback Joint alignment feedback
 * @property exerciseName The name of the exercise
 * @property duration Duration in seconds
 * @property numberOfRepetitions Number of repetitions
 * @property accuracyScore Percentage of how well it was performed
 */
data class PoseFeedback(
    val balanceScore: Double? = null,
    val jointFeedback: List<JointFeedback>,
    override val exerciseName: String,
    override val duration: Double,
    override val numberOfRepetitions: Int,
    override val accuracyScore: Double
) : ExerciseFeedback(exerciseName, duration, numberOfRepetitions, accuracyScore)

/**
 * Data class representing feedback for repetition-based exercises.
 *
 * @property angleThreshold Minimum angle for valid repetition
 * @property jointFeedback Joint-specific feedback
 * @property exerciseName The name of the exercise
 * @property duration Duration in seconds
 * @property numberOfRepetitions Number of repetitions
 * @property accuracyScore Percentage of how well it was performed
 */
data class RepetitionExerciseFeedback(
    val angleThreshold: Double? = null,
    val jointFeedback: List<JointFeedback> = emptyList(),
    override val exerciseName: String,
    override val duration: Double,
    override val numberOfRepetitions: Int,
    override val accuracyScore: Double
) : ExerciseFeedback(exerciseName, duration, numberOfRepetitions, accuracyScore)

/**
 * Data class representing feedback for static hold exercises.
 *
 * @property stabilityScore Stability score for static holds
 * @property holdTime Total hold time for static exercises
 * @property exerciseName The name of the exercise
 * @property duration Duration in seconds
 * @property numberOfRepetitions Number of repetitions
 * @property accuracyScore Percentage of how well it was performed
 */
data class StaticHoldFeedback(
    val stabilityScore: Double? = null,
    val holdTime: Double? = null,
    override val exerciseName: String,
    override val duration: Double,
    override val numberOfRepetitions: Int,
    override val accuracyScore: Double
) : ExerciseFeedback(exerciseName, duration, numberOfRepetitions, accuracyScore)

/**
 * Data class representing feedback for a specific joint.
 *
 * @property jointName The name of the joint (e.g., "knee", "elbow")
 * @property feedback A brief description of the joint's performance
 * @property correctionSuggestion Optional suggestions for improvement
 */
data class JointFeedback(
    val jointName: String,
    val feedback: String,
    val correctionSuggestion: String? = null
)
