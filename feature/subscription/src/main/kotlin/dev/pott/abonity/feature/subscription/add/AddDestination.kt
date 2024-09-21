package dev.pott.abonity.feature.subscription.add

import androidx.navigation.NavController
import dev.pott.abonity.core.entity.subscription.SubscriptionId
import kotlinx.serialization.Serializable

@Serializable
data class AddDestination(val subscriptionId: SubscriptionId? = null)

fun NavController.navigateToAddDestination(subscriptionId: SubscriptionId? = null) {
    navigate(AddDestination(subscriptionId))
}
