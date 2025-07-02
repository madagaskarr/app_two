package io.tigranes.app_two.ui.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import io.tigranes.app_two.data.filter.FilterType
import io.tigranes.app_two.ui.navigation.PhotoFilterScreen
import io.tigranes.app_two.ui.screens.editor.EditorViewModel
import io.tigranes.app_two.util.showToast
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    navController: NavController,
    imageUri: Uri?,
    viewModel: EditorViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showSavedMessage by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.savedImageUri) {
        uiState.savedImageUri?.let {
            showSavedMessage = true
            delay(2000)
            navController.popBackStack(PhotoFilterScreen.Home.route, false)
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            context.showToast(it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Photo") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (uiState.currentBitmap != null && !uiState.isSaving) {
                FloatingActionButton(
                    onClick = { viewModel.saveImage() },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Save, contentDescription = "Save")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Image preview
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                    uiState.currentBitmap != null -> {
                        Image(
                            bitmap = uiState.currentBitmap!!.asImageBitmap(),
                            contentDescription = "Edited image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                // Processing overlay
                if (uiState.isApplyingFilter) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Applying filter...",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // Saving overlay
                if (uiState.isSaving || showSavedMessage) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.7f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            if (uiState.isSaving) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Saving image...",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            } else if (showSavedMessage) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Saved",
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(32.dp)
                                        )
                                        .padding(16.dp),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Image saved successfully!",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Filter intensity slider
            if (uiState.selectedFilterIndex >= 0) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Filter Intensity",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${(uiState.filterIntensity).toInt()}%",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Slider(
                        value = uiState.filterIntensity,
                        onValueChange = { intensity ->
                            viewModel.updateFilterIntensity(intensity)
                        },
                        valueRange = 0f..100f,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isApplyingFilter
                    )
                }
            }

            // Filter selection with thumbnails
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Spacer(modifier = Modifier.width(16.dp))

                    // Original
                    FilterThumbnailItem(
                        name = "Original",
                        thumbnail = uiState.originalBitmap,
                        isSelected = uiState.selectedFilterIndex == -1,
                        isEnabled = !uiState.isApplyingFilter && uiState.originalBitmap != null,
                        onClick = {
                            viewModel.resetToOriginal()
                        }
                    )

                    // Filters with thumbnails
                    FilterType.values().forEachIndexed { index, filterType ->
                        FilterThumbnailItem(
                            name = filterType.toDisplayName(),
                            thumbnail = uiState.originalBitmap,
                            filterIndex = index,
                            isSelected = uiState.selectedFilterIndex == index,
                            isEnabled = !uiState.isApplyingFilter && uiState.originalBitmap != null,
                            onClick = {
                                viewModel.applyFilter(index)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))
                }
            }
        }
    }
}

@Composable
private fun FilterThumbnailItem(
    name: String,
    thumbnail: android.graphics.Bitmap?,
    filterIndex: Int = -1,
    isSelected: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = isEnabled) { onClick() }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(8.dp))
                .then(
                    if (isSelected) {
                        Modifier.border(
                            width = 3.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp)
                        )
                    } else {
                        Modifier
                    }
                )
        ) {
            if (thumbnail != null) {
                Image(
                    bitmap = thumbnail.asImageBitmap(),
                    contentDescription = name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                        .then(
                            if (!isEnabled) {
                                Modifier.background(Color.Black.copy(alpha = 0.5f))
                            } else {
                                Modifier
                            }
                        ),
                    contentScale = ContentScale.Crop
                )
                
                // Filter preview overlay (simplified for performance)
                if (filterIndex >= 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                when (filterIndex) {
                                    0 -> Color(0x30FFD700) // Vintage - gold tint
                                    1 -> Color(0x30FF6B35) // Warm Sunset - orange tint
                                    2 -> Color(0x304FC3F7) // Cool Breeze - blue tint
                                    3 -> Color(0x80000000) // B&W - gray overlay
                                    4 -> Color(0x20FF6B35) // Cinematic - slight orange
                                    5 -> Color(0x40000000) // High Contrast - dark overlay
                                    6 -> Color(0x20FFB6C1) // Pastel - pink tint
                                    7 -> Color(0x30964B00) // Retro - brown tint
                                    8 -> Color(0x00000000) // HDR - no tint
                                    9 -> Color(0x30D2691E) // Sepia - brown tint
                                    10 -> Color(0x30FF00FF) // Cyberpunk - magenta tint
                                    11 -> Color(0x20000000) // Soft Matte - light dark overlay
                                    else -> Color.Transparent
                                }
                            )
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

private fun FilterType.toDisplayName(): String = when (this) {
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