package dev.pott.abonity.core.test.legal

import dev.pott.abonity.core.domain.legal.LegalLocalDataSource
import dev.pott.abonity.core.entity.legal.Legal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeLegalLocalDataSource(initialValue: Legal) : LegalLocalDataSource {

    private val legalFlow = MutableStateFlow(initialValue)

    override fun getLegal(): Flow<Legal> {
        return legalFlow
    }

    override suspend fun updateLegal(legal: Legal) {
        legalFlow.value = legal
    }
}
