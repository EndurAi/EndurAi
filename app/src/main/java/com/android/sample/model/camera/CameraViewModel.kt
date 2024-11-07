package com.android.sample.model.camera

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.video.AudioConfig
import androidx.camera.view.video.ExperimentalVideo
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.android.sample.ui.composables.CameraFeedBack
import com.android.sample.ui.composables.CameraFeedBack.Companion
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

@SuppressLint("StaticFieldLeak")
@ExperimentalVideo
open class CameraViewModel(private val context: Context) : ViewModel() {


  private val _recording = MutableStateFlow<Recording?>(null)
  val recording : StateFlow<Recording?> get() = _recording.asStateFlow()


  private val _videoFile = MutableStateFlow<File>(File(context.filesDir.path + "/record.mp4"))
  val videoFile : StateFlow<File> get() = _videoFile.asStateFlow()


private val _cameraController = MutableStateFlow<LifecycleCameraController>(
  LifecycleCameraController(context).apply {
    setEnabledUseCases(CameraController.VIDEO_CAPTURE)
    cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
  }
)
  val cameraController : StateFlow<LifecycleCameraController> get() = _cameraController.asStateFlow()


  fun switchCamera(){
    when(_cameraController.value.cameraSelector){
      CameraSelector.DEFAULT_FRONT_CAMERA -> {
  _cameraController.value.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA }
CameraSelector.DEFAULT_BACK_CAMERA -> {
  _cameraController.value.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA }
    }
  }


  fun recordVideo(onSuccess: ()-> Unit, onFailure: ()-> Unit, onFinishRecording: ()-> Unit){

    if (_recording.value != null) {
      _recording.value?.stop()
      onFinishRecording()
      _recording.value = null
      return
    }

    _recording.value = _cameraController.value.startRecording(
      FileOutputOptions.Builder(_videoFile.value).build(),
      AudioConfig.AUDIO_DISABLED,
      ContextCompat.getMainExecutor(context)){
      event ->
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