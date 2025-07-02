package io.tigranes.app_two.ui.screens.camera

import android.content.Context
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.tigranes.app_two.ui.base.BaseViewModel
import io.tigranes.app_two.util.FileUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    private val cameraExecutor = Executors.newSingleThreadExecutor()
    var imageCapture: ImageCapture? = null

    fun toggleCamera() {
        _uiState.value = _uiState.value.copy(
            lensFacing = if (_uiState.value.lensFacing == CameraSelector.LENS_FACING_BACK) {
                CameraSelector.LENS_FACING_FRONT
            } else {
                CameraSelector.LENS_FACING_BACK
            }
        )
    }

    fun toggleFlash() {
        _uiState.value = _uiState.value.copy(
            flashMode = when (_uiState.value.flashMode) {
                ImageCapture.FLASH_MODE_OFF -> ImageCapture.FLASH_MODE_ON
                ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_AUTO
                else -> ImageCapture.FLASH_MODE_OFF
            }
        )
        imageCapture?.flashMode = _uiState.value.flashMode
    }

    fun capturePhoto(onPhotoSaved: (Uri) -> Unit) {
        _uiState.value = _uiState.value.copy(isCapturing = true)

        val photoFile = FileUtils.createImageFile(context)
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture?.takePicture(
            outputFileOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    viewModelScope.launch {
                        try {
                            val savedUri = Uri.fromFile(photoFile)
                            _uiState.value = _uiState.value.copy(
                                isCapturing = false,
                                lastCapturedUri = savedUri
                            )
                            onPhotoSaved(savedUri)
                        } catch (e: Exception) {
                            _uiState.value = _uiState.value.copy(
                                isCapturing = false,
                                error = e.message
                            )
                        }
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    _uiState.value = _uiState.value.copy(
                        isCapturing = false,
                        error = exception.message
                    )
                }
            }
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    override fun onCleared() {
        super.onCleared()
        cameraExecutor.shutdown()
    }
}

data class CameraUiState(
    val isCapturing: Boolean = false,
    val lensFacing: Int = CameraSelector.LENS_FACING_BACK,
    val flashMode: Int = ImageCapture.FLASH_MODE_OFF,
    val lastCapturedUri: Uri? = null,
    val error: String? = null
)