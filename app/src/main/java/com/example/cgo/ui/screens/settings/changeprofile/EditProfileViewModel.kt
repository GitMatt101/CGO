package com.example.cgo.ui.screens.settings.changeprofile

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class EditProfileState (
    val username: String = "",
    val profilePicture: Uri = Uri.EMPTY
) {
    val canSubmit = username.isNotBlank()
}

interface EditProfileActions {
    fun setUsername(username: String)
    fun setProfilePicture(imageUri: Uri)
}

class EditProfileViewModel : ViewModel() {
    private val _state = MutableStateFlow(EditProfileState())
    val state = _state.asStateFlow()

    val actions = object : EditProfileActions {
        override fun setUsername(username: String) = _state.update { editor: EditProfileState -> editor.copy(username = username) }
        override fun setProfilePicture(imageUri: Uri) = _state.update { editor: EditProfileState -> editor.copy(profilePicture = imageUri) }
    }
}