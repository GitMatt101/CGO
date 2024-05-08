package com.example.cgo.data.repositories

import com.example.cgo.data.database.Event
import com.example.cgo.data.database.EventDAO
import com.example.cgo.data.database.UserDAO
import kotlinx.coroutines.flow.Flow

class EventsRepository(
    private val eventDAO: EventDAO,
) {
    val events: Flow<List<Event>> = eventDAO.getAll()

    suspend fun upsert(event: Event) = eventDAO.upsert(event)

    suspend fun delete(event: Event) = eventDAO.delete(event)
}