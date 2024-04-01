package dev.pott.abonity.core.domain.legal.usecase

import dev.pott.abonity.core.domain.legal.LegalRepository
import dev.pott.abonity.core.entity.legal.ConsentGrant
import dev.pott.abonity.core.entity.legal.TrackingService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTrackingServicesWithGrantUseCase @Inject constructor(
    private val legalRepository: LegalRepository,
) {
    operator fun invoke(): Flow<Map<TrackingService, ConsentGrant>> {
        return legalRepository.getLegal().map { legal ->
            val consents = legal.consents
            TrackingService.entries.associateWith {
                val serviceGrant = consents.find { consent ->
                    consent.serviceId == it.serviceId
                }?.consentGrant
                serviceGrant ?: ConsentGrant.UNKNOWN
            }
        }
    }
}
