package dev.pott.abonity.core.domain.config

import dev.pott.abonity.core.entity.config.Config
import kotlinx.coroutines.flow.Flow

interface ConfigRepository {

    fun getConfig(): Flow<Config>

    suspend fun refresh(): Result<Unit>
}
