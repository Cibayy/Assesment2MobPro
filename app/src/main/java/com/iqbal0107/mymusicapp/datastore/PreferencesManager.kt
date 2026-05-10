package com.iqbal0107.mymusicapp.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {

    companion object {
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        val THEME_COLOR_KEY = stringPreferencesKey("theme_color")
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data
        .map { it[DARK_MODE_KEY] ?: false }

    val themeColor: Flow<String> = context.dataStore.data
        .map { it[THEME_COLOR_KEY] ?: "Purple" }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[DARK_MODE_KEY] = enabled }
    }

    suspend fun setThemeColor(color: String) {
        context.dataStore.edit { it[THEME_COLOR_KEY] = color }
    }
}