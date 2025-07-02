package io.tigranes.app_two.domain.usecase

import android.graphics.Bitmap
import io.tigranes.app_two.data.filter.FilterType
import io.tigranes.app_two.data.repository.FilterRepository
import io.tigranes.app_two.domain.base.BaseUseCase
import javax.inject.Inject

class ApplyFilterUseCase @Inject constructor(
    private val filterRepository: FilterRepository
) : BaseUseCase<ApplyFilterUseCase.Params, Bitmap>() {

    override suspend fun execute(params: Params): Bitmap {
        return filterRepository.applyFilter(
            params.bitmap,
            params.filterType,
            params.intensity
        ).getOrThrow()
    }

    data class Params(
        val bitmap: Bitmap,
        val filterType: FilterType,
        val intensity: Float
    )
}
