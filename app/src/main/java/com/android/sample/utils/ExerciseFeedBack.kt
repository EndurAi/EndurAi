package com.android.sample.utils

import MathsPoseDetection
import android.graphics.PointF
import android.util.Log
import com.google.mlkit.vision.common.PointF3D
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
    fun angleEqualsTo(poses: Triple<Triple<Float,Float,Float>,Triple<Float,Float,Float>, Triple<Float,Float,Float>>, target: Double, delta: Double = 0.0): Boolean {
        val a = poses.first
        val b = poses.second
        val c = poses.third
        val angle = MathsPoseDetection.angle(a, b, c)
  Log.d("MLFEEDBACK", "angleEqualsTo: actual = $angle target = $target")
        return abs(target - angle) <= delta || abs(target - angle + 180) <= delta
    }

    data class AngleCriterion(val joints : Triple<Int,Int,Int>, val targetAngle : Double, val delta: Double, val onSuccess : ()->Unit,val onFailure : ()->Unit)


    data class ExerciseCriterion(val angleCriterionSet : Set<Pair<AngleCriterion,AngleCriterion>>)

//SHOULDER HIP KNEE
    val plankCriterion_backAngle_L = AngleCriterion(
      joints = PoseDetectionJoints.LEFT_SHOULDER_HIP_KNEE,
      targetAngle = 180.0,
      delta = 25.0
      , onSuccess = {
        Log.d(
          "MLFeedback",
          "Back is good L"
        )},
      onFailure = {        Log.d(
        "MLFeedback",
        "Don't bend your back L"
      )}
    )

    val plankCriterion_backAngle_R = AngleCriterion(
      joints = PoseDetectionJoints.RIGHT_SHOULDER_HIP_KNEE,
      targetAngle = 180.0,
      delta = 25.0
      , onSuccess = {
        Log.d(
          "MLFeedback",
          "Back is good R"
        )},
      onFailure = {        Log.d(
        "MLFeedback",
        "SHOULDER HIP KNEE pas cool R"
      )}
    )

    // HIP SHOULDER ELBOW
    val plankCriterion_SHOULDER_L = AngleCriterion(
      joints = PoseDetectionJoints.LEFT_ELBOW_SHOULDER_HIP,
      targetAngle = 90.0,
      delta = 15.0
      , onSuccess = {
        Log.d(
          "MLFeedback",
          "HIP SHOULDER ELBOW is good L"
        )},
      onFailure = {        Log.d(
        "MLFeedback",
        "HIP SHOULDER ELBOW pas cool L"
      )}
    )
    // HIP SHOULDER ELBOW
    val plankCriterion_SHOULDER_R = AngleCriterion(
      joints = PoseDetectionJoints.RIGHT_ELBOW_SHOULDER_HIP,
      targetAngle = 90.0,
      delta = 15.0
      , onSuccess = {
        Log.d(
          "MLFeedback",
          "HIP SHOULDER ELBOW is good R"
        )},
      onFailure = {        Log.d(
        "MLFeedback",
        "HIP SHOULDER ELBOW pas cool R"
      )}
    )

    // HIP KNEE ANKLE
    val plankCriterion_LEG_L = AngleCriterion(
      joints = PoseDetectionJoints.LEFT_HIP_KNEE_ANKLE,
      targetAngle = 180.0,
      delta = 15.0
      , onSuccess = {
        Log.d(
          "MLFeedback",
          "HIP KNEE ANKLE is good L"
        )},
      onFailure = {        Log.d(
        "MLFeedback",
        "HIP KNEE ANKLE pas cool L"
      )}
    )
    // HIP KNEE ANKLE
    val plankCriterion_LEG_R = AngleCriterion(
      joints = PoseDetectionJoints.RIGHT_HIP_KNEE_ANKLE,
      targetAngle = 180.0,
      delta = 15.0
      , onSuccess = {
        Log.d(
          "MLFeedback",
          "HIP KNEE ANKLE is good R"
        )},
      onFailure = {        Log.d(
        "MLFeedback",
        "HIP KNEE ANKLE pas cool R"
      )}
    )

    //PlankExerciseCriterion
    val PlankExerciseCriterion : ExerciseCriterion = ExerciseCriterion(angleCriterionSet = setOf(
      plankCriterion_SHOULDER_L to plankCriterion_SHOULDER_R,
      plankCriterion_backAngle_L to plankCriterion_backAngle_R,
      plankCriterion_LEG_L  to plankCriterion_LEG_R
    ))
    //CHAIR CRITERIONS
      val chairCriterion_SHOULDER_R = AngleCriterion(
          joints = PoseDetectionJoints.RIGHT_SHOULDER_HIP_KNEE,
          targetAngle = 90.0,
          delta = 15.0,
          onSuccess = {
              Log.d(
                  "MLFeedback",
                  "R SHOULDER HIP KNEE is good"
                )},
          onFailure = {        Log.d(
              "MLFeedback",
              "R SHOULDER HIP KNEE pas cool"
          )}

      )
      val chairCriterion_SHOULDER_L = AngleCriterion(
          joints = PoseDetectionJoints.LEFT_SHOULDER_HIP_KNEE,
          targetAngle = 90.0,
          delta = 15.0,
          onSuccess = {
              Log.d(
                  "MLFeedback",
                  "L SHOULDER HIP KNEE is good"
              )},
          onFailure = {        Log.d(
              "MLFeedback",
              "L SHOULDER HIP KNEE pas cool"
          )}

      )
      val chairCriterion_KNEE_L = AngleCriterion(
          joints = PoseDetectionJoints.LEFT_HIP_KNEE_ANKLE,
          targetAngle = 90.0,
          delta = 10.0,
          onSuccess = {
              Log.d(
                  "MLFeedback",
                  "L KNEE is good"
              )},
          onFailure = {        Log.d(
              "MLFeedback",
              "L KNEE pas cool"
          )}

      )
      val chairCriterion_KNEE_R = AngleCriterion(
          joints = PoseDetectionJoints.RIGHT_HIP_KNEE_ANKLE,
          targetAngle = 90.0,
          delta = 10.0,
          onSuccess = {
              Log.d(
                  "MLFeedback",
                  "R KNEE is good"
              )},
          onFailure = {        Log.d(
              "MLFeedback",
              "R KNEE pas cool"
          )}

      )
      val chairCriterions : ExerciseCriterion = ExerciseCriterion(angleCriterionSet = setOf(
          chairCriterion_SHOULDER_L to chairCriterion_SHOULDER_R,
          chairCriterion_KNEE_L to chairCriterion_KNEE_R
      ))

    fun assessLandMarks(poseLandmarkList : List<PoseLandmark>, exerciseCriterion : ExerciseCriterion) : Boolean{
      Log.d("MLFeedback", "-----------------------------------")
      val listOfBoolean = exerciseCriterion.angleCriterionSet.map { (angleCriterionL,angleCriterionR) ->
        val a_l = pointFToTriple( poseLandmarkList[angleCriterionL.joints.first].position3D)
        val b_l = pointFToTriple( poseLandmarkList[angleCriterionL.joints.second].position3D)
        val c_l = pointFToTriple( poseLandmarkList[angleCriterionL.joints.third].position3D)
        val a_r = pointFToTriple( poseLandmarkList[angleCriterionR.joints.first].position3D)
        val b_r = pointFToTriple( poseLandmarkList[angleCriterionR.joints.second].position3D)
        val c_r = pointFToTriple( poseLandmarkList[angleCriterionR.joints.third].position3D)
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


    fun preambleCriterion(criterionSet: Set<AngleCriterion>) : Set<AngleCriterion> {
        val preambleCriterion = criterionSet.map {
            (joints, targetAngle, delta, onSuccess, onFailure) ->
            AngleCriterion(joints, targetAngle, 1.5*delta, onSuccess, onFailure)

        }
        return preambleCriterion.toSet()
    }


    fun pointFToTriple(point : PointF3D) : Triple<Float,Float,Float>{
      return Triple(point.x,point.y,point.z)
    }






  }
}