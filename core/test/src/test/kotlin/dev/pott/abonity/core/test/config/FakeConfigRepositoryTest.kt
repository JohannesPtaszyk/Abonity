package dev.pott.abonity.core.test.config

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.pott.abonity.core.test.config.entities.createTestConfig
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class FakeConfigRepositoryTest {

    @Test
    fun `GIVEN a config repository WHEN getConfiguration is called THEN the config is returned`() {
        runTest {
            val config = createTestConfig()
            val repository = FakeConfigRepository(config)

            val result = repository.getConfig()

            result.test {
                assertThat(awaitItem()).isEqualTo(config)
            }
        }
    }

    @Test
    fun `GIVEN a config repository WHEN refresh is called THEN the result is returned`() =
        runTest {
            val repository = FakeConfigRepository()

            val result = repository.refresh()

            assertThat(result).isEqualTo(Result.success(Unit))
        }
}
