package com.android.sample.model.preferences

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import junit.framework.TestCase.fail
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.timeout
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class PreferencesRepositoryFirestoreTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockQuerySnapshot: QuerySnapshot
  @Mock private lateinit var mockUser: FirebaseUser
  @Mock private lateinit var mockAuth: FirebaseAuth
  private lateinit var firebaseAuthMock: MockedStatic<FirebaseAuth>

  private lateinit var preferencesRepositoryFirestore: PreferencesRepositoryFirestore

  private val preferences = Preferences(unitsSystem = UnitsSystem.METRIC, weight = WeightUnit.KG)

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

    preferencesRepositoryFirestore = PreferencesRepositoryFirestore(mockFirestore)

    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
  }

  @After
  fun tearDown() {
    // Clean up the static mock after each test
    firebaseAuthMock.close()
  }

  /**
   * This test verifies that when we update a new Preferences, the Firestore `set()` is called on
   * the document reference. This does NOT CHECK the actual data being added
   */
  @Test
  fun updatePreferences_shouldCallFirestoreCollection() {
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null)) // Simulate success

    preferencesRepositoryFirestore.updatePreferences(preferences, onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    // Verify that the document reference's set method was called
    verify(mockDocumentReference).set(any())
  }

  /**
   * This test verifies that when fetching a Preferences class, the Firestore `get()` is called on
   * the collection reference and not the document reference.
   */
  @Test
  fun getPreferences_callsDocuments() {
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    `when`(mockQuerySnapshot.documents).thenReturn(listOf())

    preferencesRepositoryFirestore.getPreferences(
        onSuccess = {}, onFailure = { fail("Failure callback should not be called") })

    verify(timeout(100)) { (mockQuerySnapshot).documents }
  }

  /**
   * This check that the correct Firestore method is called when deleting. Does NOT CHECK that the
   * correct data is deleted.
   */
  @Test
  fun deletePreferences_shouldCallFirestoreDelete() {
    `when`(mockDocumentReference.delete()).thenReturn(Tasks.forResult(null))

    preferencesRepositoryFirestore.deletePreferences(onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    // Verify that the document reference's delete method was called
    verify(mockDocumentReference).delete()
  }
}
