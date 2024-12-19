package com.android.sample.mlUtils.exercisesCriterions

import com.android.sample.mlUtils.PoseDetectionJoints

enum class AngleCriterionComments(
    val description: String,
    val focusedJoints: List<Triple<Int, Int, Int>> = listOf()
) {
  SUCCESS("Success"),
  NOT_IMPLEMENTED("Not implemented yet"),
  NONE(""),
  SHOULDER_HIP_KNEE_NOT_FLAT(
      "Your back should be more right.",
      focusedJoints =
          listOf(
              PoseDetectionJoints.LEFT_SHOULDER_HIP_KNEE,
              PoseDetectionJoints.RIGHT_SHOULDER_HIP_KNEE,
          )),
  SHOULDER_HIP_KNEE_NOT_BENDED(
      "Try bending more your body.",
      focusedJoints =
          listOf(
              PoseDetectionJoints.LEFT_SHOULDER_HIP_KNEE,
              PoseDetectionJoints.RIGHT_SHOULDER_HIP_KNEE)),
  SHOULDER_HIP_KNEE_NOT_RIGHT(
      "Bend your body to have a 90° angle at your hips.",
      focusedJoints =
          listOf(
              PoseDetectionJoints.LEFT_SHOULDER_HIP_KNEE,
              PoseDetectionJoints.RIGHT_SHOULDER_HIP_KNEE,
          )),
  LEFT_WRIST_ELBOW_SHOULDER_NOT_FLAT(
      "Keep your right elbow totally open.",
      focusedJoints =
          listOf(
              PoseDetectionJoints.LEFT_WRIST_ELBOW_SHOULDER,
              PoseDetectionJoints.RIGHT_WRIST_ELBOW_SHOULDER,
          )),
  RIGHT_WRIST_ELBOW_SHOULDER_NOT_FLAT(
      "Keep your left elbow totally open.",
      focusedJoints =
          listOf(
              PoseDetectionJoints.LEFT_WRIST_ELBOW_SHOULDER,
              PoseDetectionJoints.RIGHT_WRIST_ELBOW_SHOULDER,
          )),
  BOTH_WRIST_ELBOW_SHOULDER_NOT_FLAT(
      "Keep your elbows stretched and totally open.",
      focusedJoints =
          listOf(
              PoseDetectionJoints.LEFT_WRIST_ELBOW_SHOULDER,
              PoseDetectionJoints.RIGHT_WRIST_ELBOW_SHOULDER,
          )),
  RIGHT_WRIST_ELBOW_SHOULDER_NOT_RIGHT(
      "Your right wrist, elbow and shoulder should make a right angle.",
      focusedJoints =
          listOf(
              PoseDetectionJoints.LEFT_WRIST_ELBOW_SHOULDER,
              PoseDetectionJoints.RIGHT_WRIST_ELBOW_SHOULDER,
          )),
  LEFT_WRIST_ELBOW_SHOULDER_NOT_RIGHT(
      "Your left wrist, elbow and shoulder should make a right angle.",
      focusedJoints =
          listOf(
              PoseDetectionJoints.LEFT_WRIST_ELBOW_SHOULDER,
              PoseDetectionJoints.RIGHT_WRIST_ELBOW_SHOULDER,
          )),
  BOTH_WRIST_ELBOW_SHOULDER_NOT_RIGHT(
      "Your wrists, elbows and shoulders should make a right angle.",
      focusedJoints =
          listOf(
              PoseDetectionJoints.LEFT_WRIST_ELBOW_SHOULDER,
              PoseDetectionJoints.RIGHT_WRIST_ELBOW_SHOULDER,
          )),
  LEFT_ELBOW_SHOULDER_OPPSHOULDER_NOT_FLAT(
      "Keep your left elbow right, aligned with your shoulder.",
      focusedJoints =
          listOf(
              PoseDetectionJoints.LEFT_ELBOW_SHOULDER_OPPSHOULDER,
              PoseDetectionJoints.RIGHT_ELBOW_SHOULDER_OPPSHOULDER,
          )),
  RIGHT_ELBOW_SHOULDER_OPPSHOULDER_NOT_FLAT(
      "Keep your left elbow right, aligned with your shoulder.",
      focusedJoints =
          listOf(
              PoseDetectionJoints.LEFT_ELBOW_SHOULDER_OPPSHOULDER,
              PoseDetectionJoints.RIGHT_ELBOW_SHOULDER_OPPSHOULDER,
          )),
  BOTH_ELBOW_SHOULDER_OPPSHOULDER_NOT_FLAT(
      "Keep elbows right, aligned with your shoulders.",
      focusedJoints =
          listOf(
              PoseDetectionJoints.LEFT_ELBOW_SHOULDER_OPPSHOULDER,
              PoseDetectionJoints.RIGHT_ELBOW_SHOULDER_OPPSHOULDER,
          )),
  LEFT_ELBOW_SHOULDER_HIP_NOT_RIGHT(
      "Keep your left arm straight parallel to the ground.",
      focusedJoints =
          listOf(
              PoseDetectionJoints.LEFT_ELBOW_SHOULDER_OPPSHOULDER,
              PoseDetectionJoints.RIGHT_ELBOW_SHOULDER_OPPSHOULDER,
          )),
  RIGHT_ELBOW_SHOULDER_HIP_NOT_RIGHT(
      "Keep your elbows under your shoulders.",
      focusedJoints =
          listOf(
              PoseDetectionJoints.LEFT_ELBOW_SHOULDER_HIP,
              PoseDetectionJoints.RIGHT_ELBOW_SHOULDER_HIP,
          )),
  BOTH_ELBOW_SHOULDER_HIP_NOT_RIGHT(
      "Keep your elbows under your shoulders.",
      focusedJoints =
          listOf(
              PoseDetectionJoints.LEFT_ELBOW_SHOULDER_HIP,
              PoseDetectionJoints.RIGHT_ELBOW_SHOULDER_HIP,
          )),
  ELBOW_SHOULDER_HIP_NOT_FLAT(
      "Put your arms aligned with your back.",
      focusedJoints =
          listOf(
              PoseDetectionJoints.LEFT_ELBOW_SHOULDER_HIP,
              PoseDetectionJoints.RIGHT_ELBOW_SHOULDER_HIP,
          )),
  LEFT_HIP_KNEE_ANKLE_NOT_FLAT(
      "Keep your left leg stretched.",
      focusedJoints = listOf(PoseDetectionJoints.LEFT_HIP_KNEE_ANKLE)),
  RIGHT_HIP_KNEE_ANKLE_NOT_FLAT(
      "Keep your right leg stretched.",
      focusedJoints = listOf(PoseDetectionJoints.RIGHT_HIP_KNEE_ANKLE)),
  BOTH_HIP_KNEE_ANKLE_NOT_FLAT(
      "Keep your legs stretched.",
      focusedJoints =
          listOf(
              PoseDetectionJoints.LEFT_HIP_KNEE_ANKLE, PoseDetectionJoints.RIGHT_HIP_KNEE_ANKLE)),
  RIGHT_HIP_KNEE_ANKLE_NOT_RIGHT(
      "Keep an angle of 90 degree with your right knee.",
      focusedJoints = listOf(PoseDetectionJoints.RIGHT_HIP_KNEE_ANKLE)),
  LEFT_HIP_KNEE_ANKLE_NOT_RIGHT(
      "Keep an angle of 90 degree with your left knee.",
      focusedJoints = listOf(PoseDetectionJoints.LEFT_HIP_KNEE_ANKLE)),
  BOTH_HIP_KNEE_ANKLE_NOT_RIGHT(
      "Keep an angle of 90 degree with your knees.",
      focusedJoints =
          listOf(
              PoseDetectionJoints.LEFT_HIP_KNEE_ANKLE, PoseDetectionJoints.RIGHT_HIP_KNEE_ANKLE)),
  RIGHT_OPPHIP_HIP_KNEE_NOT_OPEN(
      "Spread more your right leg.",
      focusedJoints = listOf(PoseDetectionJoints.RIGHT_OPPHIP_HIP_KNEE)),
  LEFT_OPPHIP_HIP_KNEE_NOT_OPEN(
      "Spread more your left leg.",
      focusedJoints = listOf(PoseDetectionJoints.LEFT_OPPHIP_HIP_KNEE)),
  BOTH_OPPHIP_HIP_KNEE_NOT_OPEN(
      "Spread more your legs.",
      focusedJoints =
          listOf(
              PoseDetectionJoints.LEFT_OPPHIP_HIP_KNEE, PoseDetectionJoints.RIGHT_OPPHIP_HIP_KNEE)),
  RIGHT_OPPHIP_HIP_KNEE_NOT_CLOSED(
      "Tighten more your right leg.",
      focusedJoints = listOf(PoseDetectionJoints.RIGHT_OPPHIP_HIP_KNEE)),
  LEFT_OPPHIP_HIP_KNEE_NOT_CLOSED(
      "Tighten more your left leg.",
      focusedJoints = listOf(PoseDetectionJoints.LEFT_OPPHIP_HIP_KNEE)),
  BOTH_OPPHIP_HIP_KNEE_NOT_CLOSED(
      "Tighten more your legs",
      focusedJoints =
          listOf(
              PoseDetectionJoints.LEFT_OPPHIP_HIP_KNEE, PoseDetectionJoints.RIGHT_OPPHIP_HIP_KNEE))
}
