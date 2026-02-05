package com.stopdemarchage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopdemarchage.data.model.Prefix
import com.stopdemarchage.data.repository.CallRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrefixViewModel @Inject constructor(
    private val repository: CallRepository
) : ViewModel() {

    val prefixes: StateFlow<List<Prefix>> = repository.getAllPrefixes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val enabledPrefixCount: StateFlow<Int> = repository.getEnabledPrefixCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    private val _editingPrefix = MutableStateFlow<Prefix?>(null)
    val editingPrefix: StateFlow<Prefix?> = _editingPrefix.asStateFlow()

    fun addPrefix(prefix: String, description: String) {
        viewModelScope.launch {
            if (!repository.prefixExists(prefix)) {
                repository.insertPrefix(
                    Prefix(
                        prefix = prefix,
                        description = description,
                        isEnabled = true
                    )
                )
            }
        }
    }

    fun updatePrefix(prefix: Prefix) {
        viewModelScope.launch {
            repository.updatePrefix(prefix)
        }
    }

    fun deletePrefix(prefix: Prefix) {
        viewModelScope.launch {
            repository.deletePrefix(prefix)
        }
    }

    fun togglePrefixEnabled(prefix: Prefix) {
        viewModelScope.launch {
            repository.updatePrefix(prefix.copy(isEnabled = !prefix.isEnabled))
        }
    }

    fun setEditingPrefix(prefix: Prefix?) {
        _editingPrefix.value = prefix
    }

    fun loadDefaultPrefixes() {
        viewModelScope.launch {
            CallRepository.DEFAULT_PREFIXES.forEach { defaultPrefix ->
                if (!repository.prefixExists(defaultPrefix.prefix)) {
                    repository.insertPrefix(defaultPrefix)
                }
            }
        }
    }

    fun deleteAllPrefixes() {
        viewModelScope.launch {
            repository.deleteAllPrefixes()
        }
    }
}
