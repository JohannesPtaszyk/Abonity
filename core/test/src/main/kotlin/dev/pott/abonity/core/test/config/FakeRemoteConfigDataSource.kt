package dev.pott.abonity.core.test.config

import dev.pott.abonity.core.domain.config.RemoteConfigDataSource

class FakeRemoteConfigDataSource(
    private val refreshResult: Result<Unit>,
) : RemoteConfigDataSource {
    override suspend fun refresh(): Result<Unit> {
        return refreshResult
    }
}
