package dev.pott.abonity.core.test.config

import dev.pott.abonity.core.domain.config.ConfigRepository
import dev.pott.abonity.core.entity.config.Config
import dev.pott.abonity.core.test.config.entities.createTestConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeConfigRepository(
    initialValue: Config = createTestConfig(),
    private val refreshResult: Result<Unit> = Result.success(Unit),
) : ConfigRepository {

    private val configFlow = MutableStateFlow(initialValue)

    override fun getConfig(): Flow<Config> = configFlow

    override suspend fun refresh(): Result<Unit> = refreshResult
}
