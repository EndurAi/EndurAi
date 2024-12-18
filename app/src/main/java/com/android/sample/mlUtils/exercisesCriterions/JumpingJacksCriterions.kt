package com.android.sample.mlUtils.exercisesCriterions

import android.util.Log
import com.android.sample.mlUtils.ExerciseFeedBack.Companion.AngleCriterion
import com.android.sample.mlUtils.ExerciseFeedBack.Companion.ExerciseCriterion
import com.android.sample.mlUtils.PoseDetectionJoints
import com.android.sample.model.workout.ExerciseType

// Arms flat
private val armFlat_L =
    AngleCriterion(
        joints = PoseDetectionJoints.LEFT_WRIST_ELBOW_SHOULDER,
        targetAngle = 165.0,
        delta = 20.0,
        failCorrectionComment = AngleCriterionComments.NONE,
        onSuccess = { Log.d("MLFeedback", "L arm flat is good") },
        onFailure = { Log.d("MLFeedback", "L arm pas cool") },
    )
private val armFlat_R =
    AngleCriterion(
        joints = PoseDetectionJoints.RIGHT_ELBOW_SHOULDER_OPPSHOULDER,
        failCorrectionComment = AngleCriterionComments.NONE,
        targetAngle = 165.0,
        delta = 20.0,
        onSuccess = { Log.d("MLFeedback", "R arm flat is good") },
        onFailure = { Log.d("MLFeedback", "R arm pas cool") })

// Arm are low
private val armLow_L =
    AngleCriterion(
        joints = PoseDetectionJoints.LEFT_ELBOW_SHOULDER_OPPSHOULDER,
        failCorrectionComment = AngleCriterionComments.NONE,
        targetAngle = 90.0,
        delta = 15.0,
        combination = true,
        LR_FailComment = AngleCriterionComments.NONE,
        onSuccess = { Log.d("MLFeedback", "L arm low is good") },
        onFailure = { Log.d("MLFeedback", "L arm low pas cool") })

private val armLow_R =
    AngleCriterion(
        joints = PoseDetectionJoints.RIGHT_ELBOW_SHOULDER_OPPSHOULDER,
        targetAngle = 90.0,
        failCorrectionComment = AngleCriterionComments.NONE,
        combination = true,
        LR_FailComment = AngleCriterionComments.NONE,
        delta = 20.0,
        onSuccess = { Log.d("MLFeedback", "R arm Low is good") },
        onFailure = { Log.d("MLFeedback", "R arm low pas cool") })

// Arm are up
private val armUp_L =
    AngleCriterion(
        joints = PoseDetectionJoints.LEFT_ELBOW_SHOULDER_HIP,
        failCorrectionComment = AngleCriterionComments.NONE,
        targetAngle = 160.0,
        delta = 15.0,
        LR_FailComment = AngleCriterionComments.NONE,
        combination = true,
        onSuccess = { Log.d("MLFeedback", "L arm up is good") },
        onFailure = { Log.d("MLFeedback", "L arm up pas cool") })

private val armUp_R =
    AngleCriterion(
        joints = PoseDetectionJoints.RIGHT_ELBOW_SHOULDER_HIP,
        failCorrectionComment = AngleCriterionComments.NONE,
        targetAngle = 160.0,
        LR_FailComment = AngleCriterionComments.NONE,
        combination = true,
        delta = 15.0,
        onSuccess = { Log.d("MLFeedback", "R arm Up is good") },
        onFailure = { Log.d("MLFeedback", "R arm Up pas cool") })

// legs are flat
private val legFlat_L =
    AngleCriterion(
        joints = PoseDetectionJoints.LEFT_HIP_KNEE_ANKLE,
        failCorrectionComment = AngleCriterionComments.NONE,
        LR_FailComment = AngleCriterionComments.NONE,
        targetAngle = 165.0,
        combination = true,
        delta = 15.0,
        onSuccess = { Log.d("MLFeedback", "L lefFlat is good") },
        onFailure = { Log.d("MLFeedback", "L legFlat pas cool") })

private val legFlat_R =
    AngleCriterion(
        joints = PoseDetectionJoints.RIGHT_HIP_KNEE_ANKLE,
        failCorrectionComment = AngleCriterionComments.NONE,
        LR_FailComment = AngleCriterionComments.NONE,
        targetAngle = 165.0,
        delta = 15.0,
        combination = true,
        onSuccess = { Log.d("MLFeedback", "R lefFlat is good") },
        onFailure = { Log.d("MLFeedback", "R legFlat pas cool") })

// legs are closed
private val legClosed_L =
    AngleCriterion(
        joints = PoseDetectionJoints.LEFT_OPPHIP_HIP_KNEE,
        failCorrectionComment = AngleCriterionComments.NONE,
        targetAngle = 90.0,
        delta = 5.0,
        combination = true,
        onSuccess = { Log.d("MLFeedback", "L legClosed is good") },
        onFailure = { Log.d("MLFeedback", "L legFlat pas cool") })

private val legClosed_R =
    AngleCriterion(
        joints = PoseDetectionJoints.RIGHT_OPPHIP_HIP_KNEE,
        targetAngle = 90.0,
        failCorrectionComment = AngleCriterionComments.NONE,
        delta = 5.0,
        onSuccess = { Log.d("MLFeedback", "R legClosed is good") },
        onFailure = { Log.d("MLFeedback", "R legClosed pas cool") })

// legs are open
private val legOpen_L =
    AngleCriterion(
        joints = PoseDetectionJoints.LEFT_OPPHIP_HIP_KNEE,
        targetAngle = 115.0,
        failCorrectionComment = AngleCriterionComments.NONE,
        delta = 10.0,
        combination = true,
        onSuccess = { Log.d("MLFeedback", "L legOpen is good") },
        onFailure = { Log.d("MLFeedback", "L legOpen pas cool") })

private val legOpen_R =
    AngleCriterion(
        joints = PoseDetectionJoints.RIGHT_OPPHIP_HIP_KNEE,
        targetAngle = 115.0,
        failCorrectionComment = AngleCriterionComments.NONE,
        delta = 10.0,
        combination = true,
        onSuccess = { Log.d("MLFeedback", "R legOpen is good") },
        onFailure = { Log.d("MLFeedback", "R legOpen pas cool") })

val JumpingJacksOpenCriterions: ExerciseCriterion =
    ExerciseCriterion(
        isCommented = false,
        criterionName = "Jumping-jacks open position",
        exerciseName = ExerciseType.JUMPING_JACKS.toString(),
        angleCriterionSet =
            setOf(legOpen_L to legOpen_R, legFlat_L to legFlat_R, armUp_L to armUp_R))

val JumpingJacksClosedCriterions: ExerciseCriterion =
    ExerciseCriterion(
        criterionName = "Jumping-jacks closed position",
        exerciseName = ExerciseType.JUMPING_JACKS.toString(),
        isCommented = false,
        angleCriterionSet =
            setOf(
                legClosed_L to legClosed_R,
                legFlat_L to legFlat_R,
                armFlat_L to armFlat_R,
                armLow_L to armLow_L,
            ))
