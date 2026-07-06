package com.ddeeaaddllyy.zenith.data.repository

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.ddeeaaddllyy.zenith.data.local.dao.StepsBaselineDao
import com.ddeeaaddllyy.zenith.data.local.entity.StepsBaselineEntity
import com.ddeeaaddllyy.zenith.domain.repository.StepsRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class StepsRepositoryImpl(
    context: Context,
    private val dao: StepsBaselineDao
) : StepsRepository {

    private val sensorManager = context.applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    override val hasStepSensor: Boolean get() = stepCounterSensor != null

    override val todaySteps: Flow<Int> = callbackFlow {
        val sensor = stepCounterSensor
        if (sensor == null) {
            trySend(0)
            awaitClose { }
        } else {
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    val cumulative = event.values[0].toInt()
                    launch {
                        val today = LocalDate.now().toString()
                        val existing = dao.get()
                        val baseline = if (existing == null || existing.date != today) {
                            val fresh = StepsBaselineEntity(date = today, baselineCount = cumulative)
                            dao.upsert(fresh)
                            fresh
                        } else {
                            existing
                        }
                        trySend((cumulative - baseline.baselineCount).coerceAtLeast(0))
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit
            }

            runCatching {
                sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
            }

            awaitClose { sensorManager.unregisterListener(listener) }
        }
    }
}
