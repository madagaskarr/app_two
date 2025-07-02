package io.tigranes.app_two.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import io.tigranes.app_two.data.base.BaseRepository
import io.tigranes.app_two.di.IoDispatcher
import javax.inject.Inject
import kotlin.math.max
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface ImageRepository {
    suspend fun loadBitmap(uri: Uri): Result<Bitmap>
    suspend fun loadBitmapWithMaxSize(uri: Uri, maxSize: Int): Result<Bitmap>
    suspend fun saveBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat): Result<Uri>
}

class ImageRepositoryImpl @Inject constructor(
    private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseRepository(), ImageRepository {

    override suspend fun loadBitmap(uri: Uri): Result<Bitmap> = withContext(ioDispatcher) {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)
                    ?: throw Exception("Failed to decode image")
                Result.success(bitmap)
            } ?: Result.failure(Exception("Failed to open input stream"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loadBitmapWithMaxSize(
        uri: Uri,
        maxSize: Int
    ): Result<Bitmap> = withContext(ioDispatcher) {
        try {
            // First, get image dimensions without loading the full image
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }

            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream, null, options)
            }

            // Calculate sample size to reduce memory usage
            val sampleSize = calculateSampleSize(options.outWidth, options.outHeight, maxSize)

            // Load the bitmap with the calculated sample size
            val scaledOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inJustDecodeBounds = false
            }

            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream, null, scaledOptions)
                    ?: throw Exception("Failed to decode image")
                Result.success(bitmap)
            } ?: Result.failure(Exception("Failed to open input stream"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveBitmap(
        bitmap: Bitmap,
        format: Bitmap.CompressFormat
    ): Result<Uri> = withContext(ioDispatcher) {
        try {
            val isPng = format == Bitmap.CompressFormat.PNG
            val savedUri = io.tigranes.app_two.util.FileUtils.saveImageToGallery(
                context = context,
                bitmap = bitmap,
                isPng = isPng
            )

            savedUri?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Failed to save image"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun calculateSampleSize(width: Int, height: Int, maxSize: Int): Int {
        var sampleSize = 1
        val maxDimension = max(width, height)

        while (maxDimension / sampleSize > maxSize) {
            sampleSize *= 2
        }

        return sampleSize
    }
}
