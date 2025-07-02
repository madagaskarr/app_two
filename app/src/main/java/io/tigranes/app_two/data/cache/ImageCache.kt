package io.tigranes.app_two.data.cache

import android.graphics.Bitmap
import android.util.LruCache
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageCache @Inject constructor() {

    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val cacheSize = maxMemory / 4 // Use 1/4 of available memory for cache

    private val bitmapCache = object : LruCache<String, Bitmap>(cacheSize) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            // Return the size of the bitmap in KB
            return bitmap.byteCount / 1024
        }
    }

    fun get(key: String): Bitmap? {
        return bitmapCache.get(key)
    }

    fun put(key: String, bitmap: Bitmap) {
        bitmapCache.put(key, bitmap)
    }

    fun remove(key: String) {
        bitmapCache.remove(key)
    }

    fun clear() {
        bitmapCache.evictAll()
    }
}
