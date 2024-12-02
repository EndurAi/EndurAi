package com.android.sample.mlUtils.exercisesCriterions


import android.util.Log
import com.android.sample.mlUtils.ExerciseFeedBack.Companion.AngleCriterion
import com.android.sample.mlUtils.ExerciseFeedBack.Companion.ExerciseCriterion
import com.android.sample.mlUtils.PoseDetectionJoints

// ArmLeft flat
private val armLeftFlat =
  AngleCriterion(
    joints = PoseDetectionJoints.LEFT_WRIST_ELBOW_SHOULDER,
    targetAngle = 180.0,
    delta = 20.0,
    combination = true,
    onSuccess = { Log.d("MLFeedback", "ArmLeft flat is good L") },
    onFailure = { Log.d("MLFeedback", "ArmLeft flat pas cool L") })

// ArmRight flat
private val armRightFlat =
  AngleCriterion(
    joints = PoseDetectionJoints.RIGHT_WRIST_ELBOW_SHOULDER,
    targetAngle = 180.0,
    delta = 20.0,
    combination = true,
    onSuccess = { Log.d("MLFeedback", "ArmRight flat is good R") },
    onFailure = { Log.d("MLFeedback", "ArmRight flat pas cool R") })


// arm Left perpendicular to body
private val armLeft_body_Perpendicular =
  AngleCriterion(
    joints = PoseDetectionJoints.LEFT_ELBOW_SHOULDER_HIP,
    targetAngle = 100.0,
    delta = 15.0,
    onSuccess = { Log.d("MLFeedback", "arm and body perpendicular LEFT flat is good L") },
    onFailure = { Log.d("MLFeedback", "arm and body perpendicular LEFT pas cool L") })

private val armRight_body_Perpendicular =
  AngleCriterion(
    joints = PoseDetectionJoints.RIGHT_ELBOW_SHOULDER_HIP,
    targetAngle = 100.0,
    delta = 15.0,
    onSuccess = { Log.d("MLFeedback", "arm and body perpendicular RIGHT flat is good R") },
    onFailure = { Log.d("MLFeedback", "arm and body perpendicular RIGHT pas cool R") })


private val bendedLeg_left =
  AngleCriterion(
    joints = PoseDetectionJoints.LEFT_HIP_KNEE_ANKLE,
    targetAngle = 100.0,
    delta = 8.0,
    onSuccess = { Log.d("MLFeedback", "bendedLeg_left is good L") },
    onFailure = { Log.d("MLFeedback", "bendedLeg_left pas cool L") })


private val bendedLeg_right =
  AngleCriterion(
    joints = PoseDetectionJoints.RIGHT_HIP_KNEE_ANKLE,
    targetAngle = 100.0,
    delta = 8.0,
    onSuccess = { Log.d("MLFeedback", "bendedLeg_right is good R") },
    onFailure = { Log.d("MLFeedback", "bendedLeg_right pas cool R") })



private val flatLeg_left =
  AngleCriterion(
    joints = PoseDetectionJoints.LEFT_HIP_KNEE_ANKLE,
    targetAngle = 175.0,
    delta = 15.0,
    onSuccess = { Log.d("MLFeedback", "flatLeg_left is good L") },
    onFailure = { Log.d("MLFeedback", "flatLeg_left pas coolL") })


private val flatLeg_right =
  AngleCriterion(
    joints = PoseDetectionJoints.RIGHT_HIP_KNEE_ANKLE,
    targetAngle = 175.0,
    delta = 15.0,
    onSuccess = { Log.d("MLFeedback", "flatLeg_right is good R") },
    onFailure = { Log.d("MLFeedback", "flatLeg_right pas cool R") })


// PlankExerciseCriterion
val Warrior_2_LEFT_Criterions: ExerciseCriterion =
  ExerciseCriterion(
    angleCriterionSet =
    setOf(
      armLeftFlat to armRightFlat,
      armLeft_body_Perpendicular to armRight_body_Perpendicular,
      bendedLeg_left to flatLeg_right)) //left leg is bent

val Warrior_2_RIGHT_Criterions: ExerciseCriterion =
  ExerciseCriterion(
    angleCriterionSet =
    setOf(
      armLeftFlat to armRightFlat,
      armLeft_body_Perpendicular to armRight_body_Perpendicular,
      flatLeg_left to bendedLeg_right)) //right leg is bent