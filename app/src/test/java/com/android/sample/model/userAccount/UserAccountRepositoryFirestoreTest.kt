package com.android.sample.model.userAccount

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.invocation.InvocationOnMock
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class UserAccountRepositoryFirestoreTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot
  @Mock private lateinit var localCache: UserAccountLocalCache

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

    localCache = mock(UserAccountLocalCache::class.java)

    // Return a valid Flow for the local cache
    `when`(localCache.getUserAccount()).thenReturn(flowOf(null))

    userAccountRepositoryFirestore = UserAccountRepositoryFirestore(mockFirestore, localCache)

    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.document()).thenReturn(mockDocumentReference)
  }

  @Test
  fun getUserAccount_success() {
    runTest {
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
  }

  @Test
  fun getUserAccount_failure() {
    runTest {
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
  }

  @Test
  fun `getUserAccount fetches from cache first`() = runTest {
    `when`(localCache.getUserAccount()).thenReturn(flowOf(userAccount))

    userAccountRepositoryFirestore.getUserAccount(
        "1",
        onSuccess = { account -> assertThat(account, `is`(userAccount)) },
        onFailure = { fail("Failure callback should not be called") })

    verify(localCache).getUserAccount()
    verify(mockDocumentReference, never()).get()
  }

  @Test
  fun `getUserAccount falls back to Firebase if cache is empty`() = runTest {
    `when`(localCache.getUserAccount()).thenReturn(flowOf(null))
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.toObject(UserAccount::class.java)).thenReturn(userAccount)

    userAccountRepositoryFirestore.getUserAccount(
        "1",
        onSuccess = { account -> assertThat(account, `is`(userAccount)) },
        onFailure = { fail("Failure callback should not be called") })

    verify(localCache).getUserAccount()
    verify(mockDocumentReference).get()
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
  fun `createUserAccount saves to cache`() = runTest {
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null))

    userAccountRepositoryFirestore.createUserAccount(
        userAccount, onSuccess = {}, onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle() // Ensure all tasks are executed

    verify(mockDocumentReference).set(eq(userAccount))
    verify(localCache).saveUserAccount(eq(userAccount)) // Verify cache save
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
  fun `updateUserAccount saves to cache`() = runTest {
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null))

    userAccountRepositoryFirestore.updateUserAccount(
        userAccount, onSuccess = {}, onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle() // Ensure all tasks are executed

    verify(mockDocumentReference).set(eq(userAccount))
    verify(localCache).saveUserAccount(eq(userAccount)) // Verify cache save
  }

  @Test
  fun acceptFriendRequest_success() {
    val friendId = "2"
    val userRef = mock<DocumentReference>()
    val friendRef = mock<DocumentReference>()

    `when`(mockFirestore.collection(any()).document(userAccount.userId)).thenReturn(userRef)
    `when`(mockFirestore.collection(any()).document(friendId)).thenReturn(friendRef)

    `when`(mockFirestore.runTransaction(any<Transaction.Function<Void>>())).thenAnswer {
        invocation: InvocationOnMock ->
      val transactionFunction = invocation.arguments[0] as Transaction.Function<Void>
      transactionFunction.apply(mockTransaction)
      Tasks.forResult<Void>(null)
    }

    `when`(mockTransaction.get(userRef)).thenReturn(mockDocumentSnapshot)
    `when`(mockTransaction.get(friendRef)).thenReturn(mockDocumentSnapshot)
    `when`(mockDocumentSnapshot.toObject(UserAccount::class.java)).thenReturn(userAccount)

    var successCalled = false

    userAccountRepositoryFirestore.acceptFriendRequest(
        userAccount,
        friendId,
        onSuccess = { successCalled = true },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockFirestore).runTransaction(any<Transaction.Function<Void>>())
    verify(mockTransaction).update(userRef, "friends", listOf(friendId))
    verify(mockTransaction).update(userRef, "receivedRequests", emptyList<String>())
    verify(mockTransaction).update(friendRef, "friends", listOf(userAccount.userId))
    verify(mockTransaction).update(friendRef, "sentRequests", emptyList<String>())
    assertTrue("Success callback should be called", successCalled)
  }

  @Test
  fun rejectFriendRequest_success() {
    val friendId = "2"
    val userRef = mock<DocumentReference>()
    val friendRef = mock<DocumentReference>()

    `when`(mockFirestore.collection(any()).document(userAccount.userId)).thenReturn(userRef)
    `when`(mockFirestore.collection(any()).document(friendId)).thenReturn(friendRef)

    `when`(mockFirestore.runTransaction(any<Transaction.Function<Void>>())).thenAnswer {
        invocation: InvocationOnMock ->
      val transactionFunction = invocation.arguments[0] as Transaction.Function<Void>
      transactionFunction.apply(mockTransaction)
      Tasks.forResult<Void>(null)
    }

    `when`(mockTransaction.get(userRef)).thenReturn(mockDocumentSnapshot)
    `when`(mockTransaction.get(friendRef)).thenReturn(mockDocumentSnapshot)
    `when`(mockDocumentSnapshot.toObject(UserAccount::class.java)).thenReturn(userAccount)

    var successCalled = false

    userAccountRepositoryFirestore.rejectFriendRequest(
        userAccount,
        friendId,
        onSuccess = { successCalled = true },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockFirestore).runTransaction(any<Transaction.Function<Void>>())
    verify(mockTransaction).update(userRef, "receivedRequests", emptyList<String>())
    verify(mockTransaction).update(friendRef, "sentRequests", emptyList<String>())
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
  fun removeFriend_success() {
    val friendId = "2"
    val userRef = mock<DocumentReference>()
    val friendRef = mock<DocumentReference>()

    `when`(mockFirestore.collection(any()).document(userAccount.userId)).thenReturn(userRef)
    `when`(mockFirestore.collection(any()).document(friendId)).thenReturn(friendRef)

    `when`(mockFirestore.runTransaction(any<Transaction.Function<Void>>())).thenAnswer {
      val transactionFunction = it.arguments[0] as Transaction.Function<Void>
      transactionFunction.apply(mockTransaction)
      Tasks.forResult<Void>(null)
    }

    `when`(mockTransaction.get(userRef)).thenReturn(mockDocumentSnapshot)
    `when`(mockTransaction.get(friendRef)).thenReturn(mockDocumentSnapshot)
    `when`(mockDocumentSnapshot.toObject(UserAccount::class.java)).thenReturn(userAccount)

    var successCalled = false

    userAccountRepositoryFirestore.removeFriend(
        userAccount,
        friendId,
        onSuccess = { successCalled = true },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockFirestore).runTransaction(any<Transaction.Function<Void>>())
    verify(mockTransaction).update(userRef, "friends", emptyList<String>())
    verify(mockTransaction).update(friendRef, "friends", emptyList<String>())
    assertTrue("Success callback should be called", successCalled)
  }

  @Test
  fun sendFriendRequest_success() {
    val toUserId = "2"
    val fromUserRef = mock<DocumentReference>()
    val toUserRef = mock<DocumentReference>()

    `when`(mockFirestore.collection(any()).document(userAccount.userId)).thenReturn(fromUserRef)
    `when`(mockFirestore.collection(any()).document(toUserId)).thenReturn(toUserRef)

    `when`(mockFirestore.runTransaction(any<Transaction.Function<Void>>())).thenAnswer {
      val transactionFunction = it.arguments[0] as Transaction.Function<Void>
      transactionFunction.apply(mockTransaction)
      Tasks.forResult<Void>(null)
    }

    `when`(mockTransaction.get(fromUserRef)).thenReturn(mockDocumentSnapshot)
    `when`(mockTransaction.get(toUserRef)).thenReturn(mockDocumentSnapshot)
    `when`(mockDocumentSnapshot.toObject(UserAccount::class.java)).thenReturn(userAccount)

    var successCalled = false

    userAccountRepositoryFirestore.sendFriendRequest(
        userAccount,
        toUserId,
        onSuccess = { successCalled = true },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockFirestore).runTransaction(any<Transaction.Function<Void>>())
    verify(mockTransaction).update(fromUserRef, "sentRequests", listOf(toUserId))
    verify(mockTransaction).update(toUserRef, "receivedRequests", listOf(userAccount.userId))
    assertTrue("Success callback should be called", successCalled)
  }

  @Test
  fun getFriendsFromFirestore_success() {
    val friendId = "2"
    val friendUser = userAccount.copy(userId = friendId)

    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.toObject(UserAccount::class.java))
        .thenReturn(userAccount.copy(friends = listOf(friendId)))

    val friendDocumentReference = mock<DocumentReference>()
    val friendDocumentSnapshot = mock<DocumentSnapshot>()
    `when`(mockCollectionReference.document(friendId)).thenReturn(friendDocumentReference)
    `when`(friendDocumentReference.get()).thenReturn(Tasks.forResult(friendDocumentSnapshot))
    `when`(friendDocumentSnapshot.toObject(UserAccount::class.java)).thenReturn(friendUser)

    var successCalled = false

    userAccountRepositoryFirestore.getFriendsFromFirestore(
        userAccount.userId,
        onSuccess = { friends ->
          successCalled = true
          // Assert that the returned friends list contains the correct friend
          assertThat(friends, `is`(listOf(friendUser)))
        },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).get()
    verify(friendDocumentReference).get()
    assertTrue("Success callback should be called", successCalled)
  }

  @Test
  fun getSentRequestsFromFirestore_success() {
    val sentRequestUserId = "2"
    val sentRequestUser = userAccount.copy(userId = sentRequestUserId)

    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.toObject(UserAccount::class.java))
        .thenReturn(userAccount.copy(sentRequests = listOf(sentRequestUserId)))

    val sentRequestDocumentReference = mock<DocumentReference>()
    val sentRequestDocumentSnapshot = mock<DocumentSnapshot>()
    `when`(mockCollectionReference.document(sentRequestUserId))
        .thenReturn(sentRequestDocumentReference)
    `when`(sentRequestDocumentReference.get())
        .thenReturn(Tasks.forResult(sentRequestDocumentSnapshot))
    `when`(sentRequestDocumentSnapshot.toObject(UserAccount::class.java))
        .thenReturn(sentRequestUser)

    var successCalled = false

    userAccountRepositoryFirestore.getSentRequestsFromFirestore(
        userAccount.userId,
        onSuccess = { sentRequests ->
          successCalled = true
          assertThat(sentRequests, `is`(listOf(sentRequestUser)))
        },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).get()
    verify(sentRequestDocumentReference).get()
    assertTrue("Success callback should be called", successCalled)
  }

  @Test
  fun getReceivedRequestsFromFirestore_success() {
    val receivedRequestUserId = "2"
    val receivedRequestUser = userAccount.copy(userId = receivedRequestUserId)

    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.toObject(UserAccount::class.java))
        .thenReturn(userAccount.copy(receivedRequests = listOf(receivedRequestUserId)))

    val receivedRequestDocumentReference = mock<DocumentReference>()
    val receivedRequestDocumentSnapshot = mock<DocumentSnapshot>()
    `when`(mockCollectionReference.document(receivedRequestUserId))
        .thenReturn(receivedRequestDocumentReference)
    `when`(receivedRequestDocumentReference.get())
        .thenReturn(Tasks.forResult(receivedRequestDocumentSnapshot))
    `when`(receivedRequestDocumentSnapshot.toObject(UserAccount::class.java))
        .thenReturn(receivedRequestUser)

    var successCalled = false

    userAccountRepositoryFirestore.getReceivedRequestsFromFirestore(
        userAccount.userId,
        onSuccess = { receivedRequests ->
          successCalled = true
          assertThat(receivedRequests, `is`(listOf(receivedRequestUser)))
        },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).get()
    verify(receivedRequestDocumentReference).get()
    assertTrue("Success callback should be called", successCalled)
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
