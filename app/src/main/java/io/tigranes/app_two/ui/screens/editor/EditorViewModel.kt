package io.tigranes.app_two.ui.screens.editor

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.tigranes.app_two.data.filter.FilterType
import io.tigranes.app_two.domain.usecase.ApplyFilterUseCase
import io.tigranes.app_two.domain.usecase.LoadImageUseCase
import io.tigranes.app_two.domain.usecase.SaveImageUseCase
import io.tigranes.app_two.ui.base.BaseViewModel
import io.tigranes.app_two.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val loadImageUseCase: LoadImageUseCase,
    private val saveImageUseCase: SaveImageUseCase,
    private val applyFilterUseCase: ApplyFilterUseCase
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