package dev.pott.abonity.core.local.legal

import androidx.datastore.core.DataStore
import dev.pott.abonity.core.domain.legal.LegalLocalDataSource
import dev.pott.abonity.core.entity.legal.Legal
import dev.pott.abonity.core.local.legal.datastore.entities.LegalEntity
import dev.pott.abonity.core.local.legal.datastore.mapper.toDomain
import dev.pott.abonity.core.local.legal.datastore.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LegalDataStoreDataSource @Inject constructor(private val dataStore: DataStore<LegalEntity>) :
    LegalLocalDataSource {
    override fun getLegal(): Flow<Legal> = dataStore.data.map { it.toDomain() }

    override suspend fun updateLegal(legal: Legal) {
        dataStore.updateData { legal.toEntity() }
    }
}
