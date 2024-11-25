package com.android.sample.utils

import MathsPoseDetection
import android.util.Log
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.abs

class ExerciseFeedBack {
  companion object{
/**
     * Checks if the angle formed by three pose landmarks is approximately equal to a target angle.
     *
     * @param poses A `Triple` containing three `Float` objects representing the points of the angle.
     * @param target The target angle in degrees.
     * @param delta The allowable deviation from the target angle.
     * @return `true` if the calculated angle is within the allowable deviation from the target angle, `false` otherwise.
     */
    fun angleEqualsTo(poses: Triple<Pair<Float,Float>,Pair<Float,Float>, Pair<Float,Float>>, target: Double, delta: Double = 0.0): Boolean {
        val a = poses.first
        val b = poses.second
        val c = poses.third
        val angle = MathsPoseDetection.angle(a, b, c)
        return abs(target - angle) <= delta
    }

    data class AngleCriterion(val joints : Triple<Int,Int,Int>, val targetAngle : Double, val delta: Double, val onSuccess : ()->Unit,val onFailure : ()->Unit)


    data class ExerciseCriterion(val angleCriterionSet : Set<AngleCriterion>)

//SHOULDER HIP KNEE
    val plankCriterion_backAngle_L = AngleCriterion(
      joints = PoseDetectionJoints.LEFT_SHOULDER_HIP_KNEE,
      targetAngle = 180.0,
      delta = 25.0
      , onSuccess = {
        Log.d(
          "MLFeedback",
          "Back is good"
        )},
      onFailure = {        Log.d(
        "MLFeedback",
        "Don't bend your back"
      )}
    )

    val plankCriterion_backAngle_R = AngleCriterion(
      joints = PoseDetectionJoints.RIGHT_SHOULDER_HIP_KNEE,
      targetAngle = 180.0,
      delta = 25.0
      , onSuccess = {
        Log.d(
          "MLFeedback",
          "SHOULDER HIP KNEE is good"
        )},
      onFailure = {        Log.d(
        "MLFeedback",
        "SHOULDER HIP KNEE pas cool"
      )}
    )

    // HIP SHOULDER ELBOW
    val plankCriterion_SHOULDER_L = AngleCriterion(
      joints = PoseDetectionJoints.LEFT_SHOULDER_HIP_KNEE,
      targetAngle = 90.0,
      delta = 15.0
      , onSuccess = {
        Log.d(
          "MLFeedback",
          "HIP SHOULDER ELBOW is good"
        )},
      onFailure = {        Log.d(
        "MLFeedback",
        "HIP SHOULDER ELBOW pas cool"
      )}
    )
    // HIP SHOULDER ELBOW
    val plankCriterion_SHOULDER_R = AngleCriterion(
      joints = PoseDetectionJoints.RIGHT_SHOULDER_HIP_KNEE,
      targetAngle = 90.0,
      delta = 15.0
      , onSuccess = {
        Log.d(
          "MLFeedback",
          "HIP SHOULDER ELBOW is good"
        )},
      onFailure = {        Log.d(
        "MLFeedback",
        "HIP SHOULDER ELBOW pas cool"
      )}
    )
    //PlankExerciseCriterion
    val PlankExerciseCriterion : ExerciseCriterion = ExerciseCriterion(angleCriterionSet = setOf(
      plankCriterion_SHOULDER_R,
      plankCriterion_SHOULDER_L,
      plankCriterion_backAngle_L,
      plankCriterion_SHOULDER_R
    ))


    fun assessLandMarks(landMarkList : List<PoseLandmark>, )











  }
}