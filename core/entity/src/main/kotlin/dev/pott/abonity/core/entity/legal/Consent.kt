package dev.pott.abonity.core.entity.legal

data class Consent(
    val serviceId: ConsentServiceId,
    val consentGrant: ConsentGrant,
)
