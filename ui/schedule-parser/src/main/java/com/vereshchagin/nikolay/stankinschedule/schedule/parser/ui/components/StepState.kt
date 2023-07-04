package com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.components

class StepState(
    val currentStep: Int,
    val totalSteps: Int
) {

    val isStart: Boolean get() = currentStep == 1

    fun next() = StepState(
        currentStep = (currentStep + 1).coerceAtMost(totalSteps),
        totalSteps = totalSteps
    )

    fun back() = StepState(
        currentStep = (currentStep - 1).coerceAtLeast(1),
        totalSteps = totalSteps
    )
}