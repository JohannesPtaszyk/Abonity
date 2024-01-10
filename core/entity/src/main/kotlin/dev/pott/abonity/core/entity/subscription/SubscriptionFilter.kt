package dev.pott.abonity.core.entity.subscription

data class SubscriptionFilter(
    val items: List<SubscriptionFilterItem>,
    val selectedItems: List<SubscriptionFilterItem>,
)
