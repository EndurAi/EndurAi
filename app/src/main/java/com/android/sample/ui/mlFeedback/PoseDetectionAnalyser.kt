package com.android.sample.ui.mlFeedback

import android.media.Image
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PoseDetectionAnalyser(private val onDetectedPoseUpdated: (List<PoseLandmark>) -> Unit) :
    ImageAnalysis.Analyzer {

  companion object {
    const val THROTTLE_TIMEOUT_MS = 15L
  }

  private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

  private val options =
      PoseDetectorOptions.Builder().setDetectorMode(PoseDetectorOptions.STREAM_MODE).build()

  private val poseDetector = PoseDetection.getClient(options)

  @OptIn(ExperimentalGetImage::class)
  override fun analyze(imageProxy: ImageProxy) {
    Log.d("MLDEBUG", "analyze:ANALIZING ")
    scope
        .launch {
          val mediaImage: Image =
              imageProxy.image
                  ?: run {
                    imageProxy.close()
                    return@launch
                  }
          val inputImage: InputImage =
              InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

          suspendCoroutine { continuation ->
            poseDetector
                .process(inputImage)
                .addOnSuccessListener { pose: Pose ->
                  val listOfLandmark = pose.allPoseLandmarks
                  if (listOfLandmark.isNotEmpty()) {
                    onDetectedPoseUpdated(listOfLandmark)
                  }
                }
                .addOnCompleteListener {
                  Log.d("MLDEBUG", "analyze:complete ")
                  continuation.resume(Unit)
                }
          }

          delay(THROTTLE_TIMEOUT_MS)
        }
        .invokeOnCompletion { exception ->
          exception?.printStackTrace()
          imageProxy.close()
        }
  }
}
