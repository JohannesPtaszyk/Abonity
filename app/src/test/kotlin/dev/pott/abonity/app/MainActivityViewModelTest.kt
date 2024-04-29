package dev.pott.abonity.app

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.pott.abonity.common.test.CoroutinesTestExtension
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
    fun `GIVEN settings flow WHEN observing state THEN main state emits values from settings as state`() {
        runTest {
            val settings = createTestSettings()
            val settingsRepository = FakeSettingsRepository(settings)
            val tested = MainActivityViewModel(settingsRepository)

            tested.state.test {
                runCurrent()
                assertThat(awaitItem()).isEqualTo(MainState.Loading)
                assertThat(awaitItem()).isEqualTo(
                    MainState.Success(
                        settings.theme,
                        settings.enableAdaptiveColors,
                    ),
                )
            }
        }
    }
}
