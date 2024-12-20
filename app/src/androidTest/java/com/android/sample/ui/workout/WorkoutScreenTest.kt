package com.android.sample.ui.workout

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.test.core.app.ApplicationProvider
import com.android.sample.model.achievements.StatisticsRepositoryFirestore
import com.android.sample.model.achievements.StatisticsViewModel
import com.android.sample.model.userAccount.UserAccount
import com.android.sample.model.userAccount.UserAccountLocalCache
import com.android.sample.model.userAccount.UserAccountRepository
import com.android.sample.model.userAccount.UserAccountViewModel
import com.android.sample.model.userAccount.WeightUnit
import com.android.sample.model.video.Video
import com.android.sample.model.video.VideoRepository
import com.android.sample.model.video.VideoViewModel
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.Exercise
import com.android.sample.model.workout.ExerciseDetail
import com.android.sample.model.workout.ExerciseType
import com.android.sample.model.workout.WarmUp
import com.android.sample.model.workout.WarmUpViewModel
import com.android.sample.model.workout.WorkoutLocalCache
import com.android.sample.model.workout.WorkoutRepository
import com.android.sample.model.workout.WorkoutType
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.times
import org.mockito.kotlin.whenever

class WorkoutScreenTest {
  private lateinit var navigationActions: NavigationActions
  private lateinit var bodyWeightViewModel: WorkoutViewModel<BodyWeightWorkout>
  private lateinit var yogaViewModel: WorkoutViewModel<YogaWorkout>
  private lateinit var localCache: UserAccountLocalCache
  private lateinit var warmUpViewModel: WarmUpViewModel
  private lateinit var yogaRepo: WorkoutRepository<YogaWorkout>
  private lateinit var bodyWeightRepo: WorkoutRepository<BodyWeightWorkout>
  private lateinit var warmUpRepo: WorkoutRepository<WarmUp>
  private lateinit var userAccountViewModel: UserAccountViewModel
  private var userAccountRepository = mock(UserAccountRepository::class.java)
  private val mockVideoRepository = mock(VideoRepository::class.java)
  private val mockVideoRepository2 = mock(VideoRepository::class.java)
  private val mockVideoViewModel = VideoViewModel(mockVideoRepository)
  private val mockVideoViewModel2 = VideoViewModel(mockVideoRepository2)
  private val statisticsRepository = mock(StatisticsRepositoryFirestore::class.java)
  private val mockStatisticsViewModel = StatisticsViewModel(statisticsRepository)
  private lateinit var mockFirebaseAuth: FirebaseAuth
  private lateinit var workoutLocalCache: WorkoutLocalCache

  @get:Rule val composeTestRule = createComposeRule()

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setUp() = runTest {
    Dispatchers.setMain(Dispatchers.Unconfined)

    val context = ApplicationProvider.getApplicationContext<Context>()
    bodyWeightRepo = mock()
    yogaRepo = mock()
    warmUpRepo = mock()
    userAccountRepository = mock(UserAccountRepository::class.java)
    localCache = UserAccountLocalCache(context)
    mockFirebaseAuth = mock(FirebaseAuth::class.java)

    // Use a real WorkoutLocalCache with a real Context
    // This ensures no NullPointerException from null context.
    workoutLocalCache = WorkoutLocalCache(context)

    val exerciseList =
        mutableListOf(
            Exercise(
                id = "1",
                type = ExerciseType.SQUATS,
                ExerciseDetail.RepetitionBased(repetitions = 3)),
            Exercise(
                id = "2",
                type = ExerciseType.PLANK,
                ExerciseDetail.TimeBased(durationInSeconds = 30, sets = 1)),
            Exercise(
                id = "3",
                type = ExerciseType.SQUATS,
                ExerciseDetail.RepetitionBased(repetitions = 3)))

    val bodyWeightWorkouts =
        listOf(
            BodyWeightWorkout(
                "2",
                "MyWorkout",
                "Hold for 60 seconds",
                false,
                exercises = exerciseList,
                date = LocalDateTime.of(2024, 11, 10, 2, 1)))

    val yogaWorkouts =
        listOf(
            YogaWorkout(
                "2",
                "MyWorkout",
                "Hold for 60 seconds",
                false,
                exercises = exerciseList,
                date = LocalDateTime.of(2024, 11, 10, 2, 1)))

    val warmups = listOf(WarmUp("2", "MyWorkout", "Hold for 60 seconds", exercises = exerciseList))

    `when`(bodyWeightRepo.getDocuments(any(), any())).then {
      it.getArgument<(List<BodyWeightWorkout>) -> Unit>(0)(bodyWeightWorkouts)
    }

    `when`(yogaRepo.getDocuments(any(), any())).then {
      it.getArgument<(List<YogaWorkout>) -> Unit>(0)(yogaWorkouts)
    }

    `when`(warmUpRepo.getDocuments(any(), any())).then {
      it.getArgument<(List<WarmUp>) -> Unit>(0)(warmups)
    }

    val userAccount =
        UserAccount(
            userId = "testUserId",
            firstName = "John",
            lastName = "Doe",
            weight = 80f,
            weightUnit = WeightUnit.KG)

    userAccountViewModel = UserAccountViewModel(userAccountRepository, localCache)
    bodyWeightViewModel =
        WorkoutViewModel(bodyWeightRepo, workoutLocalCache, BodyWeightWorkout::class.java)
    yogaViewModel = WorkoutViewModel(yogaRepo, workoutLocalCache, YogaWorkout::class.java)
    warmUpViewModel = WarmUpViewModel(repository = warmUpRepo, workoutLocalCache)

    navigationActions = mock(NavigationActions::class.java)

    yogaViewModel.getWorkouts()
    yogaViewModel.selectWorkout(yogaViewModel.workouts.value[0])

    warmUpViewModel.getWorkouts()
    warmUpViewModel.selectWorkout(warmUpViewModel.workouts.value[0])

    bodyWeightViewModel.getWorkouts()
    bodyWeightViewModel.selectWorkout(bodyWeightViewModel.workouts.value[0])

    val sampleVideos =
        ExerciseType.entries.map { exerciseType ->
          Video(
              title = exerciseType.toString(),
              tag = exerciseType.workoutType.toString(),
              url = "sampleUrl")
        }

    whenever(mockVideoRepository.getVideos(anyOrNull(), anyOrNull())).thenAnswer {
      val onSuccess = it.getArgument<Function1<List<Video>, Unit>>(0)
      onSuccess(sampleVideos)
    }
    whenever(mockVideoRepository2.getVideos(anyOrNull(), anyOrNull())).thenAnswer {
      val onSuccess = it.getArgument<Function1<List<Video>, Unit>>(0)
      onSuccess(listOf())
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @After
  fun tearDown() = runBlocking {
    // Clear all caches to ensure a fresh start for each test
    bodyWeightViewModel.clearCache()
    yogaViewModel.clearCache()
    workoutLocalCache.clearWorkouts()

    // Reset the main dispatcher
    Dispatchers.resetMain()
  }

  @Test
  fun presentationIsDisplayedBodyWeight() {
    composeTestRule.setContent {
      WorkoutScreen(
          navigationActions,
          bodyweightViewModel = bodyWeightViewModel,
          yogaViewModel = yogaViewModel,
          warmUpViewModel = warmUpViewModel,
          workoutType = WorkoutType.BODY_WEIGHT,
          videoViewModel = mockVideoViewModel,
          userAccountViewModel = userAccountViewModel,
          statisticsViewModel = mockStatisticsViewModel)
    }
    // ArrowBack
    composeTestRule.onNodeWithTag("ArrowBackButton").assertIsDisplayed()

    // Test if the presentation screen is well displayed
    // Workout Name
    composeTestRule.onNodeWithTag("WorkoutName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("WorkoutName").assertTextEquals("MyWorkout")
    // Exercise name
    composeTestRule.onNodeWithTag("ExerciseName").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("ExerciseName")
        .assertTextEquals(
            bodyWeightViewModel.selectedWorkout.value?.exercises?.get(0)?.type.toString())
    // Exercise description
    composeTestRule.onNodeWithTag("ExerciseDescription").assertIsDisplayed()
    bodyWeightViewModel.selectedWorkout.value?.exercises?.get(0)?.type?.let {
      composeTestRule.onNodeWithTag("ExerciseDescription").assertTextEquals(it.getInstruction())
    }

    // Goal Icon and value

    composeTestRule.onNodeWithTag("GoalIcon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("GoalValue").assertIsDisplayed()
    composeTestRule.onNodeWithTag("GoalValue").assertTextEquals("3 Rep.")

    // Skip and start button are displayed

    composeTestRule.onNodeWithTag("SkipButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("SkipButton").assertTextEquals("Skip")
    composeTestRule
        .onNodeWithTag("WorkoutScreenBodyColumn")
        .performScrollToNode(hasTestTag("StartButton"))
    composeTestRule.onNodeWithTag("StartButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("StartButton").assertTextEquals("Start")
    composeTestRule.onNodeWithTag("recordSwitch").assertIsDisplayed()

    // VideoPlayer
    composeTestRule.onNodeWithTag("VideoPlayer").assertIsDisplayed()

    // ClickOnStart to start the 1st activity
    composeTestRule.onNodeWithTag("StartButton").performClick()
  }

  @Test
  fun presentationIsDisplayedYoga() {
    composeTestRule.setContent {
      WorkoutScreen(
          navigationActions,
          bodyweightViewModel = bodyWeightViewModel,
          yogaViewModel = yogaViewModel,
          warmUpViewModel = warmUpViewModel,
          workoutType = WorkoutType.YOGA,
          videoViewModel = mockVideoViewModel,
          userAccountViewModel = userAccountViewModel,
          statisticsViewModel = mockStatisticsViewModel)
    }
    // ArrowBack
    composeTestRule.onNodeWithTag("ArrowBackButton").assertIsDisplayed()

    // Test if the presentation screen is well displayed
    // Workout Name
    composeTestRule.onNodeWithTag("WorkoutName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("WorkoutName").assertTextEquals("MyWorkout")
    // Exercise name
    composeTestRule.onNodeWithTag("ExerciseName").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("ExerciseName")
        .assertTextEquals(
            bodyWeightViewModel.selectedWorkout.value?.exercises?.get(0)?.type.toString())
    // Exercise description
    composeTestRule.onNodeWithTag("ExerciseDescription").assertIsDisplayed()
    bodyWeightViewModel.selectedWorkout.value?.exercises?.get(0)?.type?.let {
      composeTestRule.onNodeWithTag("ExerciseDescription").assertTextEquals(it.getInstruction())
    }

    // Goal Icon and value

    composeTestRule.onNodeWithTag("GoalIcon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("GoalValue").assertIsDisplayed()
    composeTestRule.onNodeWithTag("GoalValue").assertTextEquals("3 Rep.")

    // Skip and start button are displayed

    composeTestRule.onNodeWithTag("SkipButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("SkipButton").assertTextEquals("Skip")
    composeTestRule
        .onNodeWithTag("WorkoutScreenBodyColumn")
        .performScrollToNode(hasTestTag("StartButton"))
    composeTestRule.onNodeWithTag("StartButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("StartButton").assertTextEquals("Start")
    composeTestRule.onNodeWithTag("recordSwitch").assertIsDisplayed()

    // VideoPlayer
    composeTestRule.onNodeWithTag("VideoPlayer").assertIsDisplayed()
  }

  @Test
  fun presentationIsDisplayedWarmup() {
    composeTestRule.setContent {
      WorkoutScreen(
          navigationActions,
          bodyweightViewModel = bodyWeightViewModel,
          yogaViewModel = yogaViewModel,
          warmUpViewModel = warmUpViewModel,
          workoutType = WorkoutType.YOGA,
          videoViewModel = mockVideoViewModel,
          userAccountViewModel = userAccountViewModel,
          statisticsViewModel = mockStatisticsViewModel)
    }
    // ArrowBack
    composeTestRule.onNodeWithTag("ArrowBackButton").assertIsDisplayed()

    // Test if the presentation screen is well displayed
    // Workout Name
    composeTestRule.onNodeWithTag("WorkoutName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("WorkoutName").assertTextEquals("MyWorkout")
    // Exercise name
    composeTestRule.onNodeWithTag("ExerciseName").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("ExerciseName")
        .assertTextEquals(
            bodyWeightViewModel.selectedWorkout.value?.exercises?.get(0)?.type.toString())
    // Exercise description
    composeTestRule.onNodeWithTag("ExerciseDescription").assertIsDisplayed()
    bodyWeightViewModel.selectedWorkout.value?.exercises?.get(0)?.type?.let {
      composeTestRule.onNodeWithTag("ExerciseDescription").assertTextEquals(it.getInstruction())
    }

    // Goal Icon and value

    composeTestRule.onNodeWithTag("GoalIcon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("GoalValue").assertIsDisplayed()
    composeTestRule.onNodeWithTag("GoalValue").assertTextEquals("3 Rep.")

    // Skip and start button are displayed

    composeTestRule.onNodeWithTag("SkipButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("SkipButton").assertTextEquals("Skip")
    composeTestRule
        .onNodeWithTag("WorkoutScreenBodyColumn")
        .performScrollToNode(hasTestTag("StartButton"))
    composeTestRule.onNodeWithTag("StartButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("StartButton").assertTextEquals("Start")
    composeTestRule.onNodeWithTag("recordSwitch").assertIsDisplayed()

    // VideoPlayer
    composeTestRule.onNodeWithTag("VideoPlayer").assertIsDisplayed()
  }

  @Test
  fun startingExerciseIsDisplayed() {
    composeTestRule.setContent {
      WorkoutScreen(
          navigationActions,
          bodyweightViewModel = bodyWeightViewModel,
          yogaViewModel = yogaViewModel,
          warmUpViewModel = warmUpViewModel,
          workoutType = WorkoutType.BODY_WEIGHT,
          videoViewModel = mockVideoViewModel,
          userAccountViewModel = userAccountViewModel,
          statisticsViewModel = mockStatisticsViewModel)
    }
    // check that permanent composable are still there
    // ArrowBack
    composeTestRule.onNodeWithTag("ArrowBackButton").assertIsDisplayed()
    // Workout Name
    composeTestRule.onNodeWithTag("WorkoutName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("WorkoutName").assertTextEquals("MyWorkout")
    // Exercise name
    composeTestRule.onNodeWithTag("ExerciseName").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("ExerciseName")
        .assertTextEquals(
            bodyWeightViewModel.selectedWorkout.value?.exercises?.get(0)?.type.toString())
    // Exercise description
    composeTestRule.onNodeWithTag("ExerciseDescription").assertIsDisplayed()
    bodyWeightViewModel.selectedWorkout.value?.exercises?.get(0)?.type?.let {
      composeTestRule.onNodeWithTag("ExerciseDescription").assertTextEquals(it.getInstruction())
    }
    composeTestRule
        .onNodeWithTag("WorkoutScreenBodyColumn")
        .performScrollToNode(hasTestTag("StartButton"))
    composeTestRule.onNodeWithTag("StartButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("StartButton").performClick()
    // Check that the presentation specific components are hidden
    // startButton is not displayed
    composeTestRule.onNodeWithTag("StartButton").assertIsNotDisplayed()
    // VideoPlayer is not displayed
    composeTestRule.onNodeWithTag("VideoPlayer").assertIsNotDisplayed()

    // As it rep based, show the ExercisetypeIcon and not the timer
    composeTestRule.onNodeWithTag("ExerciseTypeIcon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("CountDownTimer").assertIsNotDisplayed()
  }

  @Test
  fun startingTimeBasedExerciseIsDisplayed() {
    composeTestRule.setContent {
      WorkoutScreen(
          navigationActions,
          bodyweightViewModel = bodyWeightViewModel,
          yogaViewModel = yogaViewModel,
          warmUpViewModel = warmUpViewModel,
          workoutType = WorkoutType.BODY_WEIGHT,
          videoViewModel = mockVideoViewModel,
          userAccountViewModel = userAccountViewModel,
          statisticsViewModel = mockStatisticsViewModel)
    }
    // check that permanent composable are still there
    // ArrowBack
    composeTestRule.onNodeWithTag("ArrowBackButton").assertIsDisplayed()
    // Workout Name
    composeTestRule.onNodeWithTag("WorkoutName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("WorkoutName").assertTextEquals("MyWorkout")
    // Exercise name
    composeTestRule.onNodeWithTag("ExerciseName").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("ExerciseName")
        .assertTextEquals(
            bodyWeightViewModel.selectedWorkout.value?.exercises?.get(0)?.type.toString())
    // Exercise description
    composeTestRule.onNodeWithTag("ExerciseDescription").assertIsDisplayed()
    bodyWeightViewModel.selectedWorkout.value?.exercises?.get(0)?.type?.let {
      composeTestRule.onNodeWithTag("ExerciseDescription").assertTextEquals(it.getInstruction())
    }
    composeTestRule
        .onNodeWithTag("WorkoutScreenBodyColumn")
        .performScrollToNode(hasTestTag("StartButton"))
    // skip the 1st exercise wich is rep. based
    composeTestRule.onNodeWithTag("SkipButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("SkipButton").performClick()
    // start the 2nd exercise wich is time based
    composeTestRule.onNodeWithTag("StartButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("StartButton").performClick()
    // Check that the presentation specific components are hidden
    // startButton is not displayed
    composeTestRule.onNodeWithTag("StartButton").assertIsNotDisplayed()
    // VideoPlayer is not displayed
    composeTestRule.onNodeWithTag("VideoPlayer").assertIsNotDisplayed()

    // check that the timer is displayed
    composeTestRule.onNodeWithTag("CountDownTimer").assertIsDisplayed()
    // check that the play resume button is displayed
    composeTestRule.onNodeWithTag("CounterPauseResumeButton").assertIsDisplayed()
  }

  @Test
  fun skipButtonGoesToNextExercise() {
    composeTestRule.setContent {
      WorkoutScreen(
          navigationActions,
          bodyweightViewModel = bodyWeightViewModel,
          yogaViewModel = yogaViewModel,
          warmUpViewModel = warmUpViewModel,
          workoutType = WorkoutType.BODY_WEIGHT,
          videoViewModel = mockVideoViewModel,
          userAccountViewModel = userAccountViewModel,
          statisticsViewModel = mockStatisticsViewModel)
    }

    // Exercise name
    composeTestRule.onNodeWithTag("ExerciseName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ExerciseName").assertTextEquals(ExerciseType.SQUATS.toString())

    // Skip the 1st activity
    composeTestRule.onNodeWithTag("SkipButton").performClick()

    // Exercise name is correctly updated
    composeTestRule.onNodeWithTag("ExerciseName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ExerciseName").assertTextEquals(ExerciseType.PLANK.toString())

    // the video box is there again
    composeTestRule.onNodeWithTag("VideoPlayer").assertIsDisplayed()

    // Check that the goal is well updated

    composeTestRule.onNodeWithTag("GoalValue").assertIsDisplayed()
    composeTestRule.onNodeWithTag("GoalValue").assertTextEquals("00:30")
  }

  @Test
  fun skipButtonWorkDuringExercise() {
    composeTestRule.setContent {
      WorkoutScreen(
          navigationActions,
          bodyweightViewModel = bodyWeightViewModel,
          yogaViewModel = yogaViewModel,
          warmUpViewModel = warmUpViewModel,
          workoutType = WorkoutType.BODY_WEIGHT,
          videoViewModel = mockVideoViewModel,
          userAccountViewModel = userAccountViewModel,
          statisticsViewModel = mockStatisticsViewModel)
    }

    // Exercise name
    composeTestRule.onNodeWithTag("ExerciseName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ExerciseName").assertTextEquals(ExerciseType.SQUATS.toString())

    // Start 1st exercise
    composeTestRule.onNodeWithTag("StartButton").performClick()
    // Then skip 1st exercise
    composeTestRule.onNodeWithTag("SkipButton").performClick()
    // Exercise name
    composeTestRule.onNodeWithTag("ExerciseName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ExerciseName").assertTextEquals(ExerciseType.PLANK.toString())

    // the video box is there again
    composeTestRule.onNodeWithTag("VideoPlayer").assertIsDisplayed()

    // Check that the goal is well updated

    composeTestRule.onNodeWithTag("GoalValue").assertIsDisplayed()
    composeTestRule.onNodeWithTag("GoalValue").assertTextEquals("00:30")
  }

  @Test
  fun finishWorkoutCallsNavigation() {
    composeTestRule.setContent {
      WorkoutScreen(
          navigationActions,
          bodyweightViewModel = bodyWeightViewModel,
          yogaViewModel = yogaViewModel,
          warmUpViewModel = warmUpViewModel,
          workoutType = WorkoutType.BODY_WEIGHT,
          videoViewModel = mockVideoViewModel,
          userAccountViewModel = userAccountViewModel,
          statisticsViewModel = mockStatisticsViewModel)
    }
    // ex1
    composeTestRule
        .onNodeWithTag("WorkoutScreenBodyColumn")
        .performScrollToNode(hasTestTag("StartButton"))
    composeTestRule.onNodeWithTag("StartButton").performClick()
    composeTestRule.onNodeWithTag("FinishButton").performClick()
    // ex2
    composeTestRule
        .onNodeWithTag("WorkoutScreenBodyColumn")
        .performScrollToNode(hasTestTag("StartButton"))
    composeTestRule.onNodeWithTag("StartButton").performClick()
    composeTestRule.onNodeWithTag("FinishButton").performClick()
    // ex3 (last one)
    composeTestRule
        .onNodeWithTag("WorkoutScreenBodyColumn")
        .performScrollToNode(hasTestTag("StartButton"))
    composeTestRule.onNodeWithTag("StartButton").performClick()
    composeTestRule.onNodeWithTag("FinishButton").performClick()
    // Skip the summary
    composeTestRule.onNodeWithTag("FinishButton").performScrollTo().performClick()

    verify(navigationActions).navigateTo(Screen.MAIN)
  }

  @Test
  fun loadingCircleIsDisplayedDuringFetching() {
    composeTestRule.setContent {
      WorkoutScreen(
          navigationActions,
          bodyweightViewModel = bodyWeightViewModel,
          yogaViewModel = yogaViewModel,
          warmUpViewModel = warmUpViewModel,
          workoutType = WorkoutType.BODY_WEIGHT,
          videoViewModel = mockVideoViewModel2,
          userAccountViewModel = userAccountViewModel,
          statisticsViewModel = mockStatisticsViewModel)
    }

    composeTestRule.onNodeWithTag("LoadingIndicator").assertIsDisplayed()
    composeTestRule.onNodeWithTag("videoPlayer").assertIsNotDisplayed()
  }

  @Test
  fun startingWorkoutCallsLoadVideos() {
    composeTestRule.setContent {
      WorkoutScreen(
          navigationActions,
          bodyweightViewModel = bodyWeightViewModel,
          yogaViewModel = yogaViewModel,
          warmUpViewModel = warmUpViewModel,
          workoutType = WorkoutType.BODY_WEIGHT,
          videoViewModel = mockVideoViewModel,
          userAccountViewModel = userAccountViewModel,
          statisticsViewModel = mockStatisticsViewModel)
    }
    // Check that on launching the composable, the videoViewModel tries to fetch the videos on
    // firestore
    verify(mockVideoRepository, atLeastOnce()).getVideos(any(), any())
  }

  @Test
  fun summaryScreenIsWellDisplayed() {
    composeTestRule.setContent {
      WorkoutScreen(
          navigationActions,
          bodyweightViewModel = bodyWeightViewModel,
          yogaViewModel = yogaViewModel,
          warmUpViewModel = warmUpViewModel,
          workoutType = WorkoutType.BODY_WEIGHT,
          videoViewModel = mockVideoViewModel,
          userAccountViewModel = userAccountViewModel,
          statisticsViewModel = mockStatisticsViewModel)
    }
    // ex1
    composeTestRule
        .onNodeWithTag("WorkoutScreenBodyColumn")
        .performScrollToNode(hasTestTag("StartButton"))
    composeTestRule.onNodeWithTag("StartButton").performClick()
    composeTestRule.onNodeWithTag("FinishButton").performClick()
    // ex2
    composeTestRule
        .onNodeWithTag("WorkoutScreenBodyColumn")
        .performScrollToNode(hasTestTag("StartButton"))
    composeTestRule.onNodeWithTag("StartButton").performClick()
    composeTestRule.onNodeWithTag("FinishButton").performClick()
    // ex3 (last one is skipped)

    composeTestRule.onNodeWithTag("SkipButton").performClick()

    // Check that the summaryscreen is well displayed
    composeTestRule.onNodeWithTag("WorkoutSummaryScreen").assertIsDisplayed()

    // exercises should appear
    // ex1
    composeTestRule.onNodeWithTag("ExerciseCardID1").assertIsDisplayed()
    // ex2
    composeTestRule.onNodeWithTag("ExerciseCardID2").assertIsDisplayed()
    // ex3
    composeTestRule.onNodeWithTag("ExerciseCardID3").assertIsDisplayed()

    // Check their text in the information box
    // ex1
    composeTestRule
        .onNodeWithTag("InnerTextExerciseCardID1", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextEquals("X 3")
    // ex2
    composeTestRule
        .onNodeWithTag("InnerTextExerciseCardID2", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextEquals("30s X 1")
    // ex3
    composeTestRule
        .onNodeWithTag("InnerTextExerciseCardID3", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextEquals("Skipped")

    // Click on a skipped exercise show the details

    composeTestRule.onNodeWithTag("InnerTextExerciseCardID3", useUnmergedTree = true).performClick()
    composeTestRule
        .onNodeWithTag("InnerTextExerciseCardID3", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextEquals("X 3")

    composeTestRule.onNodeWithTag("Calories").assertIsDisplayed()
  }
}
