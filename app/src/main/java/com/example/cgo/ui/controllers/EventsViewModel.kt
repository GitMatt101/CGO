package com.example.cgo.ui.controllers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cgo.data.database.entities.Event
import com.example.cgo.data.database.entities.EventWithUsers
import com.example.cgo.data.repositories.EventsRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class EventsState(
    val events: List<Event>,
    val eventsWithUsers: List<EventWithUsers>
)

class EventsViewModel(
    private val repository: EventsRepository
) : ViewModel() {
    val state = repository.events.combine(repository.eventsWithUsers) { event: List<Event>, eventWithUsers: List<EventWithUsers> ->
        Pair(event, eventWithUsers)
    }.map { values: Pair<List<Event>, List<EventWithUsers>> ->
        EventsState(events = values.first, eventsWithUsers = values.second)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = EventsState(emptyList(), emptyList())
    )

    fun addEvent(event: Event) = viewModelScope.launch { repository.upsert(event) }
    fun updateEvent(event: Event) = viewModelScope.launch { repository.upsert(event) }
    fun deleteEvent(event: Event) = viewModelScope.launch { repository.delete(event) }
    fun getEventWithUsersById(eventId: Int) : Deferred<EventWithUsers?> = viewModelScope.async { repository.getEventWithUsersById(eventId) }
}
