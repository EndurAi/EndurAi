package com.android.sample.mlUtils

import MathsPoseDetection
import android.util.Log
import com.android.sample.mlUtils.exercisesCriterions.AngleCriterionComments
import com.android.sample.mlUtils.exercisesCriterions.ChairCriterions
import com.android.sample.mlUtils.exercisesCriterions.DownwardDogCriterions
import com.android.sample.mlUtils.exercisesCriterions.JumpingJacksClosedCriterions
import com.android.sample.mlUtils.exercisesCriterions.JumpingJacksOpenCriterions
import com.android.sample.mlUtils.exercisesCriterions.PlankExerciseCriterions
import com.android.sample.mlUtils.exercisesCriterions.PushUpsDownCriterions
import com.android.sample.mlUtils.exercisesCriterions.PushUpsUpCrierions
import com.android.sample.mlUtils.exercisesCriterions.Warrior_2_LEFT_Criterions
import com.android.sample.mlUtils.exercisesCriterions.Warrior_2_RIGHT_Criterions
import com.android.sample.model.workout.ExerciseType
import kotlin.math.abs

class ExerciseFeedBack {
  companion object {
    /**
     * Checks if the angle formed by three pose landmarks is approximately equal to a target angle.
     *
     * @param poses A `Triple` containing three `Float` objects representing the points of the
     *   angle.
     * @param target The target angle in degrees.
     * @param delta The allowable deviation from the target angle.
     * @return `true` if the calculated angle is within the allowable deviation from the target
     *   angle, `false` otherwise.
     */
    fun angleEqualsTo(
        poses:
            Triple<
                Triple<Float, Float, Float>,
                Triple<Float, Float, Float>,
                Triple<Float, Float, Float>>,
        target: Double,
        delta: Double = 0.0
    ): Boolean {
      val a = poses.first
      val b = poses.second
      val c = poses.third
      val angle = MathsPoseDetection.angle(a, b, c)
      Log.d("MLFEEDBACK", "angleEqualsTo: actual = $angle target = $target")
      return abs(target - angle) <= delta || abs(target - angle + 180) <= delta
    }
    /**
     * Data class representing an angle criterion for an exercise.
     *
     * @property joints A `Triple` containing three integers representing the indices of the joints.
     * @property targetAngle The target angle in degrees.
     * @property delta The allowable deviation from the target angle.
     * @property combination A boolean indicating if both left and right parts should be OK.
     * @property onSuccess A callback function to be invoked on success.
     * @property onFailure A callback function to be invoked on failure.
     * @property failCorrectionComment A comment to be provided when the criterion fails.
     * @property successCorrectionComment A comment to be provided when the criterion succeeds.
     * @property LR_FailComment A comment to be provided when both left and right sides of a joint
     *   fail.
     */
    data class AngleCriterion(
        val joints: Triple<Int, Int, Int>,
        val targetAngle: Double,
        val delta: Double,
        val combination: Boolean = false,
        val onSuccess: () -> Unit,
        val onFailure: () -> Unit,
        val failCorrectionComment: AngleCriterionComments = AngleCriterionComments.NOT_IMPLEMENTED,
        val successCorrectionComment: AngleCriterionComments = AngleCriterionComments.SUCCESS,
        val LR_FailComment: AngleCriterionComments =
            AngleCriterionComments
                .NOT_IMPLEMENTED // Comment when the L and the R side of a joint are failed
    )

    /**
     * Data class representing the criteria for an exercise.
     *
     * @property angleCriterionSet A set of pairs of `AngleCriterion` objects representing the
     *   criteria for the exercise.
     * @property symmetric A boolean indicating if the exercise is symmetric.
     * @property criterionName name of the criterion.
     * @property exerciseName name of the exercise.
     * @property isCommented whether the coach should return a comment based on result of the
     *   criteria
     */
    data class ExerciseCriterion(
        val angleCriterionSet: Set<Pair<AngleCriterion, AngleCriterion>>,
        val symmetric: Boolean = true,
        val criterionName: String,
        val exerciseName: String,
        val isCommented: Boolean = true
    )
    /**
     * Asses the landmarks to the given angle criterion
     *
     * @return a Boolean stating that all the angle criterion are fulfilled and a list of correcting
     *   comment
     */
    fun assessLandMarks(
        poseLandmarkList: List<MyPoseLandmark>,
        exerciseCriterion: ExerciseCriterion
    ): Pair<Boolean, List<AngleCriterionComments>> {
      Log.d("MLFeedback", "-----------------------------------")
      val listOfComments = mutableListOf<AngleCriterionComments>()
      val listOfBoolean =
          exerciseCriterion.angleCriterionSet.map { (angleCriterionL, angleCriterionR) ->
            val a_l = poseLandmarkList[angleCriterionL.joints.first]
            val b_l = poseLandmarkList[angleCriterionL.joints.second]
            val c_l = poseLandmarkList[angleCriterionL.joints.third]
            val a_r = poseLandmarkList[angleCriterionR.joints.first]
            val b_r = poseLandmarkList[angleCriterionR.joints.second]
            val c_r = poseLandmarkList[angleCriterionR.joints.third]
            val jointL = Triple(a_l.toTriple(), b_l.toTriple(), c_l.toTriple())
            val jointR = Triple(a_r.toTriple(), b_r.toTriple(), c_r.toTriple())
            val resultL =
                angleEqualsTo(jointL, angleCriterionL.targetAngle, delta = angleCriterionL.delta)
            val resultR =
                angleEqualsTo(jointR, angleCriterionR.targetAngle, delta = angleCriterionR.delta)

            // NOTE : Only one comment is sent at a time !
            if (angleCriterionR.combination &&
                angleCriterionL.combination) { // Both left and right part should be OK
              if (resultR && resultL) {
                angleCriterionL.onSuccess()
                angleCriterionL.onSuccess()
                listOfComments.add(angleCriterionR.successCorrectionComment)
                // listOfComments.add(angleCriterionL.successCorrectionComment)
              } else {
                angleCriterionL.onFailure()
                angleCriterionR.onFailure()
                listOfComments.add(angleCriterionL.LR_FailComment)
                // listOfComments.add(angleCriterionR.failCorrectionComment)
              }
            } // If only one part is sufficient
            else if (resultL) {
              angleCriterionL.onSuccess()
              listOfComments.add(angleCriterionL.successCorrectionComment)
            } else if (resultR) {
              angleCriterionR.onSuccess()
              listOfComments.add(angleCriterionR.successCorrectionComment)
            } else {
              angleCriterionL.onFailure()
              angleCriterionR.onFailure()
              listOfComments.add(angleCriterionL.failCorrectionComment)
            }

            resultL || resultR
          }
      Log.d("MLFeedback", "-----------------------------------")

      val exerciseSuccess = listOfBoolean.all { b -> b }
      return Pair(exerciseSuccess, listOfComments.toList())
    }

    /**
     * Creates a preamble criterion by adjusting the delta of the given exercise criterion.
     *
     * @param exerciseCriterion The original exercise criterion to be adjusted.
     * @param onSuccess A callback function to be invoked on success.
     * @param onFailure A callback function to be invoked on failure.
     * @return A new `ExerciseCriterion` with adjusted delta values.
     */
    fun preambleCriterion(
        exerciseCriterion: ExerciseCriterion,
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {}
    ): ExerciseCriterion {
      val mult = 2f
      val preambleCriterion =
          exerciseCriterion.angleCriterionSet.map { (angleCriterionL, angleCriterionR) ->
            AngleCriterion(
                joints = angleCriterionL.joints,
                targetAngle = angleCriterionL.targetAngle,
                delta = angleCriterionL.delta * mult,
                onSuccess = onSuccess,
                onFailure = onFailure) to
                AngleCriterion(
                    joints = angleCriterionR.joints,
                    targetAngle = angleCriterionR.targetAngle,
                    delta = angleCriterionR.delta * mult,
                    onSuccess = onSuccess,
                    onFailure = onFailure)
          }
      return ExerciseCriterion(
          preambleCriterion.toSet(),
          criterionName = exerciseCriterion.criterionName,
          exerciseName = exerciseCriterion.exerciseName)
    }

    /**
     * Retrieves the exercise criteria based on the given exercise type.
     *
     * @param exerciseType The type of exercise for which to get the criteria.
     * @return A list of `ExerciseCriterion` objects corresponding to the given exercise type.
     */
    fun getCriterions(exerciseType: ExerciseType): List<ExerciseCriterion> {
      val ret =
          when (exerciseType) {
            ExerciseType.DOWNWARD_DOG -> listOf(DownwardDogCriterions)
            ExerciseType.TREE_POSE -> TODO()
            ExerciseType.WARRIOR_II -> listOf(Warrior_2_RIGHT_Criterions, Warrior_2_LEFT_Criterions)
            ExerciseType.PUSH_UPS -> listOf(PushUpsUpCrierions, PushUpsDownCriterions)
            ExerciseType.SQUATS -> TODO()
            ExerciseType.PLANK -> listOf(PlankExerciseCriterions)
            ExerciseType.CHAIR -> listOf(ChairCriterions)
            ExerciseType.JUMPING_JACKS ->
                listOf(JumpingJacksOpenCriterions, JumpingJacksClosedCriterions)
            ExerciseType.LEG_SWINGS -> TODO()
            ExerciseType.ARM_CIRCLES -> TODO()
            ExerciseType.ARM_WRIST_CIRCLES -> TODO()
            ExerciseType.UPWARD_FACING_DOG -> TODO()
          }
      return ret
    }
  }
}
