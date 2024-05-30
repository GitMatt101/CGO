package com.example.cgo.ui.controllers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cgo.data.repositories.AppRepository
import com.example.cgo.ui.theme.Theme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AppState(
    val userId: Int?,
    val theme: Theme
)

class AppViewModel(
    private val repository: AppRepository
) : ViewModel() {
    val state = repository.userId.combine(repository.theme) { userId: Int?, theme: Theme ->
        Pair(userId, theme)
    }.map { values: Pair<Int?, Theme> ->
        val userId = values.first
        val theme = values.second
        AppState(userId = userId, theme = theme)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = AppState(userId = null, theme = Theme.System)
    )

    fun changeTheme(theme: Theme) = viewModelScope.launch { repository.setTheme(theme) }
    fun changeUserId(userId: Int?) = viewModelScope.launch { repository.setUserId(userId) }
}