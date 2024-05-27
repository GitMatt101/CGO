package com.example.cgo.ui.screens.addevent

import androidx.lifecycle.ViewModel
import com.example.cgo.data.database.entities.Event
import com.example.cgo.data.database.entities.PrivacyType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import okhttp3.Address

data class AddEventState(
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val time: String = "",
    val address: String = "",
    val city: String = "",
    val maxParticipants: Int = 0,
    val privacyType: PrivacyType,
) {
    val canSubmit
        get() = title.isNotBlank()
                && date.isNotBlank()
                && time.isNotBlank()
                && address.isNotBlank()
                && city.isNotBlank()
                && maxParticipants > 0
                && privacyType != PrivacyType.NONE

    fun toEvent(eventCreatorId: Int) = Event(
        title = title,
        description = description,
        date = date,
        time = time,
        address = address,
        city = city,
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
    fun setAddress(address: String)
    fun setCity(city: String)
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

        override fun setAddress(address: String) =
            _state.update { it.copy(address = address) }

        override fun setCity(city: String) =
            _state.update { it.copy(city = city) }

        override fun setMaxParticipants(maxParticipants: Int) =
            _state.update { it.copy(maxParticipants = maxParticipants) }

        override fun setPrivacyType(privacyType: PrivacyType) =
            _state.update { it.copy(privacyType = privacyType) }
    }
}