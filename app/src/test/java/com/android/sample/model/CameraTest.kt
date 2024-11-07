package com.android.sample.model

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.camera.core.CameraSelector
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.video.AudioConfig
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.android.sample.model.camera.CameraViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class CameraTest {

  @get:Rule
  val instantTaskExecutorRule = InstantTaskExecutorRule()

  private val context: Context = ApplicationProvider.getApplicationContext()
  private lateinit var viewModel: CameraViewModel
  private lateinit var cameraController: LifecycleCameraController
  private val testDispatcher = UnconfinedTestDispatcher()


  @Before
  fun setup() {
    Dispatchers.setMain(testDispatcher)
    cameraController = mockk()
    every { cameraController.setEnabledUseCases(any()) } returns Unit
    every { cameraController.cameraSelector = any() } returns Unit

    // Create a mock FileOutputOptions and Recording
    val file = File(context.filesDir.path + "/record.mp4")
    val fileOutputOptions = mockk<FileOutputOptions>()
    val recording = mockk<androidx.camera.video.Recording>()

    // Use any() for all arguments in startRecording
    every {
      cameraController.startRecording(
        any<FileOutputOptions>(), // Specify FileOutputOptions explicitly
        any(),
        any(),
        any()
      )
    } answers {
      recording // Return the mock Recording
    }

    viewModel = CameraViewModel(context)
    viewModel._cameraController.value = cameraController
    viewModel._videoFile.value = file
  }
  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `switchCamera should switch to back camera when current is front`() = runTest {
    every { cameraController.cameraSelector } returns CameraSelector.DEFAULT_FRONT_CAMERA
    viewModel.switchCamera()
    verify { cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA }
  }

  @Test
  fun `switchCamera should switch to front camera when current is back`() = runTest {
    every { cameraController.cameraSelector } returns CameraSelector.DEFAULT_BACK_CAMERA
    viewModel.switchCamera()
    verify { cameraController.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA }
  }

  //Does not call on succes or failure as the user has to lauchn twice record video to create a film (one to start and another to end)
  @Test
  fun `recordVideo should start recording and not call onSuccess`() = runTest {
    val file = File(context.filesDir.path + "/record.mp4")
    val onSuccess = mockk<() -> Unit>()
    val onFailure = mockk<() -> Unit>()
    val onFinishRecording = mockk<() -> Unit>()

    viewModel.recordVideo(onSuccess, onFailure, onFinishRecording)

    verify {
      cameraController.startRecording(
        FileOutputOptions.Builder(file).build(),
        AudioConfig.AUDIO_DISABLED,
        any(),
        any()
      )
    }
    verify(exactly = 0) { onSuccess.invoke() }
    verify(exactly = 0) { onFailure() }
  }


}