package com.example.cgo.ui.screens.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginState(
    val email: String = "",
    val password: String = ""
) {
    val canSubmit get() = email.isNotBlank() && password.isNotBlank()
}

interface LoginActions {
    fun setEmail(email: String)
    fun setPassword(password: String)
}

class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    val actions = object : LoginActions {
        override fun setEmail(email: String) = _state.update { it.copy(email = email) }
        override fun setPassword(password: String) = _state.update { it.copy(password = password) }
    }
}