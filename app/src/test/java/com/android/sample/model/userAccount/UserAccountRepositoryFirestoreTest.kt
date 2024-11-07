package com.android.sample.model.userAccount

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
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
class   UserAccountRepositoryFirestoreTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot

  private lateinit var userAccountRepositoryFirestore: UserAccountRepositoryFirestore

  private val userAccount =
      UserAccount(
          userId = "1",
          firstName = "John",
          lastName = "Doe",
          height = 180f,
          weight = 75f,
          heightUnit = HeightUnit.CM,
          weightUnit = WeightUnit.KG,
          gender = Gender.MALE,
          birthDate = Timestamp.now(),
          profileImageUrl = "profile_image_url")

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    userAccountRepositoryFirestore = UserAccountRepositoryFirestore(mockFirestore)

    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.document()).thenReturn(mockDocumentReference)
  }

  @Test
  fun getUserAccount_success() {
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.toObject(UserAccount::class.java)).thenReturn(userAccount)

    var successCalled = false

    userAccountRepositoryFirestore.getUserAccount(
        "1",
        onSuccess = { account ->
          successCalled = true
          assertTrue(account == userAccount)
        },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).get()
    assertTrue("Success callback should be called", successCalled)
  }

  @Test
  fun getUserAccount_failure() {
    val exception = RuntimeException("Failed to get user account")
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forException(exception))

    var failureCalled = false

    userAccountRepositoryFirestore.getUserAccount(
        "1",
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { failureCalled = true })

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).get()
    assertTrue("Failure callback should be called", failureCalled)
  }

  @Test
  fun createUserAccount_success() {
    `when`(mockDocumentReference.set(userAccount)).thenReturn(Tasks.forResult(null))

    var successCalled = false

    userAccountRepositoryFirestore.createUserAccount(
        userAccount,
        onSuccess = { successCalled = true },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).set(userAccount)
    assertTrue("Success callback should be called", successCalled)
  }

  @Test
  fun createUserAccount_failure() {
    val exception = RuntimeException("Failed to create user account")
    `when`(mockDocumentReference.set(userAccount)).thenReturn(Tasks.forException(exception))

    var failureCalled = false

    userAccountRepositoryFirestore.createUserAccount(
        userAccount,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { failureCalled = true })

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).set(userAccount)
    assertTrue("Failure callback should be called", failureCalled)
  }

  @Test
  fun updateUserAccount_success() {
    `when`(mockDocumentReference.set(userAccount)).thenReturn(Tasks.forResult(null))

    var successCalled = false

    userAccountRepositoryFirestore.updateUserAccount(
        userAccount,
        onSuccess = { successCalled = true },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).set(userAccount)
    assertTrue("Success callback should be called", successCalled)
  }

  @Test
  fun updateUserAccount_failure() {
    val exception = RuntimeException("Failed to update user account")
    `when`(mockDocumentReference.set(userAccount)).thenReturn(Tasks.forException(exception))

    var failureCalled = false

    userAccountRepositoryFirestore.updateUserAccount(
        userAccount,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { failureCalled = true })

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).set(userAccount)
    assertTrue("Failure callback should be called", failureCalled)
  }
}
