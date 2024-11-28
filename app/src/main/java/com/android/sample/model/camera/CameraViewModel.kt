package com.android.sample.model.camera

import MathsPoseDetection
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.video.AudioConfig
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.android.sample.mlUtils.ExerciseFeedBack
import com.android.sample.mlUtils.ExerciseFeedBack.Companion.assessLandMarks
import com.android.sample.mlUtils.ExerciseFeedBack.Companion.getCriterions
import com.android.sample.mlUtils.ExerciseFeedBack.Companion.preambleCriterion
import com.android.sample.mlUtils.exercisesCriterions.ChairCriterions
import com.android.sample.mlUtils.exercisesCriterions.PlankExerciseCriterions
import com.android.sample.mlUtils.exercisesCriterions.PushUpsDownCrierions
import com.android.sample.model.workout.ExerciseType
import com.android.sample.ui.mlFeedback.PoseDetectionAnalyser
import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.pose.PoseLandmark
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * A ViewModel that manages camera operations for video recording and pose detection.
 *
 * This ViewModel provides functionality for:
 * - Controlling the camera (switching between front and back cameras)
 * - Recording videos
 * - Managing the state of the recording
 * - Enabling and disabling pose detection
 * - Providing detected pose landmarks
 *
 * @param context The application context.
 */
@SuppressLint("StaticFieldLeak")
open class CameraViewModel(private val context: Context) : ViewModel() {

  /** A MutableStateFlow that holds the current recording object. */
  open val _recording = MutableStateFlow<Recording?>(null)

  /** A StateFlow that exposes the current recording object. */
  val recording: StateFlow<Recording?>
    get() = _recording.asStateFlow()

  /** A MutableStateFlow that holds the File object where the video is stored. */
  val _videoFile = MutableStateFlow<File>(File(context.filesDir.path + "/record.mp4"))

  /** A StateFlow that exposes the File object where the video is stored. */
  val videoFile: StateFlow<File>
    get() = _videoFile.asStateFlow()

  /** A MutableStateFlow that holds the state of body recognition (enabled or disabled). */
  private val _bodyRecognitionIsEnabled = MutableStateFlow(false)

  /** A StateFlow that exposes the state of body recognition (enabled or disabled). */
  val bodyRecognitionIsEnabled: StateFlow<Boolean>
    get() = _bodyRecognitionIsEnabled.asStateFlow()

  /**
   * A MutableStateFlow that holds the LifecycleCameraController. Initialized with the front camera
   * selected.
   */
  val _cameraController =
      MutableStateFlow<LifecycleCameraController>(
          LifecycleCameraController(context).apply {
            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
          })

  /** A StateFlow that exposes the LifecycleCameraController. */
  val cameraController: StateFlow<LifecycleCameraController>
    get() = _cameraController.asStateFlow()

  /** A MutableStateFlow that holds the list of detected pose landmarks. */
  val _poseLandMarks = MutableStateFlow<ArrayList<List<PoseLandmark>>>(arrayListOf())
  /** A StateFlow that exposes the list of detected pose landmarks. */
  val poseLandmarks: StateFlow<ArrayList<List<PoseLandmark>>>
    get() = _poseLandMarks.asStateFlow()

  /** A MutableStateFlow that holds the list of detected pose landmarks. */
  val _poseLandMarks_means = MutableStateFlow<ArrayList<List<PointF3D>>>(arrayListOf())
  /** A StateFlow that exposes the list of detected pose landmarks. */
  val poseLandmarks_means: StateFlow<ArrayList<List<PointF3D>>>
    get() = _poseLandMarks_means.asStateFlow()

  val meanWindow = 10
  private val inFrameLikelihoodThreshold = 0.8f

  /** Switches between the front and back cameras. */
  fun switchCamera() {
    when (_cameraController.value.cameraSelector) {
      CameraSelector.DEFAULT_FRONT_CAMERA -> {
        _cameraController.value.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
      }
      CameraSelector.DEFAULT_BACK_CAMERA -> {
        _cameraController.value.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
      }
    }
  }

  /**
   * Starts or stops recording a video.
   *
   * @param onSuccess A callback function that is invoked when the video recording is successful.
   * @param onFailure A callback function that is invoked when the video recording fails.
   * @param onFinishRecording A callback function that is invoked when the video recording is
   *   finished (stopped or finalized).
   * @param onStarting A callback function that is invoked when the video recording is starting.
   */
  fun recordVideo(
      onSuccess: () -> Unit,
      onFailure: () -> Unit,
      onFinishRecording: () -> Unit,
      onStarting: () -> Unit = {}
  ) {
    if (_recording.value != null) {
      _recording.value?.stop()
      onFinishRecording()
      _recording.value = null
      return
    }

    onStarting()
    switchVideoCaptureUseCase() // Allow the camera to Record the video
    _recording.value =
        _cameraController.value.startRecording(
            FileOutputOptions.Builder(_videoFile.value).build(),
            AudioConfig.AUDIO_DISABLED,
            ContextCompat.getMainExecutor(context)) { event ->
              when (event) {
                is VideoRecordEvent.Finalize -> {
                  if (event.hasError()) {
                    _recording.value?.close()
                    _recording.value = null
                    onFailure()
                  } else {
                    onSuccess()
                  }
                  resetCameraController() // This allows reusability of the viewModel in the body
                  // recognition mode
                }
              }
            }
  }

  /** Switches the camera controller to the video capture use case. */
  private fun switchVideoCaptureUseCase() {
    _cameraController.value.setEnabledUseCases(CameraController.VIDEO_CAPTURE)
  }

  /** Resets the camera controller to its initial state with the front camera selected. */
  private fun resetCameraController() {
    finishPoseRecognition()
    _cameraController.value =
        LifecycleCameraController(context).apply {
          cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        }
  }

  /** Enables pose recognition by setting up the image analysis analyzer. */
  fun enablePoseRecognition() {
    val windowSize = 10 // Window Size used to compute the mean

    if (_bodyRecognitionIsEnabled.value.not()) {
      _cameraController.value.imageAnalysisTargetSize =
          CameraController.OutputSize(AspectRatio.RATIO_16_9)
      _cameraController.value.setImageAnalysisAnalyzer(
          ContextCompat.getMainExecutor(context),
          PoseDetectionAnalyser(
              onDetectedPoseUpdated = {
                if (poseLandmarks.value.size > windowSize) {
                  val lastLandMark = poseLandmarks.value.takeLast(windowSize)
                  val meanedLandmark = MathsPoseDetection.window_mean(lastLandMark)
/*
                  val assessedChair =
                      ExerciseFeedBack.assessLandMarks(meanedLandmark, ChairCriterions)
                  Log.d("MLFEEDBACK_RESULTChair", "chair: $assessedChair ")
                  val assessedPlank =
                      ExerciseFeedBack.assessLandMarks(meanedLandmark, PlankExerciseCriterions)
                  Log.d("MLFEEDBACK_RESULTPlank", "Plank: $assessedPlank ")
*/

                  val assessedPushUpsDown =
                    ExerciseFeedBack.assessLandMarks(meanedLandmark, PushUpsDownCrierions)
                  Log.d("MLFEEDBACK_RESULTPlank", "PushUpsDown: $assessedPushUpsDown ")



                }
                if (it.all { poseLandmark ->
                  poseLandmark.inFrameLikelihood >= inFrameLikelihoodThreshold
                })
                    _poseLandMarks.value.add(it)
              }))
      _bodyRecognitionIsEnabled.value = true
    }
  }

  fun enablePoseRecognition(exerciseType : ExerciseType): String {
    var exerciseWasDetected = false
    val criterions = getCriterions(exerciseType)
    val preamble = preambleCriterion(criterions, onSuccess =  {
      exerciseWasDetected = true //When the preamble criterions succeed, then we start assessing the exercise
    },
      onFailure = {
        exerciseWasDetected = false
      })
    _bodyRecognitionIsEnabled.value = true
    _cameraController.value.imageAnalysisTargetSize =
        CameraController.OutputSize(AspectRatio.RATIO_16_9)
    _cameraController.value.setImageAnalysisAnalyzer(
      ContextCompat.getMainExecutor(context),
      PoseDetectionAnalyser(
        onDetectedPoseUpdated = {
          // If the new pose is detected, we add it to the list of poses
          if (it.all { poseLandmark ->
            poseLandmark.inFrameLikelihood >= inFrameLikelihoodThreshold
          }) {
            _poseLandMarks.value.add(it)
          }
          if (poseLandmarks.value.size > meanWindow) {
            val lastLandMark = poseLandmarks.value.takeLast(meanWindow)
            val meanedLandmark = MathsPoseDetection.window_mean(lastLandMark)

            //Check if the user is trying to do the exercise, this can switch exerciseWasDetected to true or false
            assessLandMarks(meanedLandmark, preamble)

            if (exerciseWasDetected) {
              assessLandMarks(meanedLandmark, criterions)
            }

          }

        }
      )
    )
  }

  /**
   * Disables pose recognition by clearing the image analysis analyzer and emptying the landMarks
   * list.
   */
  fun finishPoseRecognition() {
    _cameraController.value.clearImageAnalysisAnalyzer()
    _poseLandMarks.value = arrayListOf()
    _bodyRecognitionIsEnabled.value = false
  }
}
