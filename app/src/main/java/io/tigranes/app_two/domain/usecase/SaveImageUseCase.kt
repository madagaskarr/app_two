package io.tigranes.app_two.domain.usecase

import android.graphics.Bitmap
import android.net.Uri
import io.tigranes.app_two.data.repository.ImageRepository
import io.tigranes.app_two.domain.base.BaseUseCase
import javax.inject.Inject

class SaveImageUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) : BaseUseCase<SaveImageUseCase.Params, Uri>() {

    override suspend fun execute(params: Params): Uri {
        return imageRepository.saveBitmap(params.bitmap, params.format).getOrThrow()
    }

    data class Params(
        val bitmap: Bitmap,
        val format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
    )
}
