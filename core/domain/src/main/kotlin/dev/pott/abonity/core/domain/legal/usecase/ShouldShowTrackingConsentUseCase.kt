package dev.pott.abonity.core.domain.legal.usecase

import dev.pott.abonity.core.domain.legal.LegalRepository
import dev.pott.abonity.core.entity.legal.ConsentGrant
import dev.pott.abonity.core.entity.legal.Legal
import dev.pott.abonity.core.entity.legal.ServiceCategory
import dev.pott.abonity.core.entity.legal.TrackingService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ShouldShowTrackingConsentUseCase @Inject constructor(
    private val legalRepository: LegalRepository,
) {
    operator fun invoke(): Flow<Boolean> {
        return legalRepository.getLegal().map { legal ->
            !hasConsentsForAllServices(legal)
        }
    }

    private fun hasConsentsForAllServices(legal: Legal): Boolean {
        val serviceIdsWithRequiredConsent = TrackingService.entries
            .filter { it.serviceCategory != ServiceCategory.REQUIRED }
            .map { it.serviceId }
        return legal.consents
            .filter { it.consentGrant != ConsentGrant.UNKNOWN }
            .map { it.serviceId }
            .containsAll(serviceIdsWithRequiredConsent)
    }
}
