package com.android.sample.model.camera

import android.content.Context
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.video.Recording
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

open class CameraViewModel(context : Context) : ViewModel() {
  private val _selectedCamera = MutableStateFlow<CameraSelector>(CameraSelector.DEFAULT_FRONT_CAMERA)
  val selectedCamera : StateFlow<CameraSelector> get() = _selectedCamera.asStateFlow()

  private val _recording = MutableStateFlow<Recording?>(null)
  val recording : StateFlow<Recording?> get() = _recording.asStateFlow()


  private val _videoFile = MutableStateFlow<File>(File(context.filesDir.path + "/record.mp4"))
  val videoFile : StateFlow<File> get() = _videoFile.asStateFlow()







}