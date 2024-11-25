package com.android.sample.utils;

import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark

class PoseDetectionJoints {
  companion object {
    /*
    LR Elbow - Shoulder - Hip
     */
    val LEFT_ELBOW_SHOULDER_HIP =
      Triple(PoseLandmark.LEFT_ELBOW, PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_HIP)
    val RIGHT_ELBOW_SHOULDER_HIP =
      Triple(PoseLandmark.RIGHT_ELBOW, PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_HIP)

    /*
  LR wrist - Elbow - Shoulder
  */
    val LEFT_WRIST_ELBOW_SHOULDER =
      Triple(PoseLandmark.LEFT_WRIST, PoseLandmark.LEFT_ELBOW, PoseLandmark.LEFT_SHOULDER)
    val RIGHT_WRIST_ELBOW_SHOULDER =
      Triple(PoseLandmark.RIGHT_WRIST, PoseLandmark.RIGHT_ELBOW, PoseLandmark.RIGHT_SHOULDER)


/*
  LR Hip - Knee - Ankle
  */
val LEFT_HIP_KNEE_ANKLE =
  Triple(PoseLandmark.LEFT_HIP, PoseLandmark.LEFT_KNEE, PoseLandmark.LEFT_ANKLE)
val RIGHT_HIP_KNEE_ANKLE =
  Triple(PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_KNEE, PoseLandmark.RIGHT_ANKLE)

/*
  LR Shoulder - Hip - Knee
  */
val LEFT_SHOULDER_HIP_KNEE =
  Triple(PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_HIP, PoseLandmark.LEFT_KNEE)
val RIGHT_SHOULDER_HIP_KNEE =
  Triple(PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_KNEE)
  }
}