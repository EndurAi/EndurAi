package com.android.sample.mlUtils

import MathsPoseDetection
import android.util.Log
import com.android.sample.mlUtils.exercisesCriterions.AngleCriterionComments
import com.android.sample.mlUtils.exercisesCriterions.ChairCriterions
import com.android.sample.mlUtils.exercisesCriterions.DownwardDogCriterions
import com.android.sample.mlUtils.exercisesCriterions.PlankExerciseCriterions
import com.android.sample.mlUtils.exercisesCriterions.PushUpsDownCrierions
import com.android.sample.mlUtils.exercisesCriterions.PushUpsUpCrierions
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

    data class AngleCriterion(
        val joints: Triple<Int, Int, Int>,
        val targetAngle: Double,
        val delta: Double,
        val combination : Boolean = false,
        val onSuccess: () -> Unit,
        val onFailure: () -> Unit,
      val failCorrectionComment: AngleCriterionComments = AngleCriterionComments.NOT_IMPLEMENTED,
      val successCorrectionComment : AngleCriterionComments = AngleCriterionComments.SUCCESS,
      val LR_FailComment : AngleCriterionComments = AngleCriterionComments.NOT_IMPLEMENTED, //Comment when the L and the R side of a joint are failed
      
    )

    data class ExerciseCriterion(val angleCriterionSet: Set<Pair<AngleCriterion, AngleCriterion>>)

    /**
     * Asses the landmarks to the given angle criterion
     * @return a Boolean stating that all the angle criterion are fulfilled and a list of correcting comment
     */
    fun assessLandMarks(
        poseLandmarkList: List<Triple<Float, Float, Float>>,
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
            val jointL = Triple(a_l, b_l, c_l)
            val jointR = Triple(a_r, b_r, c_r)
            val resultL =
                angleEqualsTo(jointL, angleCriterionL.targetAngle, delta = angleCriterionL.delta)
            val resultR =
                angleEqualsTo(jointR, angleCriterionR.targetAngle, delta = angleCriterionR.delta)

            //NOTE : Only one comment is sent at a time !
            if (angleCriterionR.combination && angleCriterionL.combination){ //Both left and right part should be OK
              if (resultR && resultL){
                angleCriterionL.onSuccess()
                angleCriterionL.onSuccess()
                listOfComments.add(angleCriterionR.successCorrectionComment)
                //listOfComments.add(angleCriterionL.successCorrectionComment)
              }
              else{
                angleCriterionL.onFailure()
                angleCriterionR.onFailure()
                listOfComments.add(angleCriterionL.LR_FailComment)
                //listOfComments.add(angleCriterionR.failCorrectionComment)
              }
            }//If only one part is sufficient
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
      return Pair(exerciseSuccess,listOfComments.toList())
    }

    /* fun assessLandMarks(poseLandmarkList : List<PoseLandmark>, exerciseCriterion : ExerciseCriterion) : Boolean{
          Log.d("MLFeedback", "-----------------------------------")
          val listOfBoolean = exerciseCriterion.angleCriterionSet.map { (angleCriterionL,angleCriterionR) ->
            val a_l =MathsPoseDetection.pointFToTriple( poseLandmarkList[angleCriterionL.joints.first].position3D)
            val b_l = MathsPoseDetection.pointFToTriple( poseLandmarkList[angleCriterionL.joints.second].position3D)
            val c_l = MathsPoseDetection.pointFToTriple( poseLandmarkList[angleCriterionL.joints.third].position3D)
            val a_r = MathsPoseDetection.pointFToTriple( poseLandmarkList[angleCriterionR.joints.first].position3D)
            val b_r = MathsPoseDetection.pointFToTriple( poseLandmarkList[angleCriterionR.joints.second].position3D)
            val c_r = MathsPoseDetection.pointFToTriple( poseLandmarkList[angleCriterionR.joints.third].position3D)
            val jointL = Triple(a_l,b_l,c_l)
            val jointR = Triple(a_r,b_r,c_r)
            val resultL = angleEqualsTo(jointL,angleCriterionL.targetAngle, delta = angleCriterionL.delta)
            val resultR = angleEqualsTo(jointR,angleCriterionR.targetAngle, delta = angleCriterionR.delta)

            if (resultL){
              angleCriterionL.onSuccess()
            }
            else if (resultR){
              angleCriterionR.onSuccess()

            }
            else{
              angleCriterionL.onFailure()
              angleCriterionR.onFailure()
            }

            resultL ||resultR
          }
          Log.d("MLFeedback", "-----------------------------------")

          return listOfBoolean.all { b -> b } //Checks that all are valid

        }
    */

    fun preambleCriterion(exerciseCriterion: ExerciseCriterion,onSuccess: () -> Unit,onFailure: () -> Unit): ExerciseCriterion {
      val preambleCriterion =
          exerciseCriterion.angleCriterionSet.map { (angleCriterionL, angleCriterionR) ->
           AngleCriterion(
               joints = angleCriterionL.joints,
                targetAngle = angleCriterionL.targetAngle,
                delta = angleCriterionL.delta*1.5,
                onSuccess = onSuccess,
                onFailure = onFailure
           ) to AngleCriterion(
               joints = angleCriterionR.joints,
               targetAngle = angleCriterionR.targetAngle,
               delta = angleCriterionR.delta*1.5,
               onSuccess = onSuccess,
               onFailure = onFailure
           )
          }
      return ExerciseCriterion(preambleCriterion.toSet())
    }
      fun getCriterions(exerciseType: ExerciseType): List<ExerciseCriterion> {
          val ret = when (exerciseType) {
              ExerciseType.DOWNWARD_DOG -> listOf(DownwardDogCriterions)
              ExerciseType.TREE_POSE -> TODO()
              ExerciseType.SUN_SALUTATION -> TODO()
              ExerciseType.WARRIOR_II -> TODO()
              ExerciseType.PUSH_UPS -> listOf(PushUpsUpCrierions, PushUpsDownCrierions)
              ExerciseType.SQUATS -> TODO()
              ExerciseType.PLANK -> listOf(PlankExerciseCriterions)
              ExerciseType.CHAIR -> listOf(ChairCriterions)
              ExerciseType.JUMPING_JACKS -> TODO()
              ExerciseType.LEG_SWINGS -> TODO()
              ExerciseType.ARM_CIRCLES -> TODO()
              ExerciseType.ARM_WRIST_CIRCLES -> TODO()
          }
            return ret
      }
  }
}
