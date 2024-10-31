// Portions of this code were developed with the help of ChatGPT and github copilot

package com.android.sample.model.video

import android.net.Uri
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import coil.compose.AsyncImagePainter
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.StorageReference
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class VideoRepositoryTest {
  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockStorage: StorageReference
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot
  @Mock private lateinit var mockQuerySnapshot: QuerySnapshot


   private lateinit var videoRepositoryStorage: VideoRepositoryStorage

  private val video = Video(
    title = "Sample Video",
    url = "http://example.com/video.mp4",
    tag = "Sample Tag",
    thumbnailUrl = "http://example.com/thumbnail.jpg",
    duration = "60",
    description = "Sample Description"
  )

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    videoRepositoryStorage = VideoRepositoryStorage(mockStorage, mockFirestore)

    `when`(mockFirestore.collection("videos")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.toObject(Video::class.java)).thenReturn(video)

  }
  @Test
  fun getVideos_success() {

    videoRepositoryStorage.getVideos(
      onSuccess = { videos ->
        assertTrue(videos.contains(video))
      },
      onFailure = {
        fail("Failure callback should not be called") }
    )

    shadowOf(Looper.getMainLooper()).idle()
    verify(mockCollectionReference).get()
  }

  @Test
  fun getVideos_failure() {
    val exception = Exception("Test exception")
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forException(exception))

    var failureCalled = false
    videoRepositoryStorage.getVideos(
      onSuccess = {
        fail("Success callback should not be called")
      },
      onFailure = { error ->
        failureCalled = true
        assertTrue(error.message == "Test exception")
      }
    )

    shadowOf(Looper.getMainLooper()).idle()
    assertTrue(failureCalled)
  }
}

