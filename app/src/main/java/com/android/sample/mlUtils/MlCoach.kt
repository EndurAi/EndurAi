package com.android.sample.mlUtils

import MathsPoseDetection
import com.android.sample.model.camera.CameraViewModel
import com.android.sample.model.workout.ExerciseType
import com.android.sample.mlUtils.ExerciseFeedBack.Companion.ExerciseCriterion
import com.android.sample.mlUtils.ExerciseFeedBack.Companion.assessLandMarks
import com.android.sample.mlUtils.exercisesCriterions.AngleCriterionComments
import com.android.sample.model.workout.ExerciseDetail

class MlCoach(val cameraViewModel: CameraViewModel, private val exerciseType: ExerciseType) {

    fun getFeedback(): List<CoachFeedback> {
        // get the criterion and the preamble

        val excerciseCriterionsList = ExerciseFeedBack.getCriterions(exerciseType = exerciseType )
        val preambleCriterionsList = excerciseCriterionsList.map { excerciseCriterions -> ExerciseFeedBack.preambleCriterion(exerciseCriterion = excerciseCriterions, onSuccess =  {}, onFailure = {})}

        // get the stacked value from the camera view model
        val rawData = cameraViewModel._poseLandMarks.value
        // mean the data
        val windowSize = 1
        val windowStep = 1
        // 1st dim : Sample number, 2nd dim : a joint fo the sample, the 3rd dim : the three coordinates of the joint
        val data : List<List<MyPoseLandmark>> = rawData
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


        val listOfCoachAdvice =  (excerciseCriterionsList zip preambleCriterionsList ).map { (exCriterion, preambleCriterions) ->
           getFeedBackSingleExercise(data = data, excerciseCriterions = exCriterion, preambleCriterions = preambleCriterions, numberOfRepetition = nbRep)
        }
        return listOfCoachAdvice










    }
    is ExerciseDetail.TimeBased -> {
        //If the exercise pose are not symetric eg : the warrior 2  pose can be done on the left or on the right
        if (excerciseCriterionsList.all { exerciseCriterion -> exerciseCriterion.symmetric }) {
            assert(excerciseCriterionsList.size == 2) // for the left and right part
            //select the one with the most detected preamble
           val bestSideAssessement = (excerciseCriterionsList zip preambleCriterionsList).map { (excerciseCriterion, preambleCriterion)-> getFeedBackSingleExercise(
               data = data,
               excerciseCriterions = excerciseCriterion,
               preambleCriterions = preambleCriterion,
               isTimeBased = true
           )}.sortedBy { it.successRate }.last()

            return listOf(bestSideAssessement)













        } else {
            assert(excerciseCriterionsList.size == 1) //Time based exercises are single pose exercise !

            val timeStamps = rawData.map { pose ->
                pose[0].timeStamp
            }
            return listOf(getFeedBackSingleExercise(
                data = data,
                excerciseCriterions = excerciseCriterionsList.first(),
                preambleCriterions = preambleCriterionsList.first(),
                isTimeBased = true
            ))
        }

    }
}




    }


    fun getFeedBackSingleExercise(data: List<List<MyPoseLandmark>>,
                                  excerciseCriterions : ExerciseCriterion ,
                                  preambleCriterions : ExerciseCriterion,
                                  isTimeBased: Boolean = false,
                                  numberOfRepetition : Int = 0 ,
    ) : CoachFeedback {
        val data_preambleActived = data.filter { sample -> ExerciseFeedBack.assessLandMarks(sample, preambleCriterions).first }


        var exerciseDuration = 0L
        if (data_preambleActived.isNotEmpty()) {
            val firstTimeStamp = data_preambleActived.first().first().timeStamp
            val lastTimeStamp = data_preambleActived.last().first().timeStamp
            exerciseDuration = (lastTimeStamp - firstTimeStamp) / 1000L
        }
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

        var successRate = 0F
        var jointFeedbackSet: MutableSet<JointFeedback> = mutableSetOf()
        val listOfAdvice = allComments.forEach{ (comment, freq) ->
            if(comment == AngleCriterionComments.SUCCESS){
                successRate = freq
            }
            else {
                jointFeedbackSet.add(JointFeedback(comment = comment.description, rate = freq))
            }
            }


        return if (isTimeBased){
            CoachFeedback(
                commentSet = jointFeedbackSet,
                successRate = successRate,
                repOrDuration = exerciseDuration.toInt(),
                "s")
        } else{
            CoachFeedback(
                commentSet = jointFeedbackSet,
                successRate = successRate,
                repOrDuration = numberOfRepetition,
                "repetitions")
        }

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