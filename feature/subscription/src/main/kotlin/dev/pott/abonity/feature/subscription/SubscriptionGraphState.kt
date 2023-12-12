package dev.pott.abonity.feature.subscription

import androidx.compose.runtime.Stable

@Stable
data class SubscriptionGraphState(
    val showOverviewAsMultiColumn: Boolean,
    val showAddFloatingActionButton: Boolean,
)
