package com.android.sample.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.sample.model.workout.Exercise
import com.android.sample.model.workout.ExerciseDetail
import com.android.sample.R

@Composable
fun ExerciseCard(exercise: Exercise) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = exercise.exType.toString(),
                modifier = Modifier.weight(1f)
            )
            ExerciseDetailCard(exercise.exDetail)
        }
    }
}

@Composable
fun ExerciseDetailCard(detail: ExerciseDetail) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(start = 16.dp)
            .wrapContentSize()
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (detail) {
                is ExerciseDetail.TimeBased -> {
                    Icon(
                        painter = painterResource(id = R.drawable.pace),
                        contentDescription = "Time Based",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "${detail.durationInSeconds} sec")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "X ${detail.sets}")
                }
                is ExerciseDetail.RepetitionBased -> {
                    Icon(
                        painter = painterResource(id = R.drawable.timeline),
                        contentDescription = "Repetition Based",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "X ${detail.repetitions}")
                }
            }
        }
    }
}