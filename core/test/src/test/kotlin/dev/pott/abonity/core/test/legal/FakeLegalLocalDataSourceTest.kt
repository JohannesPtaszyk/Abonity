package dev.pott.abonity.core.test.legal

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.pott.abonity.core.test.legal.entities.createTestLegal
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class FakeLegalLocalDataSourceTest {

    @Test
    fun `GIVEN a local legal data source WHEN getLegal is called THEN the legal is returned`() {
        runTest {
            val legal = createTestLegal()
            val dataSource = FakeLegalLocalDataSource(legal)

            dataSource.getLegal().test {
                assertThat(awaitItem()).isEqualTo(legal)
            }
        }
    }
}
