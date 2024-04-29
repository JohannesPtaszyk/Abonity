package dev.pott.abonity.core.test.config

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class FakeRemoteConfigDataSourceTest {

    @Test
    fun `GIVEN a remote config data source WHEN refresh is called THEN the result is returned`() =
        runTest {
            val dataSource = FakeRemoteConfigDataSource(Result.success(Unit))

            val result = dataSource.refresh()

            assertThat(result).isEqualTo(Result.success(Unit))
        }
}
