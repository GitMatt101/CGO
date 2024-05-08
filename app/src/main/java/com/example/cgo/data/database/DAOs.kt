package com.example.cgo.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
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

@Dao
interface UserDAO {
    @Query("SELECT * FROM user ORDER BY username ASC")
    fun getAll(): Flow<List<User>>

    @Upsert
    suspend fun upsert(user: User)

    @Delete
    suspend fun delete(item: User)

    @Transaction
    @Query("SELECT * FROM user")
    suspend fun getUserWithEvents(): List<UserWithEvents>
}

