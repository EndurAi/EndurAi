package com.android.sample.model.camera

import android.annotation.SuppressLint
import android.content.Context
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
import com.android.sample.mlUtils.CoachFeedback
import com.android.sample.mlUtils.MyPoseLandmark
import com.android.sample.ui.mlFeedback.PoseDetectionAnalyser
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock

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
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
          })

  /** A StateFlow that exposes the LifecycleCameraController. */
  val cameraController: StateFlow<LifecycleCameraController>
    get() = _cameraController.asStateFlow()

  /** A MutableStateFlow that holds the list of detected pose landmarks. */
  val _poseLandMarks = MutableStateFlow<ArrayList<List<MyPoseLandmark>>>(arrayListOf())
  /** A StateFlow that exposes the list of detected pose landmarks. */
  val poseLandmarks: StateFlow<ArrayList<List<MyPoseLandmark>>>
    get() = _poseLandMarks.asStateFlow()
  /** A MutableStateFlow that holds the list of detected pose landmarks. */
val _lastPose = MutableStateFlow<List<MyPoseLandmark>>(arrayListOf())
  /** A StateFlow that exposes the list of detected pose landmarks. */
  val lastPose: StateFlow<List<MyPoseLandmark>>
    get() = _lastPose.asStateFlow()
  var feedback : List<CoachFeedback>? = null


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
    if (_bodyRecognitionIsEnabled.value.not()) {
      _cameraController.value.imageAnalysisTargetSize =
          CameraController.OutputSize(AspectRatio.RATIO_16_9)
      _cameraController.value.setImageAnalysisAnalyzer(
          ContextCompat.getMainExecutor(context),
          PoseDetectionAnalyser(
              onDetectedPoseUpdated = {
                if (it.all { poseLandmark ->
                  poseLandmark.inFrameLikelihood >= inFrameLikelihoodThreshold
                }) {
                  // Convert into simple type
                  val currentPose = it.map { poseLandmark ->
                    val timeStamp = Clock.System.now().toEpochMilliseconds()
                    MyPoseLandmark(
                      poseLandmark.position3D.x,
                      poseLandmark.position3D.y,
                      poseLandmark.position3D.z,
                      poseLandmark.inFrameLikelihood,
                      timeStamp = timeStamp)
                  }
                  _poseLandMarks.value.add(currentPose)
                  _lastPose.value = currentPose
                }
              }))
      _bodyRecognitionIsEnabled.value = true
    }
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
