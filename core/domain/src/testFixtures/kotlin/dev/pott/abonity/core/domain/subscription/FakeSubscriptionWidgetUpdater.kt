package dev.pott.abonity.core.domain.subscription

class FakeSubscriptionWidgetUpdater : SubscriptionWidgetUpdater {
    var updatedCount: Int = 0

    override suspend fun update() {
        updatedCount++
    }
}
