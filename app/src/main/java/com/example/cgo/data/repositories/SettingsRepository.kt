package com.example.cgo.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.cgo.ui.theme.Theme
import kotlinx.coroutines.flow.map

class SettingsRepository (
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val THEME_KEY = stringPreferencesKey("theme")
    }

    val theme = dataStore.data.map { preferences ->
        try {
            Theme.valueOf(preferences[THEME_KEY] ?: "System")
        } catch (_: IllegalArgumentException) {
            Theme.System
        }
    }

    suspend fun setTheme(theme: Theme) {
        dataStore.edit { preferences -> preferences[THEME_KEY] = theme.toString() }
    }
}