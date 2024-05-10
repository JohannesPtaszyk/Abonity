package dev.pott.abonity.feature.subscription.category

import androidx.navigation.NavController
import dev.pott.abonity.navigation.destination.Destination

object CategoryDestination : Destination {
    override val route: String = "category"
}

fun NavController.navigateToCategory() {
    navigate(CategoryDestination.route)
}
