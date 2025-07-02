package io.tigranes.app_two.data.filter

import android.content.Context
import android.graphics.Bitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageColorMatrixFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageContrastFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGaussianBlurFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGrayscaleFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSaturationFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSepiaToneFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSharpenFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageVignetteFilter

@Singleton
class FilterManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val gpuImage = GPUImage(context)

    fun applyFilter(
        bitmap: Bitmap,
        filterType: FilterType,
        intensity: Float = 1.0f
    ): Bitmap {
        gpuImage.setImage(bitmap)

        val filter = when (filterType) {
            FilterType.VINTAGE -> createVintageFilter()
            FilterType.WARM_SUNSET -> createWarmSunsetFilter()
            FilterType.COOL_BREEZE -> createCoolBreezeFilter()
            FilterType.BLACK_WHITE_CLASSIC -> createBlackWhiteFilter()
            FilterType.CINEMATIC_TEAL_ORANGE -> createTealOrangeFilter()
            FilterType.HIGH_CONTRAST_MONO -> createHighContrastMonoFilter()
            FilterType.PASTEL_DREAM -> createPastelDreamFilter()
            FilterType.RETRO_FILM -> createRetroFilmFilter()
            FilterType.HDR_POP -> createHDRPopFilter()
            FilterType.SEPIA_FADE -> createSepiaFilter()
            FilterType.CYBERPUNK_NEON -> createCyberpunkFilter()
            FilterType.SOFT_MATTE -> createSoftMatteFilter()
        }

        gpuImage.setFilter(filter)
        return gpuImage.bitmapWithFilterApplied
    }

    private fun createVintageFilter(): GPUImageFilter {
        val group = GPUImageFilterGroup()
        group.addFilter(GPUImageSepiaToneFilter())
        group.addFilter(GPUImageVignetteFilter())
        return group
    }

    private fun createWarmSunsetFilter(): GPUImageFilter {
        val group = GPUImageFilterGroup()
        val colorMatrix = GPUImageColorMatrixFilter()
        colorMatrix.setColorMatrix(
            floatArrayOf(
                1.2f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.8f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f, 0.0f
            )
        )
        group.addFilter(colorMatrix)
        group.addFilter(GPUImageContrastFilter(1.2f))
        return group
    }

    private fun createCoolBreezeFilter(): GPUImageFilter {
        val group = GPUImageFilterGroup()
        val colorMatrix = GPUImageColorMatrixFilter()
        colorMatrix.setColorMatrix(
            floatArrayOf(
                0.8f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.2f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f, 0.0f
            )
        )
        group.addFilter(colorMatrix)
        group.addFilter(GPUImageBrightnessFilter(0.05f))
        return group
    }

    private fun createBlackWhiteFilter(): GPUImageFilter {
        return GPUImageGrayscaleFilter()
    }

    private fun createTealOrangeFilter(): GPUImageFilter {
        val group = GPUImageFilterGroup()
        val colorMatrix = GPUImageColorMatrixFilter()
        colorMatrix.setColorMatrix(
            floatArrayOf(
                1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.9f, 0.1f, 0.0f, 0.0f,
                0.0f, 0.2f, 0.8f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f, 0.0f
            )
        )
        group.addFilter(colorMatrix)
        group.addFilter(GPUImageContrastFilter(1.3f))
        group.addFilter(GPUImageSaturationFilter(1.2f))
        return group
    }

    private fun createHighContrastMonoFilter(): GPUImageFilter {
        val group = GPUImageFilterGroup()
        group.addFilter(GPUImageGrayscaleFilter())
        group.addFilter(GPUImageContrastFilter(2.0f))
        return group
    }

    private fun createPastelDreamFilter(): GPUImageFilter {
        val group = GPUImageFilterGroup()
        group.addFilter(GPUImageSaturationFilter(0.6f))
        group.addFilter(GPUImageBrightnessFilter(0.1f))
        val blurFilter = GPUImageGaussianBlurFilter()
        blurFilter.setBlurSize(0.5f)
        group.addFilter(blurFilter)
        return group
    }

    private fun createRetroFilmFilter(): GPUImageFilter {
        val group = GPUImageFilterGroup()
        val colorMatrix = GPUImageColorMatrixFilter()
        colorMatrix.setColorMatrix(
            floatArrayOf(
                0.9f, 0.1f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.9f, 0.1f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.9f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f, 0.0f
            )
        )
        group.addFilter(colorMatrix)
        group.addFilter(GPUImageVignetteFilter())
        group.addFilter(GPUImageContrastFilter(1.1f))
        return group
    }

    private fun createHDRPopFilter(): GPUImageFilter {
        val group = GPUImageFilterGroup()
        group.addFilter(GPUImageContrastFilter(1.5f))
        group.addFilter(GPUImageSaturationFilter(1.5f))
        val sharpenFilter = GPUImageSharpenFilter()
        // Set sharpness using the default method if available
        group.addFilter(sharpenFilter)
        return group
    }

    private fun createSepiaFilter(): GPUImageFilter {
        return GPUImageSepiaToneFilter()
    }

    private fun createCyberpunkFilter(): GPUImageFilter {
        val group = GPUImageFilterGroup()
        val colorMatrix = GPUImageColorMatrixFilter()
        colorMatrix.setColorMatrix(
            floatArrayOf(
                1.5f, 0.0f, 0.5f, 0.0f, 0.0f,
                0.0f, 0.8f, 1.2f, 0.0f, 0.0f,
                0.5f, 0.0f, 1.5f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f, 0.0f
            )
        )
        group.addFilter(colorMatrix)
        group.addFilter(GPUImageContrastFilter(1.4f))
        return group
    }

    private fun createSoftMatteFilter(): GPUImageFilter {
        val group = GPUImageFilterGroup()
        group.addFilter(GPUImageSaturationFilter(0.8f))
        group.addFilter(GPUImageContrastFilter(0.9f))
        val blurFilter = GPUImageGaussianBlurFilter()
        blurFilter.setBlurSize(0.3f)
        group.addFilter(blurFilter)
        return group
    }
}

enum class FilterType {
    VINTAGE,
    WARM_SUNSET,
    COOL_BREEZE,
    BLACK_WHITE_CLASSIC,
    CINEMATIC_TEAL_ORANGE,
    HIGH_CONTRAST_MONO,
    PASTEL_DREAM,
    RETRO_FILM,
    HDR_POP,
    SEPIA_FADE,
    CYBERPUNK_NEON,
    SOFT_MATTE
}
