package com.example.cgo.data.repositories

import com.example.cgo.data.database.entities.User
import com.example.cgo.data.database.daos.UserDAO
import com.example.cgo.data.database.entities.UserWithEvents
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class UsersRepository(
    private val userDAO: UserDAO
) {
    val users: Flow<List<User>> = userDAO.getAll()
    val usersWithEvents: Flow<List<UserWithEvents>> = userDAO.getUsersWithEvents()

    suspend fun upsert(user: User) = userDAO.upsert(user)
    suspend fun delete(user: User) = userDAO.delete(user)
    suspend fun getUserOnLogin(email: String, password: String) : User? {
        return try {
            users.first().first { it.email == email && it.password == password }
        } catch (exception: NoSuchElementException) {
            null
        }
    }
    suspend fun getUserInfo(userId: Int) : User? {
        return try {
            users.first().first { it.userId == userId }
        } catch (exception: NoSuchElementException) {
            null
        }
    }
    suspend fun getUsersWithEvents() : List<UserWithEvents> = usersWithEvents.first()
    suspend fun getUserWithEventsById(userId: Int) : UserWithEvents? {
        return try {
            usersWithEvents.first().first { it.user.userId == userId }
        } catch (exception: NoSuchElementException) {
            null
        }
    }
}