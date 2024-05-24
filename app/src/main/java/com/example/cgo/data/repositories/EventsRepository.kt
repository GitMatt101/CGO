package com.example.cgo.data.repositories

import com.example.cgo.data.database.entities.Event
import com.example.cgo.data.database.daos.EventDAO
import com.example.cgo.data.database.entities.EventWithUsers
import kotlinx.coroutines.flow.Flow

class EventsRepository(
    private val eventDAO: EventDAO
) {
    val events: Flow<List<Event>> = eventDAO.getAll()

    suspend fun upsert(event: Event) = eventDAO.upsert(event)

    suspend fun delete(event: Event) = eventDAO.delete(event)

    suspend fun getEventWithUsers() : List<EventWithUsers> = eventDAO.getEventWithUsers()
}