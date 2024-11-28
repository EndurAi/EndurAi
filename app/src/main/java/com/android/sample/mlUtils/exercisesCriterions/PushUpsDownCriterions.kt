package com.android.sample.mlUtils.exercisesCriterions

import android.util.Log
import com.android.sample.mlUtils.ExerciseFeedBack.Companion.AngleCriterion
import com.android.sample.mlUtils.ExerciseFeedBack.Companion.ExerciseCriterion
import com.android.sample.mlUtils.PoseDetectionJoints

private val legStraight_L =
  AngleCriterion(
    joints = PoseDetectionJoints.LEFT_HIP_KNEE_ANKLE,
    targetAngle = 165.0,
    delta = 15.0,
    onSuccess = { Log.d("MLFeedback", "L Leg is good") },
    onFailure = { Log.d("MLFeedback", "L leg pas cool") })

private val legStraight_R =
  AngleCriterion(
    joints = PoseDetectionJoints.RIGHT_HIP_KNEE_ANKLE,
    targetAngle = 165.0,
    delta = 15.0,
    onSuccess = { Log.d("MLFeedback", "R leg is good") },
    onFailure = { Log.d("MLFeedback", "R leg pas cool") })


private val back_L =
  AngleCriterion(
    joints = PoseDetectionJoints.LEFT_SHOULDER_HIP_KNEE,
    targetAngle = 180.0,
    delta = 15.0,
    onSuccess = { Log.d("MLFeedback", "L back is good") },
    onFailure = { Log.d("MLFeedback", "L back pas cool") })

private val back_R =
  AngleCriterion(
    joints = PoseDetectionJoints.RIGHT_SHOULDER_HIP_KNEE,
    targetAngle = 180.0,
    delta = 15.0,
    onSuccess = { Log.d("MLFeedback", "R back is good") },
    onFailure = { Log.d("MLFeedback", "R back pas cool") })



private val elbow_L =
  AngleCriterion(
    joints = PoseDetectionJoints.LEFT_WRIST_ELBOW_SHOULDER,
    targetAngle = 90.0,
    delta = 15.0,
    onSuccess = { Log.d("MLFeedback", "L elbow is good") },
    onFailure = { Log.d("MLFeedback", "L elbow pas cool") })

private val elbow_R =
  AngleCriterion(
    joints = PoseDetectionJoints.RIGHT_WRIST_ELBOW_SHOULDER,
    targetAngle = 90.0,
    delta = 15.0,
    onSuccess = { Log.d("MLFeedback", "R elbow is good") },
    onFailure = { Log.d("MLFeedback", "R elbow pas cool") })



val PushUpsDownCrierions: ExerciseCriterion =
  ExerciseCriterion(
    angleCriterionSet =
    setOf(
      elbow_L to elbow_R,
      legStraight_L to legStraight_R,
      back_L to back_R))

