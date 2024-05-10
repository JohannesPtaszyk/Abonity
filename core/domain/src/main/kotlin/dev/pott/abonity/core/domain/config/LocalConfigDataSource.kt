package dev.pott.abonity.core.domain.config

import dev.pott.abonity.core.entity.config.Config
import kotlinx.coroutines.flow.Flow

interface LocalConfigDataSource {

    fun getConfiguration(): Flow<Config>
}
