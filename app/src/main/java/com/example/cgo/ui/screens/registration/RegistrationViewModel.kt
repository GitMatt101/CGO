package com.example.cgo.ui.screens.registration

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.cgo.data.database.entities.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RegistrationState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val profilePicture: Uri = Uri.EMPTY
) {
    val canSubmit get() = username.isNotBlank()
            && email.isNotBlank()
            && password.isNotBlank()
            && confirmPassword.isNotBlank()
            && confirmPassword == password

    fun createUser() = User(
        username = username,
        email = email,
        password = password,
        profilePicture = profilePicture.toString(),
        gamesWon = 0,
        participantId = 0
    )
}

interface RegistrationActions {
    fun setUsername(username: String)
    fun setEmail(email: String)
    fun setPassword(password: String)
    fun setConfirmPassword(password: String)
    fun setProfilePicture(imageUri: Uri)
}

class RegistrationViewModel : ViewModel() {
    private val _state = MutableStateFlow(RegistrationState())
    val state = _state.asStateFlow()

    val actions = object : RegistrationActions {
        override fun setUsername(username: String) = _state.update { it.copy(username = username) }

        override fun setEmail(email: String) = _state.update { it.copy(email = email) }

        override fun setPassword(password: String) = _state.update { it.copy(password = password) }

        override fun setConfirmPassword(password: String) = _state.update { it.copy(confirmPassword = password) }

        override fun setProfilePicture(imageUri: Uri) = _state.update { it.copy(profilePicture = imageUri) }
    }
}