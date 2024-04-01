package dev.pott.abonity.app

import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.perf.performance
import dev.pott.abonity.core.domain.legal.LegalRepository
import dev.pott.abonity.core.entity.legal.Consent
import dev.pott.abonity.core.entity.legal.ConsentServiceId
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class TrackingServiceManager @Inject constructor(private val legalRepository: LegalRepository) {

    private var job: Job? = null

    @OptIn(DelicateCoroutinesApi::class)
    fun init() {
        job?.cancel()
        job = GlobalScope.launch {
            legalRepository.getLegal().collect { applyConsents(it.consents) }
        }
    }

    private fun applyConsents(consents: List<Consent>) {
        consents.map { consent ->
            when (consent.serviceId) {
                ConsentServiceId.FIREBASE_ANALYTICS -> {
                    Firebase.analytics.setAnalyticsCollectionEnabled(
                        consent.consentGrant.isGranted,
                    )
                }

                ConsentServiceId.FIREBASE_CRASHLYTICS -> {
                    Firebase.crashlytics.setCrashlyticsCollectionEnabled(
                        consent.consentGrant.isGranted,
                    )
                }

                ConsentServiceId.FIREBASE_PERFORMANCE -> {
                    Firebase.performance.isPerformanceCollectionEnabled =
                        consent.consentGrant.isGranted
                }

                else -> {
                    // Do nothing
                }
            }
        }
    }
}
