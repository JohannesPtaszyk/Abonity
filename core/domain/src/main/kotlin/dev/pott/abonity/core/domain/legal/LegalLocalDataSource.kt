package dev.pott.abonity.core.domain.legal

import dev.pott.abonity.core.entity.legal.Legal
import kotlinx.coroutines.flow.Flow

interface LegalLocalDataSource {
    fun getLegal(): Flow<Legal>

    suspend fun updateLegal(legal: Legal)
}
