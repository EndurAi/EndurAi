package com.android.sample.mlUtils.exercisesCriterions
enum class AngleCriterionComments(val description: String) {
    SUCCESS("Success"),
    NOT_IMPLEMENTED("Not implemented yet"),
    BACK_NOT_FLAT("Your back should be more right"),
    BODY_NOT_BENDED("Try bending more your body"),

    ELBOW_RIGHT_NOT_FLAT("Keep your right elbow angle flat"),
    ELBOW_LEFT_NOT_FLAT("Keep your right elbow angle flat"),
    ELBOW_RIGHT_NOT_RIGHT("Your right wrist, elbow and shoulder should make a right angle."),
    ELBOW_LEFT_NOT_RIGHT("Your left wrist, elbow and shoulder should make a right angle."),
    ELBOW_BOTH_NOT_RIGHT("Your wrists, elbows and shoulders should make a right angle."),
    SHOULDER_RIGHT_NOT_RIGHT("Keep your left elbow right, aligned with your shoulder."),
    SHOULDER_LEFT_NOT_RIGHT("Keep your left elbow right, aligned with your shoulder."),
    SHOULDER_BOTH_NOT_RIGHT("Keep elbows right, aligned with your shoulders."),


    LEG_LEFT_NOT_FLAT("Keep your left leg stretched"),
    LEG_RIGHT_NOT_FLAT("Keep your right leg stretched"),
    LEG_BOTH_NOT_FLAT("Keep your legs stretched"),

    LEG_RIGHT_NOT_RIGHT("Keep an angle of 90 degree with your right leg."),
    LEG_LEFT_NOT_RIGHT("Keep an angle of 90 degree with your left leg."),
    LEG_BOTH_NOT_RIGHT("Keep an angle of 90 degree with your legs."),

    LEG_RIGHT_NOT_SPREAD("Spread more your right leg."),
    LEG_LEFT_NOT_SPREAD("Spread more your left leg."),
    LEG_BOTH_NOT_SPREAD("Spread more your legs."),

    LEG_RIGHT_NOT_TIGHT("Tighten more your right leg."),
    LEG_LEFT_NOT_TIGHT("Tighten more your left leg."),
    LEG_BOTH_NOT_TIGHT("Tighten more your legs."),



}