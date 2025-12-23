package com.hatchbro.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.hatchbro.app.data.repository.HatcheryRepository
import com.hatchbro.app.domain.calculators.DateCalculator
import com.hatchbro.app.utils.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.Date

@HiltWorker
class BatchMonitorWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: HatcheryRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val activeBatches = repository.activeBatches.first()
        val today = Calendar.getInstance()
        
        activeBatches.forEach { batch ->
            // Check Lockdown
            if (isSameDay(today.time, batch.lockdownDate)) {
                NotificationHelper.showNotification(
                    applicationContext,
                    batch.id.toInt() * 10 + 1,
                    "Lockdown Reminder",
                    "Batch #${batch.id} is entering lockdown today!",
                    NotificationHelper.CHANNEL_ID_REMINDERS
                )
            }
            // Check Hatch
            if (isSameDay(today.time, batch.expectedHatchDate)) {
                NotificationHelper.showNotification(
                    applicationContext,
                    batch.id.toInt() * 10 + 2,
                    "Hatch Day!",
                    "Batch #${batch.id} is expected to hatch today!",
                    NotificationHelper.CHANNEL_ID_REMINDERS
                )
            }
        }
        
        return Result.success()
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}
