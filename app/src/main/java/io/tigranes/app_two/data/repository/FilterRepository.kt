package io.tigranes.app_two.data.repository

import android.graphics.Bitmap
import io.tigranes.app_two.data.base.BaseRepository
import io.tigranes.app_two.data.filter.FilterManager
import io.tigranes.app_two.data.filter.FilterType
import io.tigranes.app_two.di.IoDispatcher
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface FilterRepository {
    suspend fun applyFilter(
        bitmap: Bitmap,
        filterType: FilterType,
        intensity: Float
    ): Result<Bitmap>
}

class FilterRepositoryImpl @Inject constructor(
    private val filterManager: FilterManager,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseRepository(), FilterRepository {

    override suspend fun applyFilter(
        bitmap: Bitmap,
        filterType: FilterType,
        intensity: Float
    ): Result<Bitmap> = withContext(ioDispatcher) {
        try {
            val filteredBitmap = filterManager.applyFilter(bitmap, filterType, intensity / 100f)
            Result.success(filteredBitmap)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
