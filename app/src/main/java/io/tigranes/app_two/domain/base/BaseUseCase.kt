package io.tigranes.app_two.domain.base

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseUseCase<in P, R> {

    protected open val dispatcher: CoroutineDispatcher = Dispatchers.Default

    suspend operator fun invoke(params: P): Result<R> {
        return withContext(dispatcher) {
            try {
                Result.success(execute(params))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    protected abstract suspend fun execute(params: P): R
}

abstract class NoParamsUseCase<R> : BaseUseCase<Unit, R>() {
    suspend operator fun invoke(): Result<R> = invoke(Unit)
}