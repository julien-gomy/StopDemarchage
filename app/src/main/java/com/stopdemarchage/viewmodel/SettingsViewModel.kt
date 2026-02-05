package com.stopdemarchage.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopdemarchage.data.PreferencesManager
import com.stopdemarchage.data.model.Prefix
import com.stopdemarchage.data.repository.CallRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val repository: CallRepository
) : ViewModel() {

    val isBlockingEnabled: StateFlow<Boolean> = preferencesManager.isBlockingEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    val isNotificationsEnabled: StateFlow<Boolean> = preferencesManager.isNotificationsEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val isAutoCleanupEnabled: StateFlow<Boolean> = preferencesManager.isAutoCleanupEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    val isFirstLaunch: StateFlow<Boolean> = preferencesManager.isFirstLaunch
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    fun setBlockingEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setBlockingEnabled(enabled)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setNotificationsEnabled(enabled)
        }
    }

    fun setAutoCleanupEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setAutoCleanupEnabled(enabled)
        }
    }

    fun setFirstLaunchCompleted() {
        viewModelScope.launch {
            preferencesManager.setFirstLaunchCompleted()
        }
    }

    fun exportPrefixes(context: Context, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val prefixes = repository.getAllPrefixes().first()
                val json = Json.encodeToString(prefixes)
                val file = File(
                    context.getExternalFilesDir(null),
                    "stopdemarchage_prefixes.json"
                )
                file.writeText(json)
                onResult(true, file.absolutePath)
            } catch (e: Exception) {
                onResult(false, e.message)
            }
        }
    }

    fun importPrefixes(jsonContent: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val prefixes = Json.decodeFromString<List<Prefix>>(jsonContent)
                prefixes.forEach { prefix ->
                    if (!repository.prefixExists(prefix.prefix)) {
                        repository.insertPrefix(prefix.copy(id = 0))
                    }
                }
                onResult(true, "${prefixes.size} préfixes importés")
            } catch (e: Exception) {
                onResult(false, e.message)
            }
        }
    }
}
