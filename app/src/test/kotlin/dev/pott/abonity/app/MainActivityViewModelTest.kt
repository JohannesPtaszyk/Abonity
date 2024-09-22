package dev.pott.abonity.app

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.pott.abonity.common.test.CoroutinesTestExtension
import dev.pott.abonity.core.domain.legal.usecase.ShouldShowTrackingConsentUseCase
import dev.pott.abonity.core.test.legal.FakeLegalRepository
import dev.pott.abonity.core.test.settings.FakeSettingsRepository
import dev.pott.abonity.core.test.settings.entities.createTestSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutinesTestExtension::class)
class MainActivityViewModelTest {

    @Test
    fun `GIVEN settings flow and legal flow WHEN observing state THEN main state emits values from settings as state`() {
        runTest {
            val settings = createTestSettings()
            val settingsRepository = FakeSettingsRepository(settings)
            val legalRepository = FakeLegalRepository()
            val shouldShowTrackingConsentUseCase = ShouldShowTrackingConsentUseCase(legalRepository)

            val tested = MainActivityViewModel(
                settingsRepository,
                shouldShowTrackingConsentUseCase,
            )

            tested.state.test {
                runCurrent()
                assertThat(awaitItem()).isEqualTo(MainState.Loading)
                assertThat(awaitItem()).isEqualTo(
                    MainState.Loaded(
                        theme = settings.theme,
                        adaptiveColorsEnabled = settings.enableAdaptiveColors,
                        showConsent = true,
                    ),
                )
            }
        }
    }

    @Test
    fun `GIVEN consent is shown WHEN closeConsent THEN consent is not shown anymore`() {
        runTest {
            val settings = createTestSettings()
            val settingsRepository = FakeSettingsRepository(settings)
            val legalRepository = FakeLegalRepository()
            val shouldShowTrackingConsentUseCase = ShouldShowTrackingConsentUseCase(legalRepository)

            val tested = MainActivityViewModel(
                settingsRepository,
                shouldShowTrackingConsentUseCase,
            )

            tested.state.test {
                runCurrent()
                skipItems(2) // Initial & First state

                tested.closeConsent()

                assertThat(awaitItem()).isEqualTo(
                    MainState.Loaded(
                        theme = settings.theme,
                        adaptiveColorsEnabled = settings.enableAdaptiveColors,
                        showConsent = false,
                    ),
                )
            }
        }
    }
}
