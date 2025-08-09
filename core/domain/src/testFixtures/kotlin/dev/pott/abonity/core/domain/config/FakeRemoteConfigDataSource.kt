package dev.pott.abonity.core.domain.config

class FakeRemoteConfigDataSource(private val refreshResult: Result<Unit>) :
    RemoteConfigDataSource {
    override suspend fun refresh(): Result<Unit> = refreshResult
}
