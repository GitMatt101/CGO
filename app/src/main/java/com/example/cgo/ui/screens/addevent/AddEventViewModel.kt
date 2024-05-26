package com.example.cgo.ui.screens.addevent

import androidx.lifecycle.ViewModel
import com.example.cgo.data.database.entities.Event
import com.example.cgo.data.database.entities.PrivacyType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AddEventState(
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val time: String = "",
    val location: String = "",
    val maxParticipants: Int = 0,
    val privacyType: PrivacyType,
) {
    val canSubmit
        get() = title.isNotBlank()
                && date.isNotBlank()
                && time.isNotBlank()
                && location.isNotBlank()
                && maxParticipants > 0
                && privacyType != PrivacyType.NONE

    fun toEvent(eventCreatorId: Int) = Event(
        title = title,
        description = description,
        date = date,
        time = time,
        location = location,
        maxParticipants = maxParticipants,
        privacyType = privacyType,
        eventCreatorId = eventCreatorId,
        winnerId = null
    )
}

interface AddEventActions {
    fun setTitle(title: String)
    fun setDescription(description: String)
    fun setDate(date: String)
    fun setTime(time: String)
    fun setLocation(location: String)
    fun setMaxParticipants(maxParticipants: Int)
    fun setPrivacyType(privacyType: PrivacyType)
}

class AddEventViewModel : ViewModel() {
    private val _state = MutableStateFlow(AddEventState(privacyType = PrivacyType.NONE))
    val state = _state.asStateFlow()

    val actions = object : AddEventActions {
        override fun setTitle(title: String) =
            _state.update { it.copy(title = title) }

        override fun setDescription(description: String) =
            _state.update { it.copy(description = description) }

        override fun setDate(date: String) =
            _state.update { it.copy(date = date) }

        override fun setTime(time: String) =
            _state.update { it.copy(time = time) }

        override fun setLocation(location: String) =
            _state.update { it.copy(location = location) }

        override fun setMaxParticipants(maxParticipants: Int) =
            _state.update { it.copy(maxParticipants = maxParticipants) }

        override fun setPrivacyType(privacyType: PrivacyType) =
            _state.update { it.copy(privacyType = privacyType) }
    }
}