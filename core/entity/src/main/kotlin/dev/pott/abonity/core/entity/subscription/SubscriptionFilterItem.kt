package dev.pott.abonity.core.entity.subscription

sealed interface SubscriptionFilterItem {

    data class Currency(val price: Price) : SubscriptionFilterItem

    data class CurrentPeriod(val period: PaymentPeriod) : SubscriptionFilterItem

    data class Category(val category: dev.pott.abonity.core.entity.subscription.Category) :
        SubscriptionFilterItem
}
