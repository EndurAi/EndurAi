package com.android.sample.mlUtils

import MathsPoseDetection
import androidx.work.workDataOf
import com.android.sample.model.camera.CameraViewModel
import com.android.sample.model.workout.ExerciseType
import com.android.sample.mlUtils.ExerciseFeedBack.Companion.ExerciseCriterion
import com.android.sample.mlUtils.ExerciseFeedBack.Companion.assessLandMarks
import com.android.sample.model.workout.ExerciseDetail
import com.android.sample.model.workout.WorkoutType
import com.android.sample.ui.mlFeedback.PoseDetectionAnalyser
import kotlin.time.Duration

class MlCoach(val cameraViewModel: CameraViewModel, private val exerciseType: ExerciseType) {

    fun getFeedback(): String {
        // get the criterion and the preamble

        val excerciseCriterionsList = ExerciseFeedBack.getCriterions(exerciseType = exerciseType )
        val preambleCriterionsList = excerciseCriterionsList.map { excerciseCriterions -> ExerciseFeedBack.preambleCriterion(exerciseCriterion = excerciseCriterions, onSuccess =  {}, onFailure = {})}

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

when (exerciseType.detail) {
    is ExerciseDetail.RepetitionBased -> {
        assert(excerciseCriterionsList.size >1) //Repetition based exercises are multiple pose exercise !
        //asses all preambles
        val assessedPreambles = preambleCriterionsList.map { preambleCriterions -> data.map { pose -> assessLandMarks(pose, preambleCriterions).first }  }
        val nbRep = countAlternates(assessedPreambles)
        val adviceBuilder = StringBuilder()

        adviceBuilder.append("Repetitions: $nbRep \n")

        (excerciseCriterionsList zip preambleCriterionsList ).forEach { (exCriterion, preambleCriterions) ->
            adviceBuilder.append(getFeedBackSingleExercise(data = data, excerciseCriterions = exCriterion, preambleCriterions = preambleCriterions))
        }



        return adviceBuilder.toString()






    }
    is ExerciseDetail.TimeBased -> {
        assert(excerciseCriterionsList.size == 1) //Time based exercises are single pose exercise !

        return getFeedBackSingleExercise(data = data,
            excerciseCriterions = excerciseCriterionsList.first(),
            preambleCriterions = preambleCriterionsList.first(), prependDuration = true)
    }

}




    }


    fun getFeedBackSingleExercise(data: List<List<Triple<Float, Float, Float>>>, excerciseCriterions : ExerciseCriterion , preambleCriterions : ExerciseCriterion, prependDuration: Boolean = false) : String{
        val data_preambleActived = data.filter { sample -> ExerciseFeedBack.assessLandMarks(sample, preambleCriterions).first }
        val exerciseDuration = data_preambleActived.size * PoseDetectionAnalyser.THROTTLE_TIMEOUT_MS / 1000L
        //compute the distance from the target to the reference for each angle criterion
        //get the list of comments
        val assessedExercise = data_preambleActived.map { sample ->
            ExerciseFeedBack.assessLandMarks(sample, exerciseCriterion = excerciseCriterions)
        }
        val freqThreshold = 0.0F //If a mistake is make with a higher frequency, give advice to the user

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
        if (prependDuration){
            adviceBuilder.append("Duration:$exerciseDuration s \n")
        }
        val listOfAdvice = allComments.forEach{ (comment, freq) ->
            adviceBuilder.append(comment.description).append(" rate: ${freq*100}%").append("\n")
        }
        val advice = adviceBuilder.toString()

        return advice
    }


    fun countAlternates(assessedPoses : List<List<Boolean>>) : Int{
        //listes have the same lenght, remove when there is more than one true for the same index

        val stateList = ArrayList<Int>()
    for (index in 0 until assessedPoses[0].size) {
        var nbTrue = 0
        var trueCriterion =-1
        for (criteriaIndex in 0 until assessedPoses.size){

            if (assessedPoses[criteriaIndex][index]){
                nbTrue++
                trueCriterion = criteriaIndex
            }
        }
        if(nbTrue == 1){
            stateList.add(trueCriterion)
        }
    }

        //count the alternating number
        var count = 0
        var current = if (stateList.isNotEmpty()) stateList.first() else -1
        for (i in 0 until stateList.size ){
            if (stateList[i] == (current+1)%(assessedPoses.size)){ //handles switching from state 1 -> 2-> 3 ...
                 count++
                current= stateList[i]
            }
        }
        return count/2
    }




}