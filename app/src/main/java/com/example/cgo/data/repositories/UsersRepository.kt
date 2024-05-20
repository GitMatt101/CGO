package com.example.cgo.data.repositories

import android.content.ContentResolver
import com.example.cgo.data.database.entities.User
import com.example.cgo.data.database.daos.UserDAO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.toList

class UsersRepository(
    private val userDAO: UserDAO
) {
    val users: Flow<List<User>> = userDAO.getAll()

    suspend fun upsert(user: User) = userDAO.upsert(user)

    suspend fun delete(user: User) = userDAO.delete(user)

    suspend fun checkLogin(email: String, password: String) : Boolean = users.first().filter { it.email == email && it.password == password }.size == 1

    suspend fun getUserInfo(email: String) : User = users.first().first { it.email == email }

    suspend fun getUserWithEvents() = userDAO.getUserWithEvents()
}