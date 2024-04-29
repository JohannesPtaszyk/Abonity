package dev.pott.abonity.feature.legal.consent

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import dev.pott.abonity.common.test.CoroutinesTestExtension
import dev.pott.abonity.core.domain.legal.usecase.GetTrackingServicesWithGrantUseCase
import dev.pott.abonity.core.entity.legal.ConsentGrant
import dev.pott.abonity.core.test.config.FakeConfigRepository
import dev.pott.abonity.core.test.legal.FakeLegalRepository
import dev.pott.abonity.core.test.legal.entities.createTestLegal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutinesTestExtension::class)
class ConsentViewModelTest {

    @Test
    fun `GIVEN only unknown consents WHEN toggleService THEN should update grant to granted`() {
        runTest {
            val legal = createTestLegal()
            val legalRepository = FakeLegalRepository()
            val configRepository = FakeConfigRepository()
            val getTrackingServicesWithGrantUseCase =
                GetTrackingServicesWithGrantUseCase(legalRepository)

            val viewModel = ConsentViewModel(
                legalRepository,
                configRepository,
                getTrackingServicesWithGrantUseCase,
            )

            viewModel.state.test {
                val serviceId = legal.consents.first().serviceId
                viewModel.toggleService(serviceId)

                assertThat(awaitItem()).isEqualTo(ConsentState())
                assertThat(
                    awaitItem().consents.entries.first { it.key.serviceId == serviceId }.value,
                ).isEqualTo(ConsentGrant.GRANTED)
            }
        }
    }

    @Test
    fun `GIVEN only unknown consents WHEN toggleService twice THEN should update grant to denied`() {
        runTest {
            val legal = createTestLegal()
            val legalRepository = FakeLegalRepository()
            val configRepository = FakeConfigRepository()
            val getTrackingServicesWithGrantUseCase =
                GetTrackingServicesWithGrantUseCase(legalRepository)

            val viewModel = ConsentViewModel(
                legalRepository,
                configRepository,
                getTrackingServicesWithGrantUseCase,
            )

            viewModel.state.test {
                val serviceId = legal.consents.first().serviceId
                viewModel.toggleService(serviceId)
                runCurrent()
                viewModel.toggleService(serviceId)
                runCurrent()

                skipItems(2) // Initial state and granted state
                assertThat(
                    awaitItem().consents.entries.first { it.key.serviceId == serviceId }.value,
                ).isEqualTo(ConsentGrant.DENIED)
            }
        }
    }

    @Test
    fun `GIVEN only unknown consents WHEN acccept all THEN should update all grants to granted`() {
        runTest {
            val legal = createTestLegal()
            val legalRepository = FakeLegalRepository()
            val configRepository = FakeConfigRepository()
            val getTrackingServicesWithGrantUseCase =
                GetTrackingServicesWithGrantUseCase(legalRepository)

            val viewModel = ConsentViewModel(
                legalRepository,
                configRepository,
                getTrackingServicesWithGrantUseCase,
            )

            viewModel.state.test {
                viewModel.acceptAll()
                runCurrent()

                skipItems(1) // Initial state
                assertThat(awaitItem().shouldClose).isTrue()
                assertThat(legalRepository.legalFlow.value).isEqualTo(
                    legal.copy(
                        consents = legal.consents.map {
                            it.copy(
                                consentGrant = ConsentGrant.GRANTED,
                            )
                        },
                    ),
                )
            }
        }
    }

    @Test
    fun `GIVEN only unknown consents WHEN deny all THEN should update all grants to denied`() {
        runTest {
            val legal = createTestLegal()
            val legalRepository = FakeLegalRepository(legal)
            val configRepository = FakeConfigRepository()
            val getTrackingServicesWithGrantUseCase =
                GetTrackingServicesWithGrantUseCase(legalRepository)

            val viewModel = ConsentViewModel(
                legalRepository,
                configRepository,
                getTrackingServicesWithGrantUseCase,
            )

            viewModel.state.test {
                viewModel.denyAll()
                runCurrent()

                skipItems(1) // Initial state
                assertThat(awaitItem().shouldClose).isTrue()
                assertThat(legalRepository.legalFlow.value).isEqualTo(
                    legal.copy(
                        consents = legal.consents.map {
                            it.copy(
                                consentGrant = ConsentGrant.DENIED,
                            )
                        },
                    ),
                )
            }
        }
    }

    @Test
    fun `GIVEN only unknown consents WHEN toggle one AND save THEN should update all grants to current state`() {
        runTest {
            val legal = createTestLegal()
            val legalRepository = FakeLegalRepository(legal)
            val configRepository = FakeConfigRepository()
            val getTrackingServicesWithGrantUseCase =
                GetTrackingServicesWithGrantUseCase(legalRepository)

            val viewModel = ConsentViewModel(
                legalRepository,
                configRepository,
                getTrackingServicesWithGrantUseCase,
            )

            viewModel.state.test {
                viewModel.toggleService(legal.consents.first().serviceId)
                runCurrent()
                viewModel.save()
                runCurrent()

                skipItems(2) // Initial state && Toggled State
                assertThat(awaitItem().shouldClose).isTrue()
                assertThat(legalRepository.legalFlow.value).isEqualTo(
                    legal.copy(
                        consents = legal.consents.mapIndexed { i, consent ->
                            if (i == 0) {
                                consent.copy(consentGrant = ConsentGrant.GRANTED)
                            } else {
                                consent
                            }
                        },
                    ),
                )
            }
        }
    }
}
