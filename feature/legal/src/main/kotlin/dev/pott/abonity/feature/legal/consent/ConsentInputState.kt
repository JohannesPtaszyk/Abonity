package dev.pott.abonity.feature.legal.consent

import dev.pott.abonity.core.entity.legal.ConsentGrant
import dev.pott.abonity.core.entity.legal.ConsentServiceId

data class ConsentInputState(
    val showSecondLayer: Boolean = false,
    val shouldClose: Boolean = false,
    val grants: Map<ConsentServiceId, ConsentGrant> = emptyMap(),
)
