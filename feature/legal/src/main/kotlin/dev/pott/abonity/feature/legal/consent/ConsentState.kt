package dev.pott.abonity.feature.legal.consent

import dev.pott.abonity.core.entity.legal.ConsentGrant
import dev.pott.abonity.core.entity.legal.TrackingService
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf

data class ConsentState(
    val consents: PersistentMap<TrackingService, ConsentGrant> = persistentMapOf(),
    val showSecondLayer: Boolean = false,
    val shouldClose: Boolean = false,
    val privacyPolicyUrl: String = "",
)
