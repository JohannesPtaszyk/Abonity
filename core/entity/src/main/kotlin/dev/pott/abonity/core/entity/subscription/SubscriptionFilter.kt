package dev.pott.abonity.core.entity.subscription

data class SubscriptionFilter(
    val items: List<SubscriptionFilterItem>,
    val selectedItems: List<SubscriptionFilterItem>,
) {

    constructor(
        prices: List<Price>,
        period: PaymentPeriod,
        selectedItems: List<SubscriptionFilterItem>,
    ) : this(
        items = buildList<SubscriptionFilterItem> {
            add(SubscriptionFilterItem.CurrentPeriod(period))
            addAll(prices.map { SubscriptionFilterItem.Currency(it) })
        }.sortedBy {
            !selectedItems.contains(it)
        },
        selectedItems = selectedItems,
    )
}
