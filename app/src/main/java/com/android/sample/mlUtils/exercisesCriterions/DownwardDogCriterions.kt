package com.android.sample.mlUtils.exercisesCriterions

import android.util.Log
import com.android.sample.mlUtils.ExerciseFeedBack.Companion.AngleCriterion
import com.android.sample.mlUtils.PoseDetectionJoints
import com.android.sample.mlUtils.ExerciseFeedBack.Companion.ExerciseCriterion

private val legStraight_L =
  AngleCriterion(
    joints = PoseDetectionJoints.LEFT_HIP_KNEE_ANKLE,
    targetAngle = 170.0,
    delta = 10.0,
    failCorrectionComment = AngleCriterionComments.BOTH_HIP_KNEE_ANKLE_NOT_FLAT,
    onSuccess = { Log.d("MLFeedback", "L Leg is good") },
    onFailure = { Log.d("MLFeedback", "L leg pas cool") })

private val legStraight_R =
  AngleCriterion(
    joints = PoseDetectionJoints.RIGHT_HIP_KNEE_ANKLE,
    failCorrectionComment = AngleCriterionComments.BOTH_HIP_KNEE_ANKLE_NOT_FLAT,
    targetAngle = 170.0,
    delta = 10.0,
    onSuccess = { Log.d("MLFeedback", "R leg is good") },
    onFailure = { Log.d("MLFeedback", "R leg pas cool") })



//flat arms
private val armUp_L =
  AngleCriterion(
    joints = PoseDetectionJoints.LEFT_ELBOW_SHOULDER_HIP,
    targetAngle = 170.0,
    failCorrectionComment = AngleCriterionComments.BOTH_HIP_KNEE_ANKLE_NOT_FLAT,

    delta =10.0,
    onSuccess = { Log.d("MLFeedback", "L arm up is good") },
    onFailure = { Log.d("MLFeedback", "L arm up pas cool") })

private val armUp_R =
  AngleCriterion(
    joints = PoseDetectionJoints.RIGHT_ELBOW_SHOULDER_HIP,
    targetAngle = 170.0,
    delta =10.0,
    onSuccess = { Log.d("MLFeedback", "R arm Up is good") },
    onFailure = { Log.d("MLFeedback", "R arm Up pas cool") })

//Elbow are flat
private val elbowFlat_L =
  AngleCriterion(
    joints = PoseDetectionJoints.LEFT_WRIST_ELBOW_SHOULDER,
    targetAngle = 170.0,
    delta =10.0,
    onSuccess = { Log.d("MLFeedback", "L elbow flat is good") },
    onFailure = { Log.d("MLFeedback", "L elbow pas cool") })

private val elbowFlat_R =
  AngleCriterion(
    joints = PoseDetectionJoints.RIGHT_WRIST_ELBOW_SHOULDER,
    targetAngle = 170.0,
    delta = 10.0,
    onSuccess = { Log.d("MLFeedback", "R elbow flat is good") },
    onFailure = { Log.d("MLFeedback", "R elbow flat pas cool") })

//Body is bended

private val bended_hip_L =
  AngleCriterion(
    joints = PoseDetectionJoints.LEFT_SHOULDER_HIP_KNEE,
    targetAngle = 80.0,
    delta =15.0,
    onSuccess = { Log.d("MLFeedback", "L bended hip is good") },
    onFailure = { Log.d("MLFeedback", "L bended hip pas cool") })

private val  bended_hip_R =
  AngleCriterion(
    joints = PoseDetectionJoints.RIGHT_SHOULDER_HIP_KNEE,
    targetAngle = 80.0,
    delta = 15.0,
    onSuccess = { Log.d("MLFeedback", "R bended hip is good") },
    onFailure = { Log.d("MLFeedback", "R bended hip pas cool") })



val DownwardDogCriterions :  ExerciseCriterion = ExerciseCriterion(
  setOf(
    legStraight_L to legStraight_R,
    armUp_L to armUp_R,
    elbowFlat_L to elbowFlat_R,
    bended_hip_L to bended_hip_R
  )
)
