package com.android.sample.model.achievements

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.android.sample.model.preferences.Preferences
import com.android.sample.model.preferences.PreferencesRepositoryFirestore
import com.android.sample.model.preferences.UnitsSystem
import com.android.sample.model.preferences.WeightUnit
import com.android.sample.model.workout.WorkoutType
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import junit.framework.TestCase.fail
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import java.time.LocalDateTime

@RunWith(RobolectricTestRunner::class)
class StatisticsRepositoryFirestoreTest {

    @Mock
    private lateinit var mockFirestore: FirebaseFirestore
    @Mock
    private lateinit var mockCollectionReference: CollectionReference
    @Mock
    private lateinit var mockDocumentReference: DocumentReference
    @Mock
    private lateinit var mockSubCollectionReference: CollectionReference
    @Mock
    private lateinit var mockSubDocumentReference: DocumentReference
    @Mock
    private lateinit var mockUser: FirebaseUser
    @Mock
    private lateinit var mockAuth: FirebaseAuth
    private lateinit var firebaseAuthMock: MockedStatic<FirebaseAuth>

    private lateinit var statisticsRepositoryFirestore: StatisticsRepositoryFirestore

    private val workoutStatistics = WorkoutStatistics(id = "test", date = LocalDateTime.now(), caloriesBurnt = 10, type = WorkoutType.BODY_WEIGHT)

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

        statisticsRepositoryFirestore = StatisticsRepositoryFirestore(mockFirestore)

        `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
        `when`(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
        `when`(mockDocumentReference.collection("workouts")).thenReturn(mockSubCollectionReference)
        `when`(mockSubCollectionReference.document(any())).thenReturn(mockSubDocumentReference)
    }

    @After
    fun tearDown() {
        // Clean up the static mock after each test
        firebaseAuthMock.close()
    }

    /**
     * This test verifies that when we add a new workout statistics, the Firestore `set()` is called on
     * the document reference. This does NOT CHECK the actual data being added
     */
    @Test
    fun addWorkoutStatistics_shouldCallFirestoreSet() {
        `when`(mockSubDocumentReference.set(any())).thenReturn(Tasks.forResult(null)) // Simulate success

        statisticsRepositoryFirestore.addWorkout(
            workoutStatistics,
            onSuccess = {},
            onFailure = {})

        shadowOf(Looper.getMainLooper()).idle()

        // Verify that the document reference's set method was called
        verify(mockSubDocumentReference).set(any())
    }

    /**
     * This test verifies that when fetching a Preferences class, the Firestore `get()` is called on
     * the collection reference and not the document reference.
     */
    @Test
    fun getWorkoutStatistics_callsDocuments() {

        val mockQuerySnapshot = mock(QuerySnapshot::class.java)
        val mockDocumentSnapshot = mock(DocumentSnapshot::class.java)

        // Mock behavior for the collection reference to return a QuerySnapshot containing the document
        `when`(mockSubCollectionReference.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
        // Mock behavior for the QuerySnapshot to return a list of DocumentSnapshots
        `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))

        statisticsRepositoryFirestore.getStatistics(
            onSuccess = { documents ->
                // Here you can check the documents received
                assert(documents.isNotEmpty())
            },
            onFailure = { fail("Failure callback should not be called") })

        // Verify that the collection reference's get method was called
        verify(mockSubCollectionReference).get()
    }
}