package dev.pott.abonity.core.local.legal.datastore.mapper

import dev.pott.abonity.core.entity.legal.Consent
import dev.pott.abonity.core.entity.legal.ConsentGrant
import dev.pott.abonity.core.entity.legal.ConsentServiceId
import dev.pott.abonity.core.entity.legal.Legal
import dev.pott.abonity.core.local.legal.datastore.entities.ConsentEntity
import dev.pott.abonity.core.local.legal.datastore.entities.ConsentGrantEntity
import dev.pott.abonity.core.local.legal.datastore.entities.ConsentServiceIdEntity
import dev.pott.abonity.core.local.legal.datastore.entities.LegalEntity

fun LegalEntity.toDomain(): Legal =
    Legal(
        consents = consents.map { it.toDomain() },
    )

fun ConsentEntity.toDomain(): Consent =
    Consent(
        serviceId = serviceId.toDomain(),
        consentGrant = consentGrant.toDomain(),
    )

private fun ConsentServiceIdEntity.toDomain(): ConsentServiceId =
    when (this) {
        ConsentServiceIdEntity.FIREBASE_ANALYTICS -> {
            ConsentServiceId.FIREBASE_ANALYTICS
        }

        ConsentServiceIdEntity.FIREBASE_CRASHLYTICS -> {
            ConsentServiceId.FIREBASE_CRASHLYTICS
        }

        ConsentServiceIdEntity.FIREBASE_PERFORMANCE -> {
            ConsentServiceId.FIREBASE_PERFORMANCE
        }

        ConsentServiceIdEntity.FIREBASE_CLOUD_MESSAGING -> {
            ConsentServiceId.FIREBASE_CLOUD_MESSAGING
        }

        ConsentServiceIdEntity.FIREBASE_IN_APP_MESSAGING -> {
            ConsentServiceId.FIREBASE_IN_APP_MESSAGING
        }

        ConsentServiceIdEntity.FIREBASE_INSTALLATIONS -> {
            ConsentServiceId.FIREBASE_INSTALLATIONS
        }

        ConsentServiceIdEntity.FIREBASE_REMOTE_CONFIG -> {
            ConsentServiceId.FIREBASE_REMOTE_CONFIG
        }
    }

private fun ConsentGrantEntity.toDomain(): ConsentGrant =
    when (this) {
        ConsentGrantEntity.GRANTED -> ConsentGrant.GRANTED
        ConsentGrantEntity.DENIED -> ConsentGrant.DENIED
        ConsentGrantEntity.UNKNOWN -> ConsentGrant.UNKNOWN
    }
