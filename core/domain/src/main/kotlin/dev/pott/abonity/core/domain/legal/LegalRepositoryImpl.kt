package dev.pott.abonity.core.domain.legal

import dev.pott.abonity.core.entity.legal.Legal
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LegalRepositoryImpl @Inject constructor(
    private val legalLocalDataSource: LegalLocalDataSource,
) : LegalRepository {
    override fun getLegal(): Flow<Legal> {
        return legalLocalDataSource.getLegal()
    }

    override suspend fun updateLegal(legal: Legal) {
        legalLocalDataSource.updateLegal(legal)
    }
}
