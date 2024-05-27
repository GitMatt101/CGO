package com.example.cgo.data.repositories

import com.example.cgo.data.database.entities.Event
import com.example.cgo.data.database.daos.EventDAO
import com.example.cgo.data.database.entities.EventWithUsers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class EventsRepository(
    private val eventDAO: EventDAO
) {
    val events: Flow<List<Event>> = eventDAO.getAll()
    val eventsWithUsers: Flow<List<EventWithUsers>> = eventDAO.getEventsWithUsers()

    suspend fun upsert(event: Event) = eventDAO.upsert(event)
    suspend fun delete(event: Event) = eventDAO.delete(event)
    suspend fun getEventsWithUsers() : List<EventWithUsers> = eventsWithUsers.first()
    suspend fun getEventWithUsersById(eventId: Int) : EventWithUsers? = eventsWithUsers.first().find { it.event.eventId == eventId }
}