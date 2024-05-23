package com.example.cgo.data.repositories

import android.net.Uri
import com.example.cgo.data.database.entities.User
import com.example.cgo.data.database.daos.UserDAO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class UsersRepository(
    private val userDAO: UserDAO
) {
    val users: Flow<List<User>> = userDAO.getAll()

    suspend fun upsert(user: User) = userDAO.upsert(user)
    suspend fun delete(user: User) = userDAO.delete(user)
    suspend fun getUserOnLogin(email: String, password: String) : User {
        return try {
            users.first().first { it.email == email && it.password == password }
        } catch (exception: NoSuchElementException) {
            User(userId = -1, username = "NONE", email = "", password = "", profilePicture = Uri.EMPTY.toString(), gamesWon = 0, participantId = -1)
        }
    }
    suspend fun getUserInfo(userId: Int) : User = users.first().first { it.userId == userId }
    suspend fun getUserWithEvents() = userDAO.getUserWithEvents()
}