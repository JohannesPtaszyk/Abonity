package dev.pott.abonity.feature.settings.main

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.pott.abonity.common.test.CoroutinesTestExtension
import dev.pott.abonity.core.entity.settings.Theme
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.test.config.FakeConfigRepository
import dev.pott.abonity.core.test.config.entities.TEST_IMPRINT_URL
import dev.pott.abonity.core.test.config.entities.TEST_PRIVACY_POLICY_URL
import dev.pott.abonity.core.test.settings.FakeSettingsRepository
import dev.pott.abonity.core.test.settings.entities.createTestSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutinesTestExtension::class)
class SettingsViewModelTest {

    @Test
    fun `GIVEN default settings WHEN setTheme to light THEN settings value is updated`() =
        runTest {
            val settings = createTestSettings()
            val settingsRepository = FakeSettingsRepository(settings)
            val configRepository = FakeConfigRepository()

            val tested = SettingsViewModel(settingsRepository, configRepository)

            tested.state.test {
                runCurrent()
                tested.setTheme(Theme.LIGHT)
                runCurrent()

                assertThat(awaitItem()).isEqualTo(SettingsState())
                assertThat(awaitItem()).isEqualTo(
                    SettingsState(
                        settings,
                        TEST_PRIVACY_POLICY_URL,
                        TEST_IMPRINT_URL,
                    ),
                )
                assertThat(awaitItem()).isEqualTo(
                    SettingsState(
                        settings.copy(theme = Theme.LIGHT),
                        TEST_PRIVACY_POLICY_URL,
                        TEST_IMPRINT_URL,
                    ),
                )
            }
        }

    @Test
    fun `GIVEN default settings WHEN setPeriod to years THEN settings value is updated`() {
        runTest {
            val settings = createTestSettings()
            val settingsRepository = FakeSettingsRepository(settings)
            val configRepository = FakeConfigRepository()

            val tested = SettingsViewModel(settingsRepository, configRepository)

            tested.state.test {
                runCurrent()
                tested.setPeriod(PaymentPeriod.YEARS)
                runCurrent()

                assertThat(awaitItem()).isEqualTo(SettingsState())
                assertThat(awaitItem()).isEqualTo(
                    SettingsState(
                        settings,
                        TEST_PRIVACY_POLICY_URL,
                        TEST_IMPRINT_URL,
                    ),
                )
                assertThat(awaitItem()).isEqualTo(
                    SettingsState(
                        settings.copy(period = PaymentPeriod.YEARS),
                        TEST_PRIVACY_POLICY_URL,
                        TEST_IMPRINT_URL,
                    ),
                )
            }
        }
    }

    @Test
    fun `GIVEN default settings WHEN enableAdaptiveColors THEN settings value is updated`() =
        runTest {
            val settings = createTestSettings()
            val settingsRepository = FakeSettingsRepository(settings)
            val configRepository = FakeConfigRepository()

            val tested = SettingsViewModel(settingsRepository, configRepository)

            tested.state.test {
                runCurrent()
                tested.enableAdaptiveColors(true)
                runCurrent()

                assertThat(awaitItem()).isEqualTo(SettingsState())
                assertThat(awaitItem()).isEqualTo(
                    SettingsState(
                        settings,
                        TEST_PRIVACY_POLICY_URL,
                        TEST_IMPRINT_URL,
                    ),
                )
                assertThat(awaitItem()).isEqualTo(
                    SettingsState(
                        settings.copy(enableAdaptiveColors = true),
                        TEST_PRIVACY_POLICY_URL,
                        TEST_IMPRINT_URL,
                    ),
                )
            }
        }
}
