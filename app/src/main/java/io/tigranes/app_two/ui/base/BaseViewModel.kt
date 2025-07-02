package io.tigranes.app_two.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        handleError(throwable)
    }

    protected open fun handleError(throwable: Throwable) {
        throwable.printStackTrace()
    }

    protected fun launchSafely(
        onError: ((Throwable) -> Unit)? = null,
        block: suspend CoroutineScope.() -> Unit
    ) {
        viewModelScope.launch(
            context = exceptionHandler,
            block = {
                try {
                    block()
                } catch (e: Exception) {
                    onError?.invoke(e) ?: handleError(e)
                }
            }
        )
    }
}