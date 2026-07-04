package com.ddeeaaddllyy.zenith.analytics

import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics

object Analytic {
    fun sendWorkoutAnalytic() = Firebase.analytics.logEvent("create_workout", null)

    fun sendDiaryAnalytics() = Firebase.analytics.logEvent("create_diary", null)
}