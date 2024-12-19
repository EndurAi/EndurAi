package com.android.sample.mlUtils

import MathsPoseDetection
import com.android.sample.mlUtils.ExerciseFeedBack.Companion.ExerciseCriterion
import com.android.sample.mlUtils.ExerciseFeedBack.Companion.assessLandMarks
import com.android.sample.mlUtils.exercisesCriterions.AngleCriterionComments
import com.android.sample.model.camera.CameraViewModel
import com.android.sample.model.workout.ExerciseDetail
import com.android.sample.model.workout.ExerciseType

class MlCoach(val cameraViewModel: CameraViewModel, private val exerciseType: ExerciseType) {

  /**
   * Provides feedback for the exercise based on the type of exercise and the pose landmarks.
   *
   * @return A list of `CoachFeedback` objects containing feedback for the exercise.
   */
  fun getFeedback(): List<CoachFeedback> {
    // get the criterion and the preamble

    val excerciseCriterionsList = ExerciseFeedBack.getCriterions(exerciseType = exerciseType)
    val preambleCriterionsList =
        excerciseCriterionsList.map { excerciseCriterions ->
          ExerciseFeedBack.preambleCriterion(
              exerciseCriterion = excerciseCriterions, onSuccess = {}, onFailure = {})
        }

    // get the stacked value from the camera view model
    val rawData = cameraViewModel._poseLandMarks.value
    // average the data to reduce noise
    val windowSize = 5
    val windowStep = 1
    // 1st dim : Sample number, 2nd dim : a joint fo the sample, the 3rd dim : the three coordinates
    // of the joint
    val data: List<List<MyPoseLandmark>> =
        rawData.windowed(windowSize, windowStep, partialWindows = false).map { window ->
          MathsPoseDetection.window_mean(window)
        }

    when (exerciseType.detail) {
      is ExerciseDetail.RepetitionBased -> {
        assert(
            excerciseCriterionsList.size >
                1) // Repetition based exercises are multiple pose exercise !
        // asses all preambles
        val assessedPreambles =
            preambleCriterionsList.map { preambleCriterions ->
              data.map { pose -> assessLandMarks(pose, preambleCriterions).first }
            }
        val nbRep = countAlternates(assessedPreambles)

        val listOfCoachAdvice =
            (excerciseCriterionsList zip preambleCriterionsList).map {
                (exCriterion, preambleCriterions) ->
              getFeedBackSingleExercise(
                  data = data,
                  exerciseCriterion = exCriterion,
                  preambleCriterions = preambleCriterions,
                  numberOfRepetition = nbRep)
            }
        return listOfCoachAdvice
      }
      is ExerciseDetail.TimeBased -> {
        // If the exercise pose are not symetric eg : the warrior 2  pose can be done on the left or
        // on the right
        if (excerciseCriterionsList.all { exerciseCriterion -> !exerciseCriterion.symmetric }) {
          assert(excerciseCriterionsList.size == 2) // for the left and right part
          // select the one with the most detected preamble
          val bestSideAssessement =
              (excerciseCriterionsList zip preambleCriterionsList)
                  .map { (excerciseCriterion, preambleCriterion) ->
                    getFeedBackSingleExercise(
                        data = data,
                        exerciseCriterion = excerciseCriterion,
                        preambleCriterions = preambleCriterion,
                        isTimeBased = true)
                  }
                  .sortedBy { it.successRate }
                  .last()

          return listOf(bestSideAssessement)
        } else {
          assert(
              excerciseCriterionsList.size == 1) // Time based exercises are single pose exercise !
          return listOf(
              getFeedBackSingleExercise(
                  data = data,
                  exerciseCriterion = excerciseCriterionsList.first(),
                  preambleCriterions = preambleCriterionsList.first(),
                  isTimeBased = true))
        }
      }
    }
  }
  /**
   * Provides feedback for a single exercise based on the given data and criteria.
   *
   * @param data A list of lists of `MyPoseLandmark` objects representing the pose landmarks.
   * @param exerciseCriterion The criteria for the exercise.
   * @param preambleCriterions The preamble criteria for the exercise.
   * @param isTimeBased A boolean indicating if the exercise is time-based.
   * @param numberOfRepetition The number of repetitions for the exercise.
   * @return A `CoachFeedback` object containing feedback for the exercise.
   */
  fun getFeedBackSingleExercise(
      data: List<List<MyPoseLandmark>>,
      exerciseCriterion: ExerciseCriterion,
      preambleCriterions: ExerciseCriterion,
      isTimeBased: Boolean = false,
      numberOfRepetition: Int = 0,
  ): CoachFeedback {
    val data_preambleActived =
        data.filter { sample -> ExerciseFeedBack.assessLandMarks(sample, preambleCriterions).first }

    var exerciseDuration = 0L
    if (data_preambleActived.isNotEmpty()) {
      val firstTimeStamp = data_preambleActived.first().first().timeStamp
      val lastTimeStamp = data_preambleActived.last().first().timeStamp
      exerciseDuration = (lastTimeStamp - firstTimeStamp) / 1000L
    }
    // get the list of comments
    val assessedExercise =
        data_preambleActived.map { sample ->
          ExerciseFeedBack.assessLandMarks(sample, exerciseCriterion = exerciseCriterion)
        }
    val nbComments = assessedExercise.fold(0, { acc, (boolean, list) -> acc + list.size })
    val allComments =
        assessedExercise
            .flatMap { (sampleIsSucceed, commentList) -> commentList }
            .groupingBy { it }
            .eachCount()
            .toList()
            .map { (comment, count) -> Pair(comment, count / nbComments.toFloat()) }
            .sortedBy { (_, freq) -> freq }

    var successRate = 0F
    val jointFeedbackSet: MutableSet<JointFeedback> = mutableSetOf()
    val listOfAdvice =
        allComments.forEach { (comment, freq) ->
          if (comment == AngleCriterionComments.SUCCESS) {
            successRate = freq
          } else {
            jointFeedbackSet.add(JointFeedback(comment = comment.description, rate = freq))
          }
        }

    return if (isTimeBased) {
      CoachFeedback(
          commentSet = jointFeedbackSet,
          successRate = successRate,
          feedbackValue = exerciseDuration.toInt(),
          isCommented = exerciseCriterion.isCommented,
          feedbackUnit = ExerciseFeedBackUnit.SECONDS,
          exerciseCriterion = exerciseCriterion)
    } else {
      CoachFeedback(
          commentSet = jointFeedbackSet,
          successRate = successRate,
          isCommented = exerciseCriterion.isCommented,
          feedbackValue = numberOfRepetition,
          feedbackUnit = ExerciseFeedBackUnit.REPETITION,
          exerciseCriterion = exerciseCriterion)
    }
  }
}

/**
 * Counts the number of alternates in the assessed poses.
 *
 * @param assessedPoses A list of lists of Booleans representing the assessed poses.
 * @return The number of alternates in the assessed poses.
 */
fun countAlternates(assessedPoses: List<List<Boolean>>): Int {
  // Each column of the matrix represent the detection state of a given exercise criterion eg: for
  // the pushups, the 1st column is the detection of the up position and the 2nd column is the
  // detection of the down position
  // lists have the same length, remove when there is more than one true for the same index

  val stateList = ArrayList<Int>()
  for (index in 0 until assessedPoses[0].size) {
    var nbTrue = 0
    var trueCriterion = -1
    for (criteriaIndex in 0 until assessedPoses.size) {

      if (assessedPoses[criteriaIndex][index]) {
        nbTrue++
        trueCriterion = criteriaIndex
      }
    }
    if (nbTrue == 1) {
      stateList.add(trueCriterion) // add the state (represented by an integer)
    }
  }

  // count the alternating number
  var count = 0
  var current = if (stateList.isNotEmpty()) stateList.first() else -1
  for (i in 0 until stateList.size) {
    if (stateList[i] ==
        (current + 1) % (assessedPoses.size)) { // handles switching from state 1 -> 2-> 3 ...
      count++
      current = stateList[i]
    }
  }
  return count / 2
}
