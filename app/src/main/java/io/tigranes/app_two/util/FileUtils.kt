package io.tigranes.app_two.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileUtils {
    
    private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    private const val JPEG_EXTENSION = ".jpg"
    private const val PNG_EXTENSION = ".png"
    
    fun createImageFile(context: Context, isPng: Boolean = false): File {
        val timeStamp = SimpleDateFormat(FILENAME_FORMAT, Locale.getDefault()).format(Date())
        val extension = if (isPng) PNG_EXTENSION else JPEG_EXTENSION
        val fileName = "IMG_$timeStamp$extension"
        
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            Constants.APP_FOLDER_NAME
        ).apply {
            if (!exists()) {
                mkdirs()
            }
        }
        
        return File(storageDir, fileName)
    }
    
    fun saveImageToGallery(
        context: Context,
        bitmap: Bitmap,
        isPng: Boolean = false
    ): Uri? {
        val fileName = "IMG_${System.currentTimeMillis()}"
        val mimeType = if (isPng) "image/png" else "image/jpeg"
        
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveImageToMediaStore(context, bitmap, fileName, mimeType, isPng)
        } else {
            saveImageToExternalStorage(context, bitmap, isPng)
        }
    }
    
    private fun saveImageToMediaStore(
        context: Context,
        bitmap: Bitmap,
        fileName: String,
        mimeType: String,
        isPng: Boolean
    ): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/${Constants.APP_FOLDER_NAME}")
        }
        
        val uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
        
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { outputStream ->
                val format = if (isPng) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG
                bitmap.compress(format, Constants.IMAGE_COMPRESS_QUALITY, outputStream)
            }
        }
        
        return uri
    }
    
    private fun saveImageToExternalStorage(
        context: Context,
        bitmap: Bitmap,
        isPng: Boolean
    ): Uri? {
        val file = createImageFile(context, isPng)
        
        return try {
            FileOutputStream(file).use { outputStream ->
                val format = if (isPng) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG
                bitmap.compress(format, Constants.IMAGE_COMPRESS_QUALITY, outputStream)
            }
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun getFileProviderUri(context: Context, file: File): Uri {
        return androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
}