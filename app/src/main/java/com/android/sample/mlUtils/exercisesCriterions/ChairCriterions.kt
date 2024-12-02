package com.android.sample.mlUtils.exercisesCriterions

import android.util.Log
import com.android.sample.mlUtils.ExerciseFeedBack.Companion.AngleCriterion
import com.android.sample.mlUtils.ExerciseFeedBack.Companion.ExerciseCriterion
import com.android.sample.mlUtils.PoseDetectionJoints

// CHAIR CRITERIONS
private val chairCriterion_SHOULDER_R =
    AngleCriterion(
        joints = PoseDetectionJoints.RIGHT_SHOULDER_HIP_KNEE,
        targetAngle = 90.0,
        delta = 15.0,
        failCorrectionComment = AngleCriterionComments.SHOULDER_HIP_KNEE_NOT_RIGHT,
        onSuccess = { Log.d("MLFeedback", "R SHOULDER HIP KNEE is good") },
        onFailure = { Log.d("MLFeedback", "R SHOULDER HIP KNEE pas cool") })

private val chairCriterion_SHOULDER_L =
    AngleCriterion(
        joints = PoseDetectionJoints.LEFT_SHOULDER_HIP_KNEE,
        targetAngle = 90.0,
        delta = 15.0,
        failCorrectionComment = AngleCriterionComments.SHOULDER_HIP_KNEE_NOT_RIGHT,
        onSuccess = { Log.d("MLFeedback", "L SHOULDER HIP KNEE is good") },
        onFailure = { Log.d("MLFeedback", "L SHOULDER HIP KNEE pas cool") })

private val chairCriterion_KNEE_L =
    AngleCriterion(
        joints = PoseDetectionJoints.LEFT_HIP_KNEE_ANKLE,
        targetAngle = 90.0,
        delta = 10.0,
        failCorrectionComment = AngleCriterionComments.BOTH_HIP_KNEE_ANKLE_NOT_RIGHT,
        onSuccess = { Log.d("MLFeedback", "L KNEE is good") },
        onFailure = { Log.d("MLFeedback", "L KNEE pas cool") })

private val chairCriterion_KNEE_R =
    AngleCriterion(
        joints = PoseDetectionJoints.RIGHT_HIP_KNEE_ANKLE,
        targetAngle = 90.0,
        delta = 10.0,
        failCorrectionComment = AngleCriterionComments.BOTH_HIP_KNEE_ANKLE_NOT_RIGHT,
        onSuccess = { Log.d("MLFeedback", "R KNEE is good") },
        onFailure = { Log.d("MLFeedback", "R KNEE pas cool") })

val ChairCriterions: ExerciseCriterion =
    ExerciseCriterion(
        angleCriterionSet =
            setOf(
                chairCriterion_SHOULDER_L to chairCriterion_SHOULDER_R,
                chairCriterion_KNEE_L to chairCriterion_KNEE_R))
