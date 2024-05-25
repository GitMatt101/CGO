package com.example.cgo.data.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.cgo.data.database.entities.Participation
import kotlinx.coroutines.flow.Flow

@Dao
interface ParticipationDAO {
    @Query("SELECT * FROM participation ORDER BY userId ASC")
    fun getAll(): Flow<List<Participation>>

    @Upsert
    suspend fun upsert(participation: Participation)

    @Delete
    suspend fun delete(participation: Participation)
}