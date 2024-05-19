package com.example.cgo.ui.controllers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cgo.data.database.entities.User
import com.example.cgo.data.repositories.UsersRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class UsersState(val users: List<User>)

class UsersViewModel(private val repository: UsersRepository) : ViewModel() {
    val state = repository.users.map { UsersState(users = it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = UsersState(emptyList())
    )

    fun addUser(user: User) = viewModelScope.launch { repository.upsert(user) }

    fun updateUser(user: User) = viewModelScope.launch { repository.upsert(user) }

    fun deleteUser(user: User) = viewModelScope.launch { repository.delete(user) }

    fun checkUserExists(email: String, password: String) : Deferred<Boolean> = viewModelScope.async { repository.checkUserExists(email, password) }
}