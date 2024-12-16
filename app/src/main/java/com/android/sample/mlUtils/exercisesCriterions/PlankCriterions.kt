package com.android.sample.mlUtils.exercisesCriterions

import android.util.Log
import com.android.sample.mlUtils.ExerciseFeedBack.Companion.AngleCriterion
import com.android.sample.mlUtils.ExerciseFeedBack.Companion.ExerciseCriterion
import com.android.sample.mlUtils.PoseDetectionJoints
import com.android.sample.model.workout.ExerciseType

// SHOULDER HIP KNEE
private val plankCriterion_backAngle_L =
    AngleCriterion(
        joints = PoseDetectionJoints.LEFT_SHOULDER_HIP_KNEE,
        targetAngle = 180.0,
        delta = 25.0,
        failCorrectionComment = AngleCriterionComments.SHOULDER_HIP_KNEE_NOT_FLAT,
        onSuccess = { Log.d("MLFeedback", "Back is good L") },
        onFailure = { Log.d("MLFeedback", "Don't bend your back L") })

private val plankCriterion_backAngle_R =
    AngleCriterion(
        joints = PoseDetectionJoints.RIGHT_SHOULDER_HIP_KNEE,
        targetAngle = 180.0,
        delta = 25.0,
        failCorrectionComment = AngleCriterionComments.SHOULDER_HIP_KNEE_NOT_FLAT,
        onSuccess = { Log.d("MLFeedback", "Back is good R") },
        onFailure = { Log.d("MLFeedback", "SHOULDER HIP KNEE pas cool R") })

// HIP SHOULDER ELBOW
private val plankCriterion_SHOULDER_L =
    AngleCriterion(
        joints = PoseDetectionJoints.LEFT_ELBOW_SHOULDER_HIP,
        targetAngle = 90.0,
        failCorrectionComment = AngleCriterionComments.LEFT_WRIST_ELBOW_SHOULDER_NOT_RIGHT,
        delta = 15.0,
        onSuccess = { Log.d("MLFeedback", "HIP SHOULDER ELBOW is good L") },
        onFailure = { Log.d("MLFeedback", "HIP SHOULDER ELBOW pas cool L") })
// HIP SHOULDER ELBOW
private val plankCriterion_SHOULDER_R =
    AngleCriterion(
        joints = PoseDetectionJoints.RIGHT_ELBOW_SHOULDER_HIP,
        targetAngle = 90.0,
        failCorrectionComment = AngleCriterionComments.RIGHT_WRIST_ELBOW_SHOULDER_NOT_RIGHT,
        delta = 15.0,
        onSuccess = { Log.d("MLFeedback", "HIP SHOULDER ELBOW is good R") },
        onFailure = { Log.d("MLFeedback", "HIP SHOULDER ELBOW pas cool R") })

// HIP KNEE ANKLE
private val plankCriterion_LEG_L =
    AngleCriterion(
        joints = PoseDetectionJoints.LEFT_HIP_KNEE_ANKLE,
        targetAngle = 170.0,
        failCorrectionComment = AngleCriterionComments.LEFT_HIP_KNEE_ANKLE_NOT_FLAT,
        delta = 15.0,
        onSuccess = { Log.d("MLFeedback", "HIP KNEE ANKLE is good L") },
        onFailure = { Log.d("MLFeedback", "HIP KNEE ANKLE pas cool L") })
// HIP KNEE ANKLE
private val plankCriterion_LEG_R =
    AngleCriterion(
        joints = PoseDetectionJoints.RIGHT_HIP_KNEE_ANKLE,
        targetAngle = 170.0,
        failCorrectionComment = AngleCriterionComments.RIGHT_HIP_KNEE_ANKLE_NOT_FLAT,
        delta = 15.0,
        onSuccess = { Log.d("MLFeedback", "HIP KNEE ANKLE is good R") },
        onFailure = { Log.d("MLFeedback", "HIP KNEE ANKLE pas cool R") })

// PlankExerciseCriterion
val PlankExerciseCriterions: ExerciseCriterion =
    ExerciseCriterion(
        exerciseName = ExerciseType.PLANK.toString(),
        criterionName = ExerciseType.PLANK.toString(),
        angleCriterionSet =
            setOf(
                plankCriterion_SHOULDER_L to plankCriterion_SHOULDER_R,
                plankCriterion_backAngle_L to plankCriterion_backAngle_R,
                plankCriterion_LEG_L to plankCriterion_LEG_R))
