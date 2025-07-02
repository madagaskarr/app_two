package io.tigranes.app_two.domain.usecase

import android.graphics.Bitmap
import android.net.Uri
import io.tigranes.app_two.data.repository.ImageRepository
import io.tigranes.app_two.domain.base.BaseUseCase
import io.tigranes.app_two.util.Constants
import javax.inject.Inject

class LoadImageUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) : BaseUseCase<LoadImageUseCase.Params, Bitmap>() {

    override suspend fun execute(params: Params): Bitmap {
        val result = if (params.maxSize != null) {
            imageRepository.loadBitmapWithMaxSize(params.uri, params.maxSize)
        } else {
            imageRepository.loadBitmap(params.uri)
        }
        
        return result.getOrThrow()
    }

    data class Params(
        val uri: Uri,
        val maxSize: Int? = Constants.PREVIEW_IMAGE_MAX_SIZE
    )
}