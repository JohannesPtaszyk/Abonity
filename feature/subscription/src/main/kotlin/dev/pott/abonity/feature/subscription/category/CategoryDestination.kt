package dev.pott.abonity.feature.subscription.category

import androidx.navigation.NavController
import kotlinx.serialization.Serializable

@Serializable
data object CategoryDestination

fun NavController.navigateToCategoryDestination() {
    navigate(CategoryDestination)
}
