package com.android.sample.model.workout

import com.google.firebase.firestore.QuerySnapshot
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
import junit.framework.TestCase.fail
import org.junit.After
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
    @Mock private lateinit var mockCollectionReference: CollectionReference
    @Mock private lateinit var mockDocumentReference: DocumentReference
    @Mock private lateinit var mockUser: FirebaseUser
    @Mock private lateinit var mockAuth: FirebaseAuth
    private lateinit var firebaseAuthMock: MockedStatic<FirebaseAuth>

    private lateinit var workoutRepositoryFirestore1: WorkoutRepositoryFirestore<BodyWeightWorkout>
    private lateinit var workoutRepositoryFirestore2: WorkoutRepositoryFirestore<YogaWorkout>


    private val bodyWeightWorkout = BodyWeightWorkout(
        workoutId = "workout-1",
        name = "Morning Workout",
        description = "A great way to start your day!",
        warmup = true
    )

    private val yogaWorkout = YogaWorkout(
        workoutId = "workout-2",
        name = "Workout after Bugnion lesson",
        description = "A great way to change my mind!",
        warmup = false
    )

    @Before
    fun setUp() {

        MockitoAnnotations.openMocks(this)

        if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
            FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
        }

        firebaseAuthMock = mockStatic(FirebaseAuth::class.java)

        // Mock FirebaseAuth and FirebaseUser behavior
        `when`(FirebaseAuth.getInstance()).thenReturn(mockAuth)
        `when`(mockAuth.currentUser).thenReturn(mockUser)
        `when`(mockUser.uid).thenReturn("mocked-uid")

        // Test with BodyWeightWorkout
        workoutRepositoryFirestore1 = WorkoutRepositoryFirestore(mockFirestore, BodyWeightWorkout::class.java)

        `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
        `when`(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
        `when`(mockDocumentReference.collection(any())).thenReturn(mockCollectionReference)
    }

    @After
    fun tearDown() {
        // Clean up the static mock after each test
        firebaseAuthMock.close()
    }

    /**
     * This test verifies that when we add a BodyWeightWorkout, the Firestore `set()` is called on
     * the document reference. This does NOT CHECK the actual data being added
     */
    @Test
    fun addDocument_shouldCallFirestoreSet() {

        `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null)) // Simulate success

        workoutRepositoryFirestore1.addDocument(bodyWeightWorkout, onSuccess = {}, onFailure = {})

        shadowOf(Looper.getMainLooper()).idle()

        // Verify that the document reference's set method was called
        verify(mockDocumentReference).set(any())
    }

    /**
     * This test verifies that when fetching documents, the Firestore `get()` is called on
     * the collection reference and not the document reference.
     */
    @Test
    fun getDocuments_callsCollectionGet() {

        val mockQuerySnapshot = mock(QuerySnapshot::class.java)
        val mockDocumentSnapshot = mock(DocumentSnapshot::class.java)

        // Mock behavior for the collection reference to return a QuerySnapshot containing the document
        `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
        // Mock behavior for the QuerySnapshot to return a list of DocumentSnapshots
        `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))

        workoutRepositoryFirestore1.getDocuments(
            onSuccess = { documents ->
                // Here you can check the documents received
                assert(documents.isNotEmpty()) // Example assertion
            },
            onFailure = { fail("Failure callback should not be called") }
        )

        // Verify that the collection reference's get method was called
        verify(mockCollectionReference).get()
    }

    /**
     * This check verifies that the correct Firestore method is called when deleting a workout.
     * Does NOT CHECK that the correct data is deleted.
     */
    @Test
    fun deleteDocument_shouldCallFirestoreDelete() {

        `when`(mockDocumentReference.delete()).thenReturn(Tasks.forResult(null))

        workoutRepositoryFirestore1.deleteDocument("workout-1", onSuccess = {}, onFailure = {})

        shadowOf(Looper.getMainLooper()).idle()

        // Verify that the document reference's delete method was called
        verify(mockDocumentReference).delete()
    }

    /**
     * This test verifies that when we update a BodyWeightWorkout, the Firestore `set()` is called on
     * the document reference. This does NOT CHECK the actual data being added
     */
    @Test
    fun updateDocument_shouldCallFirestoreSet() {

        `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null)) // Simulate success

        workoutRepositoryFirestore1.updateDocument(bodyWeightWorkout, onSuccess = {}, onFailure = {})

        shadowOf(Looper.getMainLooper()).idle()

        // Verify that the document reference's set method was called
        verify(mockDocumentReference).set(any())
    }

    /**
     * This test verifies that when we add a YogaWorkout, the Firestore `set()` is called on
     * the document reference. This does NOT CHECK the actual data being added
     */
    @Test
    fun addYogaWorkout_shouldCallFirestoreSet() {

        // Change the repository to YogaWorkout for this test
        workoutRepositoryFirestore2 = WorkoutRepositoryFirestore(mockFirestore, YogaWorkout::class.java)
        `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null)) // Simulate success

        workoutRepositoryFirestore2.addDocument(yogaWorkout, onSuccess = {}, onFailure = {})

        shadowOf(Looper.getMainLooper()).idle()

        // Verify that the document reference's set method was called
        verify(mockDocumentReference).set(any())
    }

    /**
     * This test verifies that when we add a BodyWeightWorkout, the Firestore `set()` is called on
     * the document reference. This does NOT CHECK the actual data being added
     */
    @Test
    fun addBodyWeightWorkout_shouldCallFirestoreSet() {

        // Change the repository to YogaWorkout for this test
        workoutRepositoryFirestore1 = WorkoutRepositoryFirestore(mockFirestore, BodyWeightWorkout::class.java)
        `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null)) // Simulate success

        workoutRepositoryFirestore1.addDocument(bodyWeightWorkout, onSuccess = {}, onFailure = {})

        shadowOf(Looper.getMainLooper()).idle()

        // Verify that the document reference's set method was called
        verify(mockDocumentReference).set(any())
    }
}
