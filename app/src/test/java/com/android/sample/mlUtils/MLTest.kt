package com.android.sample.mlUtils

import android.util.Log
import com.android.sample.model.camera.CameraViewModel
import com.android.sample.model.workout.ExerciseType
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.PoseLandmark.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`


class MLTest {
    private lateinit var mockCameraViewModel: CameraViewModel
    private lateinit var testImage : MutableStateFlow<ArrayList<List<MyPoseLandmark>>>
    @Before
    fun setup() {
        mockCameraViewModel = mock()
    }
    @Test
    fun testGoodPlankDataGivesGoodFeedback() {
        testImage = MutableStateFlow(ArrayList<List<MyPoseLandmark>>().apply { //Good plank
            repeat(199) {
                add(List(33) { index ->
                    when (index) {
                        LEFT_SHOULDER -> MyPoseLandmark(10.0f, 10.0f, 1.0f, 1.0f,0)
                        RIGHT_SHOULDER -> MyPoseLandmark(10.0f, 10.0f, 2.0f, 1.0f,0)
                        LEFT_HIP -> MyPoseLandmark(20.0f, 11.0f, 3.0f, 1.0f,0)
                        RIGHT_HIP -> MyPoseLandmark(20.0f, 11.0f, 4.0f, 1.0f,0)
                        LEFT_KNEE -> MyPoseLandmark(30.0f, 10.0f, 5.0f, 1.0f,0)
                        RIGHT_KNEE -> MyPoseLandmark(30.0f, 10.0f, 6.0f, 1.0f,0)
                        LEFT_ANKLE -> MyPoseLandmark(40.0f, 10.0f, 7.0f, 1.0f,0)
                        RIGHT_ANKLE -> MyPoseLandmark(40.0f, 10.0f, 8.0f, 1.0f,0)
                        LEFT_ELBOW -> MyPoseLandmark(10.0f, 5.0f, 9.0f, 1.0f,0)
                        RIGHT_ELBOW -> MyPoseLandmark(10.0f, 5.0f, 10.0f, 1.0f,0)
                        else -> MyPoseLandmark(0.0f, 0.0f, 0.0f, 0.0f,0)
                    }
                })
            }
            add(List(33) { index ->
                when (index) {
                    LEFT_SHOULDER -> MyPoseLandmark(10.0f, 10.0f, 1.0f, 1.0f,10000)
                    RIGHT_SHOULDER -> MyPoseLandmark(10.0f, 10.0f, 2.0f, 1.0f,10000)
                    LEFT_HIP -> MyPoseLandmark(20.0f, 11.0f, 3.0f, 1.0f,10000)
                    RIGHT_HIP -> MyPoseLandmark(20.0f, 11.0f, 4.0f, 1.0f,10000)
                    LEFT_KNEE -> MyPoseLandmark(30.0f, 10.0f, 5.0f, 1.0f,10000)
                    RIGHT_KNEE -> MyPoseLandmark(30.0f, 10.0f, 6.0f, 1.0f,10000)
                    LEFT_ANKLE -> MyPoseLandmark(40.0f, 10.0f, 7.0f, 1.0f,10000)
                    RIGHT_ANKLE -> MyPoseLandmark(40.0f, 10.0f, 8.0f, 1.0f,10000)
                    LEFT_ELBOW -> MyPoseLandmark(10.0f, 5.0f, 9.0f, 1.0f,10000)
                    RIGHT_ELBOW -> MyPoseLandmark(10.0f, 5.0f, 10.0f, 1.0f,10000)
                    else -> MyPoseLandmark(0.0f, 0.0f, 0.0f, 0.0f,10000)
                }
            })
        })
        `when`(mockCameraViewModel._poseLandMarks).thenReturn(testImage)
        val mlCoach = MlCoach(mockCameraViewModel, ExerciseType.PLANK)
        val feedback = mlCoach.getFeedback()
        assert(feedback.contains("Duration:10 s"))
        assert(feedback.contains("Success rate: 100.0%"))

    }
}