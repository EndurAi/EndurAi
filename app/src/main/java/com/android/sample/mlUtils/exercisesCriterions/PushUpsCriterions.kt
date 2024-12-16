package com.android.sample.mlUtils.exercisesCriterions

import android.util.Log
import com.android.sample.mlUtils.ExerciseFeedBack.Companion.AngleCriterion
import com.android.sample.mlUtils.ExerciseFeedBack.Companion.ExerciseCriterion
import com.android.sample.mlUtils.PoseDetectionJoints
import com.android.sample.model.workout.ExerciseType

private val legStraight_L =
    AngleCriterion(
        joints = PoseDetectionJoints.LEFT_HIP_KNEE_ANKLE,
        failCorrectionComment = AngleCriterionComments.BOTH_HIP_KNEE_ANKLE_NOT_FLAT,
        targetAngle = 165.0,
        delta = 15.0,
        onSuccess = { Log.d("MLFeedback", "L Leg is good") },
        onFailure = { Log.d("MLFeedback", "L leg pas cool") })

private val legStraight_R =
    AngleCriterion(
        joints = PoseDetectionJoints.RIGHT_HIP_KNEE_ANKLE,
        failCorrectionComment = AngleCriterionComments.BOTH_HIP_KNEE_ANKLE_NOT_FLAT,
        targetAngle = 165.0,
        delta = 15.0,
        onSuccess = { Log.d("MLFeedback", "R leg is good") },
        onFailure = { Log.d("MLFeedback", "R leg pas cool") })

private val back_L =
    AngleCriterion(
        joints = PoseDetectionJoints.LEFT_SHOULDER_HIP_KNEE,
        targetAngle = 180.0,
        failCorrectionComment = AngleCriterionComments.SHOULDER_HIP_KNEE_NOT_FLAT,
        delta = 15.0,
        onSuccess = { Log.d("MLFeedback", "L back is good") },
        onFailure = { Log.d("MLFeedback", "L back pas cool") })

private val back_R =
    AngleCriterion(
        joints = PoseDetectionJoints.RIGHT_SHOULDER_HIP_KNEE,
        targetAngle = 180.0,
        failCorrectionComment = AngleCriterionComments.SHOULDER_HIP_KNEE_NOT_FLAT,
        delta = 15.0,
        onSuccess = { Log.d("MLFeedback", "R back is good") },
        onFailure = { Log.d("MLFeedback", "R back pas cool") })

private val elbowCurved_L =
    AngleCriterion(
        joints = PoseDetectionJoints.LEFT_WRIST_ELBOW_SHOULDER,
        failCorrectionComment = AngleCriterionComments.BOTH_WRIST_ELBOW_SHOULDER_NOT_RIGHT,
        targetAngle = 90.0,
        delta = 20.0,
        onSuccess = { Log.d("MLFeedback", "L elbow curved is good") },
        onFailure = { Log.d("MLFeedback", "L elbow curved pas cool") })

private val elbowCurved_R =
    AngleCriterion(
        joints = PoseDetectionJoints.RIGHT_WRIST_ELBOW_SHOULDER,
        failCorrectionComment = AngleCriterionComments.BOTH_WRIST_ELBOW_SHOULDER_NOT_RIGHT,
        targetAngle = 90.0,
        delta = 20.0,
        onSuccess = { Log.d("MLFeedback", "R elbow is good") },
        onFailure = { Log.d("MLFeedback", "R elbow pas cool") })

private val elbowFlat_L =
    AngleCriterion(
        joints = PoseDetectionJoints.LEFT_WRIST_ELBOW_SHOULDER,
        failCorrectionComment = AngleCriterionComments.BOTH_WRIST_ELBOW_SHOULDER_NOT_FLAT,
        targetAngle = 170.0,
        delta = 20.0,
        onSuccess = { Log.d("MLFeedback", "L elbow flat is good") },
        onFailure = { Log.d("MLFeedback", "L elbow pas cool") })

private val elbowFlat_R =
    AngleCriterion(
        joints = PoseDetectionJoints.RIGHT_WRIST_ELBOW_SHOULDER,
        failCorrectionComment = AngleCriterionComments.BOTH_WRIST_ELBOW_SHOULDER_NOT_FLAT,
        targetAngle = 170.0,
        delta = 20.0,
        onSuccess = { Log.d("MLFeedback", "R elbow flat is good") },
        onFailure = { Log.d("MLFeedback", "R elbow flat pas cool") })

val PushUpsDownCriterions: ExerciseCriterion =
    ExerciseCriterion(
        exerciseName = ExerciseType.PUSH_UPS.toString(),
        criterionName = "Pushup low position",
        angleCriterionSet =
            setOf(elbowCurved_L to elbowCurved_R, legStraight_L to legStraight_R, back_L to back_R))

val PushUpsUpCrierions: ExerciseCriterion =
    ExerciseCriterion(
        exerciseName = ExerciseType.PUSH_UPS.toString(),
        criterionName = "Pushup high position",
        angleCriterionSet =
            setOf(elbowFlat_L to elbowFlat_R, legStraight_L to legStraight_R, back_L to back_R))
