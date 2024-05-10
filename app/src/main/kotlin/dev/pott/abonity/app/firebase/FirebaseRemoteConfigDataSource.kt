package dev.pott.abonity.app.firebase

import co.touchlab.kermit.Logger
import com.google.firebase.perf.trace
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import dev.pott.abonity.core.domain.config.LocalConfigDataSource
import dev.pott.abonity.core.domain.config.RemoteConfigDataSource
import dev.pott.abonity.core.entity.config.Config
import dev.pott.abonity.core.entity.config.LegalConfig
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val FIREBASE_CONFIG_TRACE = "fetch_and_active_firebase_config"

@Singleton
class FirebaseRemoteConfigDataSource @Inject constructor(
    private val firebaseRemoteConfig: FirebaseRemoteConfig,
) : RemoteConfigDataSource, LocalConfigDataSource {

    private val logger = Logger.withTag(this::class.simpleName.orEmpty())

    private val configFlow by lazy {
        callbackFlow {
            trySend(firebaseRemoteConfig.toDomain())
            val listener = object : ConfigUpdateListener {

                override fun onUpdate(configUpdate: ConfigUpdate) {
                    logger.d { "Firebase Remote Config updated: ${configUpdate.updatedKeys}" }

                    firebaseRemoteConfig.activate()
                        .addOnSuccessListener { trySend(firebaseRemoteConfig.toDomain()) }
                        .addOnFailureListener {
                            logger.e(it) {
                                "Could not activate Firebase Remote Config after update"
                            }
                        }
                }

                override fun onError(error: FirebaseRemoteConfigException) {
                    logger.e(error) {
                        "Error while listening to Firebase Remote Config in real time"
                    }
                }
            }
            val registration = firebaseRemoteConfig.addOnConfigUpdateListener(listener)
            awaitClose { registration.remove() }
        }
    }

    override fun getConfiguration(): Flow<Config> {
        return configFlow
    }

    override suspend fun refresh(): Result<Unit> {
        val result = runCatching {
            trace(FIREBASE_CONFIG_TRACE) { firebaseRemoteConfig.fetchAndActivate().await() }
        }
        return result.map { /* Map to Unit */ }
    }

    private fun FirebaseRemoteConfig.toDomain(): Config {
        val legalConfig = LegalConfig(
            privacyPolicyUrl = getString(FirebaseConfig.LegalConfig.PRIVACY_POLICY_URL),
            imprintUrl = getString(FirebaseConfig.LegalConfig.IMPRINT_URL),
        )
        return Config(legalConfig)
    }
}
