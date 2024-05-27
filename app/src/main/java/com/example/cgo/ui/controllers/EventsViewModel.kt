package com.example.cgo.ui.controllers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cgo.data.database.entities.Event
import com.example.cgo.data.database.entities.EventWithUsers
import com.example.cgo.data.repositories.EventsRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class EventsState(val events: List<Event>)

class EventsViewModel(
    private val repository: EventsRepository
) : ViewModel() {
    val state = repository.events.map { EventsState(events = it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = EventsState(emptyList())
    )

    fun addEvent(event: Event) = viewModelScope.launch { repository.upsert(event) }
    fun deleteEvent(event: Event) = viewModelScope.launch { repository.delete(event) }
    fun getEventWithUsersById(eventId: Int) : Deferred<EventWithUsers?> = viewModelScope.async { repository.getEventWithUsersById(eventId) }
}
