package dev.pott.abonity.core.test.config

import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.pott.abonity.core.test.config.entities.createTestConfig
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.Test

class FakeLocalConfigDataSourceTest {

    @Test
    fun `GIVEN a local config data source WHEN getConfiguration is called THEN the config is returned`() {
        val configFlow = flowOf(createTestConfig())
        val dataSource = FakeLocalConfigDataSource(configFlow)

        val result = dataSource.getConfiguration()

        assertThat(result).isEqualTo(configFlow)
    }
}
