package com.ddeeaaddllyy.zenith.analytics

import com.ddeeaaddllyy.zenith.domain.model.Gender
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent

object Analytic {
    fun sendWorkoutAnalytic() = Firebase.analytics.logEvent("create_workout", null)

    fun sendDiaryAnalytics() = Firebase.analytics.logEvent("create_diary", null)

    fun sendGenderAnalytics(gender: Gender) {
        Firebase.analytics.logEvent("choose_gender") {
            param("gender", gender.name)
        }
    }
}