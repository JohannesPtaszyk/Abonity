package dev.pott.abonity.feature.subscription.detail

import dev.pott.abonity.core.entity.Subscription

data class DetailState(
    val subscription: Subscription? = null,
    val loading: Boolean = true,
)
