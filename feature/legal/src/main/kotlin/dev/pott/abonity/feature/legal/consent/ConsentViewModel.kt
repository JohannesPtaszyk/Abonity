package dev.pott.abonity.feature.legal.consent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pott.abonity.core.domain.config.ConfigRepository
import dev.pott.abonity.core.domain.legal.LegalRepository
import dev.pott.abonity.core.domain.legal.usecase.GetTrackingServicesWithGrantUseCase
import dev.pott.abonity.core.entity.legal.Consent
import dev.pott.abonity.core.entity.legal.ConsentGrant
import dev.pott.abonity.core.entity.legal.ConsentServiceId
import dev.pott.abonity.core.entity.legal.ServiceCategory
import dev.pott.abonity.core.entity.legal.TrackingService
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConsentViewModel @Inject constructor(
    private val legalRepository: LegalRepository,
    configRepository: ConfigRepository,
    getTrackingServicesWithGrant: GetTrackingServicesWithGrantUseCase,
) : ViewModel() {

    private val consentInputState = MutableStateFlow(ConsentInputState())

    val state = combine(
        getTrackingServicesWithGrant(),
        consentInputState,
        configRepository.getConfig().map { it.legal.privacyPolicyUrl }.distinctUntilChanged(),
    ) { servicesWithGrant, inputState, privacyPolicyUrl ->
        ConsentState(
            consents = servicesWithGrant.mapValues { (service, grant) ->
                consentInputState.value.grants[service.serviceId] ?: grant
            }.toPersistentMap(),
            showSecondLayer = inputState.showSecondLayer,
            shouldClose = inputState.shouldClose,
            privacyPolicyUrl = privacyPolicyUrl,
        )
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ConsentState(),
    )

    fun toggleService(serviceId: ConsentServiceId) {
        viewModelScope.launch {
            val currentGrant = state.value
                .consents
                .entries
                .find { it.key.serviceId == serviceId }
                ?.value
                ?: ConsentGrant.UNKNOWN
            val newGrant = currentGrant.toggle()
            consentInputState.update {
                val grants = it.grants.toMutableMap()
                grants[serviceId] = newGrant
                it.copy(grants = grants)
            }
        }
    }

    fun acceptAll() {
        viewModelScope.launch {
            updateAllConsents(ConsentGrant.GRANTED)
            close()
        }
    }

    fun denyAll() {
        viewModelScope.launch {
            updateAllConsents(ConsentGrant.DENIED)
            close()
        }
    }

    fun save() {
        viewModelScope.launch {
            val legal = legalRepository.getLegal().first()
            val updatedConsents = legal.consents.map {
                val newGrant = consentInputState.value.grants[it.serviceId] ?: it.consentGrant
                it.copy(consentGrant = newGrant)
            }
            val legalWithUpdatedConsents = legal.copy(consents = updatedConsents)
            legalRepository.updateLegal(legalWithUpdatedConsents)
            close()
        }
    }

    private fun close() {
        consentInputState.update { it.copy(shouldClose = true) }
    }

    private suspend fun updateAllConsents(consentGrant: ConsentGrant) {
        val legal = legalRepository.getLegal().first()
        val updatedConsents = TrackingService.entries
            .filter { it.serviceCategory != ServiceCategory.REQUIRED }
            .map { service ->
                Consent(service.serviceId, consentGrant)
            }
        val legalWithAll = legal.copy(consents = updatedConsents)
        legalRepository.updateLegal(legalWithAll)
    }

    fun toggleSecondLayer() {
        consentInputState.update {
            it.copy(showSecondLayer = !it.showSecondLayer)
        }
    }
}
