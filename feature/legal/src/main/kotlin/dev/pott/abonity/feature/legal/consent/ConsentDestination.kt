package dev.pott.abonity.feature.legal.consent

import androidx.navigation.NavController
import dev.pott.abonity.navigation.destination.Destination

object ConsentDestination : Destination {
    override val route: String = "consent"
}

fun NavController.navigateToConsentDialog() {
    navigate(ConsentDestination.route)
}
