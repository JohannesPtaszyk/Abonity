package dev.pott.abonity.core.test.subscription

import dev.pott.abonity.core.domain.subscription.SubscriptionWidgetUpdater

class FakeSubscriptionWidgetUpdater : SubscriptionWidgetUpdater {
    var updatedCount: Int = 0

    override suspend fun update() {
        updatedCount++
    }
}
