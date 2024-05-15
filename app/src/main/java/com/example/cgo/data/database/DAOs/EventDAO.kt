package com.example.cgo.data.database.DAOs

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.cgo.data.database.entities.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDAO {
    @Query("SELECT * FROM event ORDER BY title ASC")
    fun getAll(): Flow<List<Event>>

    @Upsert
    suspend fun upsert(event: Event)

    @Delete
    suspend fun delete(item: Event)
}
