package dev.pott.abonity.core.domain

import dev.pott.abonity.core.entity.Subscription
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    fun getSubscriptionFlow(): Flow<List<Subscription>>
}
