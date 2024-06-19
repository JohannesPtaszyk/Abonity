package dev.pott.abonity.core.local.legal.datastore.mapper

import dev.pott.abonity.core.entity.legal.Consent
import dev.pott.abonity.core.entity.legal.ConsentGrant
import dev.pott.abonity.core.entity.legal.ConsentServiceId
import dev.pott.abonity.core.entity.legal.Legal
import dev.pott.abonity.core.local.legal.datastore.entities.ConsentEntity
import dev.pott.abonity.core.local.legal.datastore.entities.ConsentGrantEntity
import dev.pott.abonity.core.local.legal.datastore.entities.ConsentServiceIdEntity
import dev.pott.abonity.core.local.legal.datastore.entities.LegalEntity

fun Legal.toEntity(): LegalEntity = LegalEntity(consents = consents.map { it.toEntity() })

fun Consent.toEntity(): ConsentEntity =
    ConsentEntity(
        serviceId = serviceId.toEntity(),
        consentGrant = consentGrant.toEntity(),
    )

private fun ConsentServiceId.toEntity(): ConsentServiceIdEntity =
    when (this) {
        ConsentServiceId.FIREBASE_ANALYTICS -> {
            ConsentServiceIdEntity.FIREBASE_ANALYTICS
        }
        ConsentServiceId.FIREBASE_CRASHLYTICS -> {
            ConsentServiceIdEntity.FIREBASE_CRASHLYTICS
        }
        ConsentServiceId.FIREBASE_PERFORMANCE -> {
            ConsentServiceIdEntity.FIREBASE_PERFORMANCE
        }
        ConsentServiceId.FIREBASE_CLOUD_MESSAGING -> {
            ConsentServiceIdEntity.FIREBASE_CLOUD_MESSAGING
        }
        ConsentServiceId.FIREBASE_IN_APP_MESSAGING -> {
            ConsentServiceIdEntity.FIREBASE_IN_APP_MESSAGING
        }
        ConsentServiceId.FIREBASE_INSTALLATIONS -> {
            ConsentServiceIdEntity.FIREBASE_INSTALLATIONS
        }
        ConsentServiceId.FIREBASE_REMOTE_CONFIG -> {
            ConsentServiceIdEntity.FIREBASE_REMOTE_CONFIG
        }
    }

private fun ConsentGrant.toEntity(): ConsentGrantEntity =
    when (this) {
        ConsentGrant.GRANTED -> ConsentGrantEntity.GRANTED
        ConsentGrant.DENIED -> ConsentGrantEntity.DENIED
        ConsentGrant.UNKNOWN -> ConsentGrantEntity.UNKNOWN
    }
