package io.tigranes.app_two.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "photo_filter_prefs")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val gson = Gson()
    
    companion object {
        val RECENT_EDITS_KEY = stringPreferencesKey("recent_edits")
        val FAVORITE_FILTER_KEY = intPreferencesKey("favorite_filter")
        val MAX_RECENT_EDITS = 10
    }
    
    suspend fun saveRecentEdit(recentEdit: RecentEdit) {
        context.dataStore.edit { preferences ->
            val currentEdits = getRecentEditsFromPrefs(preferences).toMutableList()
            
            // Remove if already exists (to move to top)
            currentEdits.removeAll { it.imageUri == recentEdit.imageUri }
            
            // Add to beginning
            currentEdits.add(0, recentEdit)
            
            // Keep only last MAX_RECENT_EDITS
            val editsToSave = currentEdits.take(MAX_RECENT_EDITS)
            
            preferences[RECENT_EDITS_KEY] = gson.toJson(editsToSave)
        }
    }
    
    fun getRecentEdits(): Flow<List<RecentEdit>> = context.dataStore.data.map { preferences ->
        getRecentEditsFromPrefs(preferences)
    }
    
    private fun getRecentEditsFromPrefs(preferences: Preferences): List<RecentEdit> {
        val json = preferences[RECENT_EDITS_KEY] ?: return emptyList()
        return try {
            val type = object : TypeToken<List<RecentEdit>>() {}.type
            gson.fromJson<List<RecentEdit>>(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun saveFavoriteFilter(filterIndex: Int) {
        context.dataStore.edit { preferences ->
            preferences[FAVORITE_FILTER_KEY] = filterIndex
        }
    }
    
    fun getFavoriteFilter(): Flow<Int?> = context.dataStore.data.map { preferences ->
        preferences[FAVORITE_FILTER_KEY]
    }
    
    suspend fun clearRecentEdits() {
        context.dataStore.edit { preferences ->
            preferences.remove(RECENT_EDITS_KEY)
        }
    }
}

data class RecentEdit(
    val imageUri: String,
    val filterId: Int,
    val filterName: String,
    val editedAt: Long = System.currentTimeMillis()
)