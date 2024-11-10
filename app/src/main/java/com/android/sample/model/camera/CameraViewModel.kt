package com.android.sample.model.camera

import android.annotation.SuppressLint
import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.video.AudioConfig

import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * A ViewModel that manages camera operations for video recording.
 *
 * This ViewModel provides functionality for:
 *  - Controlling the camera (switching between front and back cameras)
 *  - Recording videos
 *  - Managing the state of the recording
 *
 * @param context The application context.
 */
@SuppressLint("StaticFieldLeak")
open class CameraViewModel(private val context: Context) : ViewModel() {

  /**
   * A MutableStateFlow that holds the current recording object.
   */
  open val _recording = MutableStateFlow<Recording?>(null)

  /**
   * A StateFlow that exposes the current recording object.
   */
  val recording: StateFlow<Recording?>
    get() = _recording.asStateFlow()

  /**
   * A MutableStateFlow that holds the File object where the video is stored.
   */
  val _videoFile = MutableStateFlow<File>(File(context.filesDir.path + "/record.mp4"))

  /**
   * A StateFlow that exposes the File object where the video is stored.
   */
  val videoFile: StateFlow<File>
    get() = _videoFile.asStateFlow()

  /**
   * A MutableStateFlow that holds the LifecycleCameraController.
   */
  val _cameraController =
    MutableStateFlow<LifecycleCameraController>(
      LifecycleCameraController(context).apply {
        setEnabledUseCases(CameraController.VIDEO_CAPTURE)
        cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
      })

  /**
   * A StateFlow that exposes the LifecycleCameraController.
   */
  val cameraController: StateFlow<LifecycleCameraController>
    get() = _cameraController.asStateFlow()

  /**
   * Switches between the front and back cameras.
   */
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
   * @param onFinishRecording A callback function that is invoked when the video recording is finished (stopped or finalized).
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
          }
        }
      }
  }
}