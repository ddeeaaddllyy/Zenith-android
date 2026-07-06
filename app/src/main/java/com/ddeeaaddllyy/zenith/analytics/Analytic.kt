package com.ddeeaaddllyy.zenith.analytics

import com.ddeeaaddllyy.zenith.domain.model.Gender
import com.google.firebase.Firebase
import com.google.firebase.analytics.ParametersBuilder
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent

object Analytic {
    private inline fun logEvent(
        name: String,
        builder: ParametersBuilder.() -> Unit = {}
    ) {
        Firebase.analytics.logEvent(name, builder)
    }

    fun sendWorkoutAnalytic() =
        logEvent("create_workout")

    fun sendDiaryAnalytics() =
        logEvent("create_diary")

    fun sendGenderAnalytics(gender: Gender) =
        logEvent("choose_gender") {
            param("gender", gender.name)
        }

    fun sendStepAnalytics(steps: Long) =
        logEvent("step_count") {
            param("steps", steps)
        }
}