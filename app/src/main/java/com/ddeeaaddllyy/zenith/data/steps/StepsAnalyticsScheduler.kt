package com.ddeeaaddllyy.zenith.data.steps

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

/**
 * Schedules [StepsAnalyticsWorker] to run once, landing in the 23:00–01:00 window.
 * The worker reschedules itself for the following night after it runs, so this only
 * needs to be kicked off once (from app startup).
 */
object StepsAnalyticsScheduler {
    private const val WORK_NAME = "steps_analytics_nightly"
    private const val TARGET_HOUR = 23

    fun scheduleNext(context: Context) {
        val now = LocalDateTime.now()
        var target = now.toLocalDate().atTime(TARGET_HOUR, 0)
        if (!now.isBefore(target)) {
            target = target.plusDays(1)
        }
        val delay = Duration.between(now, target).toMillis()

        val request = OneTimeWorkRequestBuilder<StepsAnalyticsWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}
