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
import com.google.firebase.firestore.Transaction
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.invocation.InvocationOnMock
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class UserAccountRepositoryFirestoreTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot

  @Mock private lateinit var mockTransaction: Transaction

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

  @Test
  fun acceptFriendRequest_success() {
    val friendId = "2"
    val updatedUserAccount =
        userAccount.copy(
            friends = userAccount.friends + friendId,
            receivedRequests = userAccount.receivedRequests - friendId)
    val friendAccount =
        userAccount.copy(userId = friendId, sentRequests = listOf(userAccount.userId))
    val updatedFriendAccount =
        friendAccount.copy(
            friends = friendAccount.friends + userAccount.userId,
            sentRequests = friendAccount.sentRequests - userAccount.userId)

    `when`(mockFirestore.runTransaction(any<Transaction.Function<Void>>())).thenAnswer {
        invocation: InvocationOnMock ->
      val transactionFunction = invocation.arguments[0] as Transaction.Function<Void>
      transactionFunction.apply(mockTransaction)
      Tasks.forResult<Void>(null)
    }

    `when`(mockTransaction.get(mockDocumentReference)).thenReturn(mockDocumentSnapshot)
    `when`(mockDocumentSnapshot.toObject(UserAccount::class.java)).thenReturn(friendAccount)
    `when`(mockTransaction.set(mockDocumentReference, updatedFriendAccount))
        .thenReturn(mockTransaction)
    `when`(mockTransaction.set(mockDocumentReference, updatedUserAccount))
        .thenReturn(mockTransaction)

    var successCalled = false

    userAccountRepositoryFirestore.acceptFriendRequest(
        userAccount,
        friendId,
        onSuccess = { successCalled = true },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockFirestore).runTransaction(any<Transaction.Function<Void>>())
    verify(mockTransaction).get(mockDocumentReference)
    verify(mockTransaction).set(mockDocumentReference, updatedFriendAccount)
    verify(mockTransaction).set(mockDocumentReference, updatedUserAccount)
    assertTrue("Success callback should be called", successCalled)
  }

  @Test
  fun rejectFriendRequest_success() {
    val friendId = "2"
    val updatedUserAccount =
        userAccount.copy(receivedRequests = userAccount.receivedRequests - friendId)
    val friendAccount =
        userAccount.copy(userId = friendId, sentRequests = listOf(userAccount.userId))
    val updatedFriendAccount =
        friendAccount.copy(sentRequests = friendAccount.sentRequests - userAccount.userId)

    `when`(mockFirestore.runTransaction(any<Transaction.Function<Void>>())).thenAnswer {
        invocation: InvocationOnMock ->
      val transactionFunction = invocation.arguments[0] as Transaction.Function<Void>
      transactionFunction.apply(mockTransaction)
      Tasks.forResult<Void>(null)
    }

    `when`(mockTransaction.get(mockDocumentReference)).thenReturn(mockDocumentSnapshot)
    `when`(mockDocumentSnapshot.toObject(UserAccount::class.java)).thenReturn(friendAccount)
    `when`(mockTransaction.set(mockDocumentReference, updatedFriendAccount))
        .thenReturn(mockTransaction)
    `when`(mockTransaction.set(mockDocumentReference, updatedUserAccount))
        .thenReturn(mockTransaction)

    var successCalled = false

    userAccountRepositoryFirestore.rejectFriendRequest(
        userAccount,
        friendId,
        onSuccess = { successCalled = true },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockFirestore).runTransaction(any<Transaction.Function<Void>>())
    verify(mockTransaction).get(mockDocumentReference)
    verify(mockTransaction).set(mockDocumentReference, updatedFriendAccount)
    verify(mockTransaction).set(mockDocumentReference, updatedUserAccount)
    assertTrue("Success callback should be called", successCalled)
  }

  @Test
  fun acceptFriendRequest_failure() {
    val friendId = "2"
    val exception = RuntimeException("Failed to accept friend request")

    `when`(mockFirestore.runTransaction(any<Transaction.Function<Void>>()))
        .thenReturn(Tasks.forException(exception))

    var failureCalled = false

    userAccountRepositoryFirestore.acceptFriendRequest(
        userAccount,
        friendId,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { failureCalled = true })

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockFirestore).runTransaction(any<Transaction.Function<Void>>())
    assertTrue("Failure callback should be called", failureCalled)
  }

  @Test
  fun rejectFriendRequest_failure() {
    val friendId = "2"
    val exception = RuntimeException("Failed to reject friend request")

    `when`(mockFirestore.runTransaction(any<Transaction.Function<Void>>()))
        .thenReturn(Tasks.forException(exception))

    var failureCalled = false

    userAccountRepositoryFirestore.rejectFriendRequest(
        userAccount,
        friendId,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { failureCalled = true })

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockFirestore).runTransaction(any<Transaction.Function<Void>>())
    assertTrue("Failure callback should be called", failureCalled)
  }

  @Test
  fun deleteUserAccount_success() {
    `when`(mockDocumentReference.delete()).thenReturn(Tasks.forResult(null))

    var successCalled = false

    userAccountRepositoryFirestore.deleteUserAccount(
        "1",
        onSuccess = { successCalled = true },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).delete()
    assertTrue("Success callback should be called", successCalled)
  }

  @Test
  fun deleteUserAccount_failure() {
    val exception = RuntimeException("Failed to delete user account")
    `when`(mockDocumentReference.delete()).thenReturn(Tasks.forException(exception))

    var failureCalled = false

    userAccountRepositoryFirestore.deleteUserAccount(
        "1",
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { failureCalled = true })

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).delete()
    assertTrue("Failure callback should be called", failureCalled)
  }
}
