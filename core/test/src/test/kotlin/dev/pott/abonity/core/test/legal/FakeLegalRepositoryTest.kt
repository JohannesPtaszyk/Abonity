package dev.pott.abonity.core.test.legal

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.pott.abonity.core.test.legal.entities.createTestLegal
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class FakeLegalRepositoryTest {

    @Test
    fun `GIVEN a legal repository WHEN getLegal is called THEN the legal is returned`() =
        runTest {
            val legal = createTestLegal()
            val repository = FakeLegalRepository(legal)

            repository.getLegal().test {
                assertThat(awaitItem()).isEqualTo(legal)
            }
        }

    @Test
    fun `GIVEN a legal repository WHEN refresh is called THEN the result is returned`() =
        runTest {
            val legal = createTestLegal()
            val repository = FakeLegalRepository(legal)

            repository.updateLegal(legal.copy(consents = emptyList()))

            repository.getLegal().test {
                assertThat(awaitItem().consents).isEqualTo(emptyList())
            }
        }
}
