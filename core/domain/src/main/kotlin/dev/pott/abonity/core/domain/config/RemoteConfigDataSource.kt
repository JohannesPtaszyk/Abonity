package dev.pott.abonity.core.domain.config

interface RemoteConfigDataSource {

    suspend fun refresh(): Result<Unit>
}
