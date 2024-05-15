package com.example.cgo.data.repositories

import com.example.cgo.data.database.entities.User
import com.example.cgo.data.database.DAOs.UserDAO
import kotlinx.coroutines.flow.Flow

class UsersRepository(
    private val userDAO: UserDAO
) {
    val users: Flow<List<User>> = userDAO.getAll()

    suspend fun upsert(user: User) = userDAO.upsert(user)

    suspend fun delete(user: User) = userDAO.delete(user)

    suspend fun getUserWithEvents() = userDAO.getUserWithEvents()
}