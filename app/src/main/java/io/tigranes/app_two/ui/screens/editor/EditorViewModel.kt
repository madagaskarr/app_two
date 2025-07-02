package io.tigranes.app_two.ui.screens.editor

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.tigranes.app_two.data.filter.FilterType
import io.tigranes.app_two.data.preferences.PreferencesManager
import io.tigranes.app_two.data.preferences.RecentEdit
import io.tigranes.app_two.domain.usecase.ApplyFilterUseCase
import io.tigranes.app_two.domain.usecase.LoadImageUseCase
import io.tigranes.app_two.domain.usecase.SaveImageUseCase
import io.tigranes.app_two.ui.base.BaseViewModel
import io.tigranes.app_two.util.Constants
import io.tigranes.app_two.util.FileUtils
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class EditorViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val savedStateHandle: SavedStateHandle,
    private val loadImageUseCase: LoadImageUseCase,
    private val saveImageUseCase: SaveImageUseCase,
    private val applyFilterUseCase: ApplyFilterUseCase,
    private val preferencesManager: PreferencesManager
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    init {
        loadInitialImage()
    }

    private fun loadInitialImage() {
        val imageUriString = savedStateHandle.get<String>("imageUri")
        imageUriString?.let { uriString ->
            val uri = Uri.parse(uriString)
            loadImage(uri)
        }
    }

    private fun loadImage(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            loadImageUseCase(LoadImageUseCase.Params(uri)).fold(
                onSuccess = { bitmap ->
                    _uiState.value = _uiState.value.copy(
                        originalBitmap = bitmap,
                        currentBitmap = bitmap,
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
            )
        }
    }

    fun applyFilter(filterIndex: Int) {
        val originalBitmap = _uiState.value.originalBitmap ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                selectedFilterIndex = filterIndex,
                isApplyingFilter = true
            )

            try {
                val filterType = FilterType.values()[filterIndex]
                val intensity = _uiState.value.filterIntensity

                applyFilterUseCase(
                    ApplyFilterUseCase.Params(
                        bitmap = originalBitmap,
                        filterType = filterType,
                        intensity = intensity
                    )
                ).fold(
                    onSuccess = { filteredBitmap ->
                        _uiState.value = _uiState.value.copy(
                            currentBitmap = filteredBitmap,
                            isApplyingFilter = false
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isApplyingFilter = false,
                            error = error.message
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isApplyingFilter = false,
                    error = e.message
                )
            }
        }
    }

    fun updateFilterIntensity(intensity: Float) {
        _uiState.value = _uiState.value.copy(filterIntensity = intensity)

        // Reapply filter with new intensity
        val currentFilter = _uiState.value.selectedFilterIndex
        if (currentFilter >= 0) {
            applyFilter(currentFilter)
        }
    }

    fun resetToOriginal() {
        _uiState.value = _uiState.value.copy(
            currentBitmap = _uiState.value.originalBitmap,
            selectedFilterIndex = -1,
            filterIntensity = Constants.DEFAULT_FILTER_INTENSITY.toFloat()
        )
    }

    fun saveImage() {
        viewModelScope.launch {
            val bitmap = _uiState.value.currentBitmap ?: return@launch

            _uiState.value = _uiState.value.copy(isSaving = true)

            saveImageUseCase(SaveImageUseCase.Params(bitmap)).fold(
                onSuccess = { uri ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        savedImageUri = uri
                    )
                    
                    // Save to recent edits
                    val currentFilter = _uiState.value.selectedFilterIndex
                    if (currentFilter >= 0) {
                        val filterType = FilterType.values()[currentFilter]
                        val filterName = getFilterDisplayName(filterType)
                        preferencesManager.saveRecentEdit(
                            RecentEdit(
                                imageUri = uri.toString(),
                                filterId = currentFilter,
                                filterName = filterName
                            )
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = error.message
                    )
                }
            )
        }
    }

    suspend fun prepareImageForSharing(): Uri? = withContext(Dispatchers.IO) {
        try {
            val bitmap = _uiState.value.currentBitmap ?: return@withContext null
            
            // Create a temporary file for sharing
            val shareDir = File(context.cacheDir, "shared_images")
            shareDir.mkdirs()
            
            val shareFile = File(shareDir, "shared_image_${System.currentTimeMillis()}.jpg")
            
            FileOutputStream(shareFile).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, Constants.IMAGE_COMPRESS_QUALITY, outputStream)
            }
            
            // Get URI using FileProvider
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                shareFile
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(error = "Failed to prepare image for sharing")
            null
        }
    }
    
    private fun getFilterDisplayName(filterType: FilterType): String = when (filterType) {
        FilterType.VINTAGE -> "Vintage"
        FilterType.WARM_SUNSET -> "Warm Sunset"
        FilterType.COOL_BREEZE -> "Cool Breeze"
        FilterType.BLACK_WHITE_CLASSIC -> "B&W Classic"
        FilterType.CINEMATIC_TEAL_ORANGE -> "Cinematic"
        FilterType.HIGH_CONTRAST_MONO -> "High Contrast"
        FilterType.PASTEL_DREAM -> "Pastel Dream"
        FilterType.RETRO_FILM -> "Retro Film"
        FilterType.HDR_POP -> "HDR Pop"
        FilterType.SEPIA_FADE -> "Sepia Fade"
        FilterType.CYBERPUNK_NEON -> "Cyberpunk"
        FilterType.SOFT_MATTE -> "Soft Matte"
    }
}

data class EditorUiState(
    val originalBitmap: Bitmap? = null,
    val currentBitmap: Bitmap? = null,
    val selectedFilterIndex: Int = -1,
    val filterIntensity: Float = Constants.DEFAULT_FILTER_INTENSITY.toFloat(),
    val isLoading: Boolean = false,
    val isApplyingFilter: Boolean = false,
    val isSaving: Boolean = false,
    val savedImageUri: Uri? = null,
    val error: String? = null
)
