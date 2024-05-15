package com.example.cgo.data.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.cgo.data.database.entities.User
import com.example.cgo.data.database.entities.UserWithEvents
import kotlinx.coroutines.flow.Flow

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