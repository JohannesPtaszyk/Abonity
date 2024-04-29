package dev.pott.abonity.core.test.legal.entities

import dev.pott.abonity.core.entity.legal.Consent
import dev.pott.abonity.core.entity.legal.ConsentGrant
import dev.pott.abonity.core.entity.legal.Legal
import dev.pott.abonity.core.entity.legal.ServiceCategory
import dev.pott.abonity.core.entity.legal.TrackingService

fun createTestLegal(consents: List<Consent> = createConsentListAllUnknown()) =
    Legal(consents = consents)

fun createConsentListAllUnknown(): List<Consent> =
    TrackingService.entries
        .filter { it.serviceCategory != ServiceCategory.REQUIRED }
        .map {
            Consent(
                serviceId = it.serviceId,
                consentGrant = ConsentGrant.UNKNOWN,
            )
        }
