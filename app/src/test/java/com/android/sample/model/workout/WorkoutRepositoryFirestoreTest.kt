package com.android.sample.model.workout

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.time.LocalDateTime
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class WorkoutRepositoryFirestoreTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockCollectionPath: CollectionReference
  @Mock private lateinit var mockDocumentWorkoutID: DocumentReference
  @Mock private lateinit var mockMainDocumentName: CollectionReference
  @Mock private lateinit var mockDocumentToCollectionName: DocumentReference
  @Mock private lateinit var mockDocumentWorkout: DocumentReference
  @Mock private lateinit var mockCollectionDocumentName: CollectionReference
  @Mock private lateinit var mockDoneDocumentName: CollectionReference
  @Mock private lateinit var mockDoneDocumentToCollectionName: DocumentReference
  @Mock private lateinit var mockDocumentDoneWorkout: DocumentReference
  @Mock private lateinit var mockDocumentDoneWorkoutID: DocumentReference
  @Mock private lateinit var mockCollectionDoneDocumentName: CollectionReference
  @Mock private lateinit var mockUser: FirebaseUser
  @Mock private lateinit var mockAuth: FirebaseAuth
  @Mock private lateinit var mockLocalCache: WorkoutLocalCache
  private lateinit var firebaseAuthMock: MockedStatic<FirebaseAuth>

  private lateinit var workoutRepositoryFirestore1: WorkoutRepositoryFirestore<BodyWeightWorkout>
  private lateinit var workoutRepositoryFirestore2: WorkoutRepositoryFirestore<YogaWorkout>

  private val mainDocumentName = "AllWorkouts"
  private val collectionPath: String = "mocked-uid"
  private val documentToCollectionName: String = "Workouts"
  private val doneDocumentName = "DoneWorkouts"

  private val bodyWeightWorkout =
      BodyWeightWorkout(
          workoutId = "workout-1",
          name = "Morning Workout",
          description = "A great way to start your day!",
          date = LocalDateTime.of(2024, 11, 1, 0, 42),
          warmup = true)

  private val yogaWorkout =
      YogaWorkout(
          workoutId = "workout-2",
          name = "Workout after Bugnion lesson",
          description = "A great way to change my mind!",
          date = LocalDateTime.of(2024, 11, 1, 0, 42),
          warmup = false)

  @Before
  fun setUp() {

    MockitoAnnotations.openMocks(this)

    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    // Mock LocalCache behavior
    `when`(mockLocalCache.getWorkouts()).thenReturn(flowOf(emptyList()))

    firebaseAuthMock = mockStatic(FirebaseAuth::class.java)

    // Mock FirebaseAuth and FirebaseUser behavior
    `when`(FirebaseAuth.getInstance()).thenReturn(mockAuth)
    `when`(mockAuth.currentUser).thenReturn(mockUser)
    `when`(mockUser.uid).thenReturn("mocked-uid")

    // Test with BodyWeightWorkout
    workoutRepositoryFirestore1 =
        WorkoutRepositoryFirestore(mockFirestore, mockLocalCache, BodyWeightWorkout::class.java)

    // Mock FirebaseAuth and FirebaseUser behavior
    `when`(FirebaseAuth.getInstance()).thenReturn(mockAuth)
    `when`(mockAuth.currentUser).thenReturn(mockUser)
    `when`(mockUser.uid).thenReturn("mocked-uid")


    `when`(mockFirestore.collection(collectionPath)).thenReturn(mockCollectionPath)

    // Mock main document name and its behavior
    `when`(mockFirestore.collection(mainDocumentName)).thenReturn(mockMainDocumentName)
    val mockDocumentSnapshot = mock(DocumentSnapshot::class.java)
    `when`(mockDocumentSnapshot.exists()).thenReturn(true) // Simulate that the document exists
    `when`(mockDocumentSnapshot.data).thenReturn(mapOf("key" to "value")) // Simulate document data
    `when`(mockMainDocumentName.document(any())).thenReturn(mockDocumentWorkout)
    `when`(mockDocumentWorkout.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))

    // Mock done workouts path
    `when`(mockFirestore.collection(doneDocumentName)).thenReturn(mockDoneDocumentName)
    `when`(mockDoneDocumentName.document(any())).thenReturn(mockDocumentDoneWorkout)

    `when`(mockCollectionPath.document(doneDocumentName))
        .thenReturn(mockDoneDocumentToCollectionName)
    `when`(mockDoneDocumentToCollectionName.collection(any()))
        .thenReturn(mockCollectionDoneDocumentName)
    `when`(mockCollectionDoneDocumentName.document(any())).thenReturn(mockDocumentDoneWorkoutID)

    // Mock workouts path
    `when`(mockCollectionPath.document(documentToCollectionName))
        .thenReturn(mockDocumentToCollectionName)
    `when`(mockDocumentToCollectionName.collection(any())).thenReturn(mockCollectionDocumentName)
    `when`(mockCollectionDocumentName.document(any())).thenReturn(mockDocumentWorkoutID)
  }

  @After
  fun tearDown() {
    // Clean up the static mock after each test
    firebaseAuthMock.close()
  }

  /**
   * This test verifies that when we add a BodyWeightWorkout, the Firestore `set()` is called on the
   * document reference. This does NOT CHECK the actual data being added
   */
  @Test
  fun addDocument_shouldCallFirestoreSet() {

    `when`(mockDocumentWorkout.set(any())).thenReturn(Tasks.forResult(null)) // Simulate success
    `when`(mockDocumentWorkoutID.set(any())).thenReturn(Tasks.forResult(null))

    workoutRepositoryFirestore1.addDocument(bodyWeightWorkout, onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    // Verify that the document reference's set method was called
    verify(mockDocumentWorkout).set(any())
    verify(mockDocumentWorkoutID).set(any())
  }

  /**
   * This check verifies that the correct Firestore method is called when deleting a workout. Does
   * NOT CHECK that the correct data is deleted.
   */
  @Test
  fun deleteDocument_shouldCallFirestoreDelete() {

    `when`(mockDocumentWorkout.delete()).thenReturn(Tasks.forResult(null)) // Simulate success
    `when`(mockDocumentWorkoutID.delete()).thenReturn(Tasks.forResult(null))

    workoutRepositoryFirestore1.deleteDocument("workout-1", onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    // Verify that the document reference's delete method was called
    verify(mockDocumentWorkoutID).delete()
    verify(mockDocumentWorkout).delete()
  }

  /**
   * This test verifies that when we update a BodyWeightWorkout, the Firestore `set()` is called on
   * the document reference. This does NOT CHECK the actual data being added
   */
  @Test
  fun updateDocument_shouldCallFirestoreSet() {

    `when`(mockDocumentWorkout.set(any())).thenReturn(Tasks.forResult(null)) // Simulate success

    workoutRepositoryFirestore1.updateDocument(bodyWeightWorkout, onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    // Verify that the document reference's set method was called
    verify(mockDocumentWorkout).set(any())
  }

  /**
   * This test verifies that when we add a YogaWorkout, the Firestore `set()` is called on the
   * document reference. This does NOT CHECK the actual data being added
   */
  @Test
  fun addYogaWorkout_shouldCallFirestoreSet() {
    // Change the repository to YogaWorkout for this test
    workoutRepositoryFirestore2 =
        WorkoutRepositoryFirestore(mockFirestore, mockLocalCache, YogaWorkout::class.java)

    `when`(mockDocumentWorkout.set(any())).thenReturn(Tasks.forResult(null)) // Simulate success
    `when`(mockDocumentWorkoutID.set(any())).thenReturn(Tasks.forResult(null))

    workoutRepositoryFirestore2.addDocument(yogaWorkout, onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    // Verify that the document reference's set method was called
    verify(mockDocumentWorkout).set(any())
    verify(mockDocumentWorkoutID).set(any())
  }

  /**
   * Test that `transferDocumentToDone` set the values in the correct document and delete from the
   * old one
   */
  @Test
  fun transferDocumentToDone_shouldTransferAndDelete() {
    val mockDocumentSnapshot = mock(DocumentSnapshot::class.java)
    `when`(mockDocumentSnapshot.exists()).thenReturn(true) // Simulate the existence of the document
    `when`(mockDocumentSnapshot.data).thenReturn(mapOf("key" to "value")) // Simulated data
    `when`(mockDocumentWorkout.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))

    `when`(mockDocumentDoneWorkout.set(any()))
        .thenReturn(Tasks.forResult(null)) // Simulate success for done workout transfer
    `when`(mockDocumentDoneWorkoutID.set(any())).thenReturn(Tasks.forResult(null))
    `when`(mockDocumentWorkout.delete())
        .thenReturn(Tasks.forResult(null)) // Simulate success for deletion of the previous workout
    `when`(mockDocumentWorkoutID.delete()).thenReturn(Tasks.forResult(null))

    workoutRepositoryFirestore1.transferDocumentToDone(
        id = "workout-1", onSuccess = {}, onFailure = { fail("Transfer should not fail") })

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockMainDocumentName, times(2)).document("workout-1")
    verify(mockDocumentDoneWorkout).set(any()) // Verifiy added to the done documents
    verify(mockDocumentWorkout).delete() // Verify workout deleted from previous location
    verify(mockDocumentWorkoutID).delete() // Verify workout ID deleted from previous location
    verify(mockDocumentDoneWorkoutID)
        .set(any()) // Verify ID added to the list of done workouts of the user
  }

  @Test
  fun importDocumentFromDone_shouldImportAndDelete() {
    val id = "workout-1"

    val mockDocumentSnapshot = mock(DocumentSnapshot::class.java)

    // Simulate that the document exists and contains some data
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)
    `when`(mockDocumentSnapshot.data).thenReturn(mapOf("key" to "value"))

    // Mock Firestore calls to get the document from done workout collection
    `when`(mockDocumentDoneWorkout.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))

    // Simulate successful Firestore operations for setting and deleting documents
    `when`(mockDocumentWorkout.set(any())).thenReturn(Tasks.forResult(null))
    `when`(mockDocumentWorkoutID.set(any())).thenReturn(Tasks.forResult(null))

    `when`(mockDocumentDoneWorkout.delete()).thenReturn(Tasks.forResult(null))
    `when`(mockDocumentDoneWorkoutID.delete()).thenReturn(Tasks.forResult(null))

    // Call the method to test
    workoutRepositoryFirestore1.importDocumentFromDone(
        id = id, onSuccess = {}, onFailure = { fail("Import should not fail") })

    shadowOf(Looper.getMainLooper()).idle()

    // Verify interactions with mock references
    verify(mockDoneDocumentName, times(2))
        .document("workout-1") // Ensure retrieval of the workout document
    verify(mockDocumentDoneWorkout)
        .delete() // Ensure the workout is added back to the main collection
    verify(mockDocumentDoneWorkoutID)
        .delete() // Ensure the workout id is added back to the main collection
    verify(mockDocumentWorkout)
        .set(any()) // Ensure the workout is removed from the previous collection
    verify(mockDocumentWorkoutID).set(any()) // Verify the deletion of workout IDs
  }

  /** This test verifies that when fetching done documents, the Firestore `get()` is called. */
  @Test
  fun getDoneDocuments_callsCollectionGet() {
    // Mock QuerySnapshot
    val mockQuerySnapshot = mock(QuerySnapshot::class.java)
    val mockDocumentSnapshot1 = mock(DocumentSnapshot::class.java)
    val mockDocumentSnapshot2 = mock(DocumentSnapshot::class.java)

    // Mock responses
    `when`(mockDocumentSnapshot1.exists()).thenReturn(true)
    `when`(mockDocumentSnapshot2.exists()).thenReturn(true)

    `when`(mockQuerySnapshot.documents)
        .thenReturn(listOf(mockDocumentSnapshot1, mockDocumentSnapshot2))
    `when`(mockCollectionDoneDocumentName.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    workoutRepositoryFirestore1.getDoneDocuments(
        onSuccess = { documents -> assert(documents.isNotEmpty()) },
        onFailure = { fail("Failure callback should not be called") })

    // Verify that the collection reference's get method was called
    verify(mockCollectionDoneDocumentName).get()
  }
}
