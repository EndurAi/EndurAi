package com.android.sample.mlUtils

import MathsPoseDetection
import com.android.sample.model.camera.CameraViewModel
import com.android.sample.model.workout.ExerciseType

class MlCoach(val cameraViewModel: CameraViewModel, private val exerciseType: ExerciseType) {

    fun getFeedback(): String {
        // get the criterion and the preamble

        val excerciseCriterions = ExerciseFeedBack.getCriterions(exerciseType = exerciseType )
        val preambleCriterions = ExerciseFeedBack.preambleCriterion(exerciseCriterion = excerciseCriterions, onSuccess =  {}, onFailure = {})
        // get the stacked value from the camera view model
        val rawData = cameraViewModel._poseLandMarks.value
        // mean the data
        val windowSize = 3
        val windowStep = 1
        // 1st dim : Sample number, 2nd dim : a joint fo the sample, the 3rd dim : the three coordinates of the joint
        val data : List<List<Triple<Float,Float,Float>>> = rawData
            .windowed(windowSize,windowStep, partialWindows = false)
            .map { window ->
                MathsPoseDetection.window_mean(window)
            }

        val data_preambleActived = data.filter { sample -> ExerciseFeedBack.assessLandMarks(sample, preambleCriterions).first }

        //compute the distance from the target to the reference for each angle criterion
        //get the list of comments

        val assessedExercise = data_preambleActived.map { sample ->
            ExerciseFeedBack.assessLandMarks(sample, exerciseCriterion = excerciseCriterions)
        }

        val freqThreshold = 0.15F //If a mistake is make with a higher frequency, give advice to the user

        val nbComments = assessedExercise.fold(0,{acc,(boolean,list)-> acc+list.size })
        val allComments = assessedExercise
            .flatMap { (sampleIsSucceed, commentList) -> commentList }
            .groupingBy { it }
            .eachCount()
            .toList()
            .map { (comment, count) -> Pair(comment,count/nbComments.toFloat()) }
            .filter { (comment,freq) -> freq >= freqThreshold }
            .sortedBy { (_, freq) -> freq }

        val adviceBuilder :StringBuilder = StringBuilder()
        val listOfAdvice = allComments.forEach{ (comment, freq) ->
            adviceBuilder.append(comment.description).append(" rate: ${freq*100}%").append("\n")
        }
        val advice = adviceBuilder.toString()

        return advice



    }
}