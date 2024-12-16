package com.android.sample.screen.friends

import android.content.Context
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.core.app.ApplicationProvider
import com.android.sample.model.achievements.StatisticsRepositoryFirestore
import com.android.sample.model.achievements.StatisticsViewModel
import com.android.sample.model.achievements.WorkoutStatistics
import com.android.sample.model.userAccount.Gender
import com.android.sample.model.userAccount.HeightUnit
import com.android.sample.model.userAccount.UserAccount
import com.android.sample.model.userAccount.UserAccountLocalCache
import com.android.sample.model.userAccount.UserAccountRepository
import com.android.sample.model.userAccount.UserAccountViewModel
import com.android.sample.model.userAccount.WeightUnit
import com.android.sample.model.workout.WorkoutType
import com.android.sample.ui.authentication.FakeUserAccountRepository
import com.android.sample.ui.friends.FriendStatisticsScreen
import com.android.sample.ui.friends.FriendsScreen
import com.android.sample.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

class FriendsStatsScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Mock navigation actions
    private lateinit var navigationActions: NavigationActions

    @Mock
    private lateinit var userAccountRepository: UserAccountRepository


    private lateinit var statisticsRepository: StatisticsRepositoryFirestore
    private lateinit var statisticsViewModel: StatisticsViewModel

    private lateinit var userAccountViewModel: UserAccountViewModel
    private lateinit var localCache: UserAccountLocalCache
    private val userAccount =
        UserAccount(
            userId = "testUserId",
            firstName = "John",
            lastName = "Doe",
            height = 180f,
            heightUnit = HeightUnit.CM,
            weight = 75f,
            weightUnit = WeightUnit.KG,
            gender = Gender.MALE,
            birthDate = com.google.firebase.Timestamp.now(),
            profileImageUrl = "content://path/to/image")

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Initialize localCache with the context
        localCache = UserAccountLocalCache(context)
        userAccountRepository = FakeUserAccountRepository()
        navigationActions = mock(NavigationActions::class.java)

        statisticsRepository = mock(StatisticsRepositoryFirestore::class.java)

        val fakeStatisticsList = listOf(
            WorkoutStatistics(
                id = "testWorkoutId1",
                date = LocalDateTime.now(),
                duration = 1000,
                caloriesBurnt = 1000,
                type = WorkoutType.RUNNING
            ),
        )
        whenever(statisticsRepository.getFriendStatistics(any(), any(), any())).doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(List<WorkoutStatistics>) -> Unit>(1)
            onSuccess(fakeStatisticsList)
            null
        }

        userAccountViewModel = UserAccountViewModel(userAccountRepository, localCache)
        statisticsViewModel = StatisticsViewModel(statisticsRepository)

        // Initialize the fake repository with a user account for the tests
        (userAccountRepository as FakeUserAccountRepository).setUserAccount(userAccount)

        // Call getUserAccount to initialize the state
        userAccountViewModel.getUserAccount(userAccount.userId)

        // Set the content of the test to the FriendsScreen
        composeTestRule.setContent {
            FriendStatisticsScreen(navigationActions = navigationActions, userAccountViewModel,statisticsViewModel)
        }
    }

    @Test
    fun friendsScreenDisplaysCorrectly() {
        composeTestRule.onNodeWithTag("friendStatisticsScreen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("ScreenTitle").assertIsDisplayed()
        composeTestRule.onNodeWithTag("friendStatisticsList").assertIsDisplayed()
        composeTestRule.onNodeWithTag("workoutCard").assertIsDisplayed()
        composeTestRule.onNodeWithTag("workoutCardContent").assertIsDisplayed()
        composeTestRule.onNodeWithTag("workoutType").assertIsDisplayed()
        composeTestRule.onNodeWithTag("workoutTypeImage").assertIsDisplayed()
        composeTestRule.onNodeWithTag("workoutDate").assertIsDisplayed()
        composeTestRule.onNodeWithTag("workoutCaloriesBurnt").assertIsDisplayed()
        composeTestRule.onNodeWithTag("workoutDuration").assertIsDisplayed()
    }
}