package dev.pott.abonity.core.domain.config

import dev.pott.abonity.core.entity.config.Config
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConfigRepositoryImpl @Inject constructor(
    private val remoteConfigDataSource: RemoteConfigDataSource,
    private val localConfigDataSource: LocalConfigDataSource,
) : ConfigRepository {
    override fun getConfig(): Flow<Config> {
        return localConfigDataSource.getConfiguration()
    }

    override suspend fun refresh(): Result<Unit> {
        return remoteConfigDataSource.refresh()
    }
}
