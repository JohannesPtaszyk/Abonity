package dev.pott.abonity.core.domain.subscription

class FakeSubscriptionNotificationScheduler : SubscriptionNotificationScheduler {
    var immediateCheckCount: Int = 0
        private set

    override fun scheduleImmediateCheck() {
        immediateCheckCount++
    }
}
