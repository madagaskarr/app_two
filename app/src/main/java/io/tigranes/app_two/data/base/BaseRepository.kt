package io.tigranes.app_two.data.base

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseRepository {

    protected suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): Result<T> {
        return withContext(Dispatchers.IO) {
            try {
                Result.success(apiCall())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    protected suspend fun <T> executeOnIO(
        block: suspend () -> T
    ): T {
        return withContext(Dispatchers.IO) {
            block()
        }
    }
}