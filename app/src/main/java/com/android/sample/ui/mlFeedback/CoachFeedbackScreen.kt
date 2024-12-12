package com.android.sample.ui.mlFeedback

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.mlUtils.CoachFeedback
import com.android.sample.mlUtils.FeedbackRank
import com.android.sample.mlUtils.exercisesCriterions.JumpingJacksOpenCriterions
import com.android.sample.mlUtils.exercisesCriterions.PushUpsUpCrierions
import com.android.sample.mlUtils.rateToRank
import com.android.sample.model.camera.CameraViewModel
import com.android.sample.ui.composables.SaveButton
import com.android.sample.ui.composables.TalkingCoach
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.Black
import com.android.sample.ui.theme.BlueWorkoutCard
import com.android.sample.ui.theme.FontSizes.BigTitleFontSize
import com.android.sample.ui.theme.Green
import com.android.sample.ui.theme.MediumGrey
import com.android.sample.ui.theme.OpenSans
import com.android.sample.ui.theme.Red
import com.android.sample.ui.theme.RunningTag
import com.android.sample.ui.theme.Yellow
import com.android.sample.ui.theme.YogaTag
import com.android.sample.ui.workout.LeafShape
import kotlinx.coroutines.launch

@Composable
fun CoachFeedbackScreen(navigationActions: NavigationActions, cameraViewModel: CameraViewModel) {
    Scaffold(
        topBar = {
            TopBar(
                title = R.string.coach_feedback_title,
                navigationActions = navigationActions
            )
        },
        content = {
            pd ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(pd),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val rawFeedback = cameraViewModel.feedback
                    val rank = getNote(rawFeedback!!)
                    val exerciseString = exerciseName(rawFeedback)
                    val durationOrRepetitionString = durationString(rawFeedback)
                    val startingFeedback = genericFeedbackFromRank(rank)

                    // Card with exercise name and duration/repetition
                    Card(
                        colors = CardDefaults.cardColors(containerColor =  BlueWorkoutCard),
                        shape = LeafShape,
                        modifier = Modifier
                            .padding(18.dp)
                            .shadow(8.dp, shape = LeafShape)
                            .testTag("exerciseCard")
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = exerciseString,
                                fontFamily = OpenSans,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.testTag("exerciseName")
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = durationOrRepetitionString,
                                fontFamily = OpenSans,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.testTag("exerciseDuration")
                            )
                        }
                    }
                    // Animated feedback rank circle
                    RankCircle(rank)

                    TalkingCoach(
                        text = startingFeedback + "\n" + rawFeedback.joinToString("\n") { it.toString() },
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    //Done button
                    SaveButton(
                        onSaveClick = {
                            navigationActions.navigateTo(Screen.MAIN)
                        },
                        testTag = "doneButton",
                        text = "Done"
                    )
                }


        }
    )
}

private fun getNote(feedbacks: List<CoachFeedback>): FeedbackRank {
    val averageRate = feedbacks.map { it.successRate }.average()
    val rank = rateToRank(averageRate.toFloat())
    return rank
}
private fun genericFeedbackFromRank(rank: FeedbackRank): String {
    return when (rank) {
        FeedbackRank.S -> "Amazing! Keep it up! Can't say anything wrong about your performance!"
        FeedbackRank.A -> "Great job! Here are some tips to improve even more :"
        FeedbackRank.B -> "Good job! But you can surely do better! Here are some tips to improve :"
        FeedbackRank.C -> "Ok, there is room for improvement! Here are some tips to improve :"
        FeedbackRank.D -> "You need to improve in order to do the exercise correctly! Here are some tips to improve :"
        FeedbackRank.X -> "I couldn't see you. Make sure your whole body is in the camera frame."
    }
}
private fun durationString(feedbacks: List<CoachFeedback>): String {
    val feedback = feedbacks.first()
   return "${feedback.feedbackUnit.valuePrefix}: ${feedback.feedbackValue} ${feedback.feedbackUnit.stringRepresentation}"
}
private fun exerciseName(feedbacks :List<CoachFeedback>): String {
    return when (feedbacks.first().exerciseCriterion) {
        PushUpsUpCrierions -> "Push Ups"
        JumpingJacksOpenCriterions -> "Jumping Jacks"
        else -> feedbacks.first().exerciseCriterion.name
    }
}
@Composable
fun RankCircle(rank: FeedbackRank) {
    // Couleur principale en fonction du rang
    val rankColor = getColorForRank(rank)

    // Animation infinie pour l'effet de respiration
    val infiniteTransition = rememberInfiniteTransition()
    val animatedRadius = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse // Effet de contraction après expansion
        )
    )

    // Animation pour les grandes vagues initiales
    val initialWaveCount = 5 // Nombre de vagues initiales
    val initialWaveRadius = remember { mutableStateListOf<Float>() }
    val initialWaveAlpha = remember { mutableStateListOf<Float>() }

    // Initialiser les états des vagues
    if (initialWaveRadius.isEmpty()) {
        repeat(initialWaveCount) { index ->
            initialWaveRadius.add(0f)
            initialWaveAlpha.add(1f)
        }
    }

    // Lancer les animations initiales
    LaunchedEffect(Unit) {
        initialWaveRadius.forEachIndexed { index, _ ->
            launch {
                animate(
                    initialValue = 0f,
                    targetValue = 3.5f, // Les vagues dépassent l'écran
                    animationSpec = tween(durationMillis = 1500 + index * 300, easing = LinearOutSlowInEasing)
                ) { value, _ ->
                    initialWaveRadius[index] = value
                }
            }
            launch {
                animate(
                    initialValue = 1f,
                    targetValue = 0f, // Les vagues disparaissent progressivement
                    animationSpec = tween(durationMillis = 1500 + index * 300, easing = LinearOutSlowInEasing)
                ) { value, _ ->
                    initialWaveAlpha[index] = value
                }
            }
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(180.dp) // Taille globale
            .testTag("rankCircle")
    ) {
        // Grandes vagues initiales et effet respirant (en arrière-plan)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasRadius = size.minDimension / 2

            // Grandes vagues initiales
            initialWaveRadius.forEachIndexed { index, waveRadius ->
                if (initialWaveAlpha[index] > 0f) { // Afficher uniquement si visible
                    drawCircle(
                        color = rankColor.copy(alpha = 0.3f * initialWaveAlpha[index]),
                        radius = canvasRadius * waveRadius,
                        style = Stroke(width = 6.dp.toPx()) // Grandes vagues fines
                    )
                }
            }

            // Effet respirant
            drawCircle(
                color = rankColor.copy(alpha = 0.3f), // Couleur semi-transparente
                radius = canvasRadius * animatedRadius.value, // Rayon animé
                style = Stroke(width = 8.dp.toPx()) // Contour seulement
            )
        }

        // Cercle principal (au-dessus des animations)
        Box(
            modifier = Modifier
                .size(140.dp) // Taille réduite par rapport aux animations
                .shadow(
                    elevation = 12.dp, // Ombre douce
                    shape = CircleShape,
                    clip = true
                )
                .background(MediumGrey, CircleShape) // Fond totalement opaque
                .border(
                    width = 4.dp, // Contour du cercle principal
                    color = rankColor,
                    shape = CircleShape
                )
        ) {
            // Texte du rang au centre
            Text(
                text = rank.name,
                color = rankColor, // Couleur du texte selon le rang
                fontFamily = OpenSans,
                fontSize = BigTitleFontSize,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

private fun getColorForRank(rank: FeedbackRank): Color {
    return when (rank) {
        FeedbackRank.S -> YogaTag
        FeedbackRank.A -> Green
        FeedbackRank.B -> RunningTag
        FeedbackRank.C -> Yellow
        FeedbackRank.D -> Red
        FeedbackRank.X -> Black
    }
}

