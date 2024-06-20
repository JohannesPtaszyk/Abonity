package dev.pott.abonity.core.test.legal

import dev.pott.abonity.core.domain.legal.LegalRepository
import dev.pott.abonity.core.entity.legal.Legal
import dev.pott.abonity.core.test.legal.entities.createTestLegal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeLegalRepository(initialValue: Legal = createTestLegal()) : LegalRepository {

    val legalFlow = MutableStateFlow(initialValue)

    override fun getLegal(): Flow<Legal> = legalFlow

    override suspend fun updateLegal(legal: Legal) {
        legalFlow.value = legal
    }
}
