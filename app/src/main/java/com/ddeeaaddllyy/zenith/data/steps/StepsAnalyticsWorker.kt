package com.ddeeaaddllyy.zenith.data.steps

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ddeeaaddllyy.zenith.ZenithApplication
import com.ddeeaaddllyy.zenith.analytics.Analytic
import kotlinx.coroutines.flow.first

/**
 * Fires once nightly (see [StepsAnalyticsScheduler]) to report today's step count,
 * then immediately reschedules itself for the next night.
 */
class StepsAnalyticsWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val container = (applicationContext as ZenithApplication).container
        val stepsRepository = container.stepsRepository
        if (stepsRepository.hasStepSensor) {
            val steps = stepsRepository.todaySteps.first()
            Analytic.sendStepAnalytics(steps.toLong())
        }
        StepsAnalyticsScheduler.scheduleNext(applicationContext)
        return Result.success()
    }
}
