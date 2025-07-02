package io.tigranes.app_two.domain.usecase

import android.graphics.Bitmap
import io.tigranes.app_two.data.filter.FilterManager
import io.tigranes.app_two.data.filter.FilterType
import io.tigranes.app_two.domain.base.BaseUseCase
import javax.inject.Inject

class ProcessImageUseCase @Inject constructor(
    private val filterManager: FilterManager
) : BaseUseCase<ProcessImageUseCase.Params, Bitmap>() {

    override suspend fun execute(params: Params): Bitmap {
        return filterManager.applyFilter(params.bitmap, params.filterType)
    }

    data class Params(
        val bitmap: Bitmap,
        val filterType: FilterType
    )
}