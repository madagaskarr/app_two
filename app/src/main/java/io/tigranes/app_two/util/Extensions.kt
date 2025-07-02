package io.tigranes.app_two.util

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun <T> Flow<T>.onStartLoading(action: () -> Unit): Flow<T> = onStart { action() }

fun <T> Flow<T>.onError(action: (Throwable) -> Unit): Flow<T> = catch { action(it) }