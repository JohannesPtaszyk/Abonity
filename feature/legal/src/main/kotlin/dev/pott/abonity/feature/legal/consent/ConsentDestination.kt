package dev.pott.abonity.feature.legal.consent

import androidx.navigation.NavController
import kotlinx.serialization.Serializable

@Serializable
data object ConsentDestination

fun NavController.navigateToConsent() {
    navigate(ConsentDestination)
}
