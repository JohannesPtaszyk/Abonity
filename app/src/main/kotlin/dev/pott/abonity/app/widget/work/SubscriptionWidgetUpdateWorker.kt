package dev.pott.abonity.app.widget.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.pott.abonity.core.domain.subscription.SubscriptionWidgetUpdater

@HiltWorker
class SubscriptionWidgetUpdateWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val widgetUpdater: SubscriptionWidgetUpdater,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        widgetUpdater.update()
        return Result.success()
    }
}
