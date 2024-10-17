import com.android.sample.model.video.Video
import com.android.sample.model.video.VideoRepositoryStorage
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.StorageReference
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.times

class VideoRepositoryStorageTest {

  private lateinit var videoRepository: VideoRepositoryStorage
  private lateinit var mockStorageReference: StorageReference
  private lateinit var mockFirestore: FirebaseFirestore
  private lateinit var mockQuerySnapshot: QuerySnapshot
  private lateinit var mockVideoRef: StorageReference

  @Before
  fun setup() {
    mockStorageReference = Mockito.mock(StorageReference::class.java)
    mockFirestore = Mockito.mock(FirebaseFirestore::class.java)
    mockQuerySnapshot = Mockito.mock(QuerySnapshot::class.java)
    mockVideoRef = Mockito.mock(StorageReference::class.java)

    // Mock the collection method to return a valid CollectionReference
    val mockCollectionReference = Mockito.mock(CollectionReference::class.java)
    Mockito.`when`(mockFirestore.collection("videos")).thenReturn(mockCollectionReference)

    videoRepository = VideoRepositoryStorage(mockStorageReference, mockFirestore)
  }

  @Test
  fun `getVideos should return list of videos from Firestore`() {
    // Arrange
    val mockVideo1 = Video("Video 1", "url1", "tag1", "thumbnail1", "100", "description1")
    val mockVideo2 = Video("Video 2", "url2", "tag2", "thumbnail2", "200", "description2")
    val mockSnapshot = listOf(mockVideo1, mockVideo2)

    val mockFirestoreTask = Tasks.forResult(mockQuerySnapshot)
    Mockito.`when`(mockQuerySnapshot.toObjects(Video::class.java)).thenReturn(mockSnapshot)
    Mockito.`when`(mockFirestore.collection("videos").get()).thenReturn(mockFirestoreTask)

    // Act & Assert
    videoRepository.getVideos(
        onSuccess = { videos ->
          assertEquals(2, videos.size)
          assertTrue(videos.contains(mockVideo1))
          assertTrue(videos.contains(mockVideo2))
        },
        onFailure = { fail("Should not fail") })
  }

  @Test
  fun `getVideos should call onFailure if Firestore retrieval fails`() {
    // Arrange
    val mockFirestoreFailureTask = Tasks.forException<QuerySnapshot>(Exception("Firestore failed"))
    Mockito.`when`(mockFirestore.collection("videos").get()).thenReturn(mockFirestoreFailureTask)

    // Act & Assert
    videoRepository.getVideos(
        onSuccess = { fail("Should not succeed") },
        onFailure = { exception -> assertTrue(exception is Exception) })
  }
}
