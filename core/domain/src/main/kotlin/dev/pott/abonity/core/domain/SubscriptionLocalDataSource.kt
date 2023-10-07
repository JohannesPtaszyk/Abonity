package dev.pott.abonity.core.domain

import dev.pott.abonity.core.entity.Subscription
import kotlinx.coroutines.flow.Flow

interface SubscriptionLocalDataSource {
    fun getSubscriptionFlow(): Flow<List<Subscription>>
}
