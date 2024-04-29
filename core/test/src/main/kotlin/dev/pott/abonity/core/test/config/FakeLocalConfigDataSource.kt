package dev.pott.abonity.core.test.config

import dev.pott.abonity.core.domain.config.LocalConfigDataSource
import dev.pott.abonity.core.entity.config.Config
import kotlinx.coroutines.flow.Flow

class FakeLocalConfigDataSource(
    private val flow: Flow<Config>,
) : LocalConfigDataSource {
    override fun getConfiguration(): Flow<Config> {
        return flow
    }
}
