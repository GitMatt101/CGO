package com.example.cgo.data.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.cgo.data.database.entities.Event
import com.example.cgo.data.database.entities.EventWithUsers
import com.example.cgo.data.database.entities.UserWithEvents
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDAO {
    @Query("SELECT * FROM event ORDER BY title ASC")
    fun getAll(): Flow<List<Event>>

    @Upsert
    suspend fun upsert(event: Event)

    @Delete
    suspend fun delete(item: Event)

    @Transaction
    @Query("SELECT * FROM event")
    suspend fun getEventWithUsers(): List<EventWithUsers>
}
