package com.android.sample.mlUtils.exercisesCriterions
enum class AngleCriterionComments(val description: String) {
  SUCCESS("Success"),
  NOT_IMPLEMENTED("Not implemented yet"),
  SHOULDER_HIP_KNEE_NOT_FLAT("Your back should be more right"),
  SHOULDER_HIP_KNEE_NOT_BENDED("Try bending more your body"),
  SHOULDER_HIP_KNEE_NOT_RIGHT("Bend your body to have a 90° angle at your hips."),
  LEFT_WRIST_ELBOW_SHOULDER_NOT_FLAT("Keep your right elbow totally open"),
  RIGHT_WRIST_ELBOW_SHOULDER_NOT_FLAT("Keep your left elbow totally open"),
  BOTH_WRIST_ELBOW_SHOULDER_NOT_FLAT("Keep your elbows stretched and totally open"),
  RIGHT_WRIST_ELBOW_SHOULDER_NOT_RIGHT(
      "Your right wrist, elbow and shoulder should make a right angle."),
  LEFT_WRIST_ELBOW_SHOULDER_NOT_RIGHT(
      "Your left wrist, elbow and shoulder should make a right angle."),
  BOTH_WRIST_ELBOW_SHOULDER_NOT_RIGHT(
      "Your wrists, elbows and shoulders should make a right angle."),
  LEFT_ELBOW_SHOULDER_OPPSHOULDER_NOT_FLAT(
      "Keep your left elbow right, aligned with your shoulder."),
  RIGHT_ELBOW_SHOULDER_OPPSHOULDER_NOT_FLAT(
      "Keep your left elbow right, aligned with your shoulder."),
  BOTH_ELBOW_SHOULDER_OPPSHOULDER_NOT_FLAT("Keep elbows right, aligned with your shoulders."),
  ELBOW_SHOULDER_HIP_NOT_FLAT("Put your arms aligned with your back."),
  LEFT_HIP_KNEE_ANKLE_NOT_FLAT("Keep your left leg stretched"),
  RIGHT_HIP_KNEE_ANKLE_NOT_FLAT("Keep your right leg stretched"),
  BOTH_HIP_KNEE_ANKLE_NOT_FLAT("Keep your legs stretched"),
  RIGHT_HIP_KNEE_ANKLE_NOT_RIGHT("Keep an angle of 90 degree with your right knee."),
  LEFT_HIP_KNEE_ANKLE_NOT_RIGHT("Keep an angle of 90 degree with your left knee."),
  BOTH_HIP_KNEE_ANKLE_NOT_RIGHT("Keep an angle of 90 degree with your knees."),
  RIGHT_OPPHIP_HIP_KNEE_NOT_OPEN("Spread more your right leg."),
  LEFT_OPPHIP_HIP_KNEE_NOT_OPEN("Spread more your left leg."),
  BOTH_OPPHIP_HIP_KNEE_NOT_OPEN("Spread more your legs."),
  RIGHT_OPPHIP_HIP_KNEE_NOT_CLOSED("Tighten more your right leg."),
  LEFT_OPPHIP_HIP_KNEE_NOT_CLOSED("Tighten more your left leg."),
  BOTH_OPPHIP_HIP_KNEE_NOT_CLOSED("Tighten more your legs."),
}
