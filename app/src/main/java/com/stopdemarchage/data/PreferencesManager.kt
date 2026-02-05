package com.stopdemarchage.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val KEY_BLOCKING_ENABLED = booleanPreferencesKey("blocking_enabled")
        private val KEY_NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val KEY_AUTO_CLEANUP_ENABLED = booleanPreferencesKey("auto_cleanup_enabled")
        private val KEY_FIRST_LAUNCH = booleanPreferencesKey("first_launch")
    }

    val isBlockingEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_BLOCKING_ENABLED] ?: true
    }

    val isNotificationsEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_NOTIFICATIONS_ENABLED] ?: false
    }

    val isAutoCleanupEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_AUTO_CLEANUP_ENABLED] ?: true
    }

    val isFirstLaunch: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_FIRST_LAUNCH] ?: true
    }

    suspend fun setBlockingEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_BLOCKING_ENABLED] = enabled
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setAutoCleanupEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_AUTO_CLEANUP_ENABLED] = enabled
        }
    }

    suspend fun setFirstLaunchCompleted() {
        dataStore.edit { preferences ->
            preferences[KEY_FIRST_LAUNCH] = false
        }
    }
}
