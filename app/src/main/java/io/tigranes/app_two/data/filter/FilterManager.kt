package io.tigranes.app_two.data.filter

import android.content.Context
import android.graphics.Bitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.*
import javax.inject.Inject
import javax.inject.Singleton

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
        
        // Apply intensity
        if (filter is GPUImageFilterGroup && intensity < 1.0f) {
            filter.filters.forEach { childFilter ->
                when (childFilter) {
                    is GPUImageTwoInputFilter -> childFilter.mix = intensity
                    is GPUImageColorMatrixFilter -> {
                        val matrix = childFilter.colorMatrix
                        for (i in matrix.indices) {
                            if (i % 5 != 4) { // Don't modify the translation component
                                matrix[i] = matrix[i] * intensity + (if (i % 6 == 0) 1f else 0f) * (1f - intensity)
                            }
                        }
                        childFilter.setColorMatrix(matrix)
                    }
                }
            }
        }
        
        gpuImage.setFilter(filter)
        return gpuImage.bitmapWithFilterApplied
    }
    
    private fun createVintageFilter(): GPUImageFilter {
        return GPUImageFilterGroup(listOf(
            GPUImageSepiaFilter().apply { intensity = 0.5f },
            GPUImageVignetteFilter()
        ))
    }
    
    private fun createWarmSunsetFilter(): GPUImageFilter {
        return GPUImageFilterGroup(listOf(
            GPUImageColorMatrixFilter().apply {
                setColorMatrix(floatArrayOf(
                    1.2f, 0.0f, 0.0f, 0.0f, 0.0f,
                    0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
                    0.0f, 0.0f, 0.8f, 0.0f, 0.0f,
                    0.0f, 0.0f, 0.0f, 1.0f, 0.0f
                ))
            },
            GPUImageContrastFilter(1.2f)
        ))
    }
    
    private fun createCoolBreezeFilter(): GPUImageFilter {
        return GPUImageFilterGroup(listOf(
            GPUImageColorMatrixFilter().apply {
                setColorMatrix(floatArrayOf(
                    0.8f, 0.0f, 0.0f, 0.0f, 0.0f,
                    0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
                    0.0f, 0.0f, 1.2f, 0.0f, 0.0f,
                    0.0f, 0.0f, 0.0f, 1.0f, 0.0f
                ))
            },
            GPUImageBrightnessFilter(0.05f)
        ))
    }
    
    private fun createBlackWhiteFilter(): GPUImageFilter {
        return GPUImageGrayscaleFilter()
    }
    
    private fun createTealOrangeFilter(): GPUImageFilter {
        return GPUImageFilterGroup(listOf(
            GPUImageColorMatrixFilter().apply {
                setColorMatrix(floatArrayOf(
                    1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                    0.0f, 0.9f, 0.1f, 0.0f, 0.0f,
                    0.0f, 0.2f, 0.8f, 0.0f, 0.0f,
                    0.0f, 0.0f, 0.0f, 1.0f, 0.0f
                ))
            },
            GPUImageContrastFilter(1.3f),
            GPUImageSaturationFilter(1.2f)
        ))
    }
    
    private fun createHighContrastMonoFilter(): GPUImageFilter {
        return GPUImageFilterGroup(listOf(
            GPUImageGrayscaleFilter(),
            GPUImageContrastFilter(2.0f)
        ))
    }
    
    private fun createPastelDreamFilter(): GPUImageFilter {
        return GPUImageFilterGroup(listOf(
            GPUImageSaturationFilter(0.6f),
            GPUImageBrightnessFilter(0.1f),
            GPUImageGaussianBlurFilter().apply { setBlurSize(0.5f) }
        ))
    }
    
    private fun createRetroFilmFilter(): GPUImageFilter {
        return GPUImageFilterGroup(listOf(
            GPUImageColorMatrixFilter().apply {
                setColorMatrix(floatArrayOf(
                    0.9f, 0.1f, 0.0f, 0.0f, 0.0f,
                    0.0f, 0.9f, 0.1f, 0.0f, 0.0f,
                    0.0f, 0.0f, 0.9f, 0.0f, 0.0f,
                    0.0f, 0.0f, 0.0f, 1.0f, 0.0f
                ))
            },
            GPUImageVignetteFilter(),
            GPUImageContrastFilter(1.1f)
        ))
    }
    
    private fun createHDRPopFilter(): GPUImageFilter {
        return GPUImageFilterGroup(listOf(
            GPUImageContrastFilter(1.5f),
            GPUImageSaturationFilter(1.5f),
            GPUImageSharpenFilter().apply { sharpness = 0.4f }
        ))
    }
    
    private fun createSepiaFilter(): GPUImageFilter {
        return GPUImageSepiaFilter()
    }
    
    private fun createCyberpunkFilter(): GPUImageFilter {
        return GPUImageFilterGroup(listOf(
            GPUImageColorMatrixFilter().apply {
                setColorMatrix(floatArrayOf(
                    1.5f, 0.0f, 0.5f, 0.0f, 0.0f,
                    0.0f, 0.8f, 1.2f, 0.0f, 0.0f,
                    0.5f, 0.0f, 1.5f, 0.0f, 0.0f,
                    0.0f, 0.0f, 0.0f, 1.0f, 0.0f
                ))
            },
            GPUImageContrastFilter(1.4f)
        ))
    }
    
    private fun createSoftMatteFilter(): GPUImageFilter {
        return GPUImageFilterGroup(listOf(
            GPUImageSaturationFilter(0.8f),
            GPUImageContrastFilter(0.9f),
            GPUImageGaussianBlurFilter().apply { setBlurSize(0.3f) }
        ))
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