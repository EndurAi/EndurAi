package com.android.sample.mlUtils

import MathsPoseDetection
import android.util.Log
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
        val onSuccess: () -> Unit,
        val onFailure: () -> Unit
    )

    data class ExerciseCriterion(val angleCriterionSet: Set<Pair<AngleCriterion, AngleCriterion>>)

    fun assessLandMarks(
        poseLandmarkList: List<Triple<Float, Float, Float>>,
        exerciseCriterion: ExerciseCriterion
    ): Boolean {
      Log.d("MLFeedback", "-----------------------------------")
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
            if (resultL) {
              angleCriterionL.onSuccess()
            } else if (resultR) {
              angleCriterionR.onSuccess()
            } else {
              angleCriterionL.onFailure()
              angleCriterionR.onFailure()
            }

            resultL || resultR
          }
      Log.d("MLFeedback", "-----------------------------------")

      return listOfBoolean.all { b -> b } // Checks that all are valid
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

    fun preambleCriterion(criterionSet: Set<AngleCriterion>): Set<AngleCriterion> {
      val preambleCriterion =
          criterionSet.map { (joints, targetAngle, delta, onSuccess, onFailure) ->
            AngleCriterion(joints, targetAngle, 1.5 * delta, onSuccess, onFailure)
          }
      return preambleCriterion.toSet()
    }
  }
}
