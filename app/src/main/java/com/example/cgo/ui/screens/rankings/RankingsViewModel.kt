package com.example.cgo.ui.screens.rankings

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.example.cgo.data.database.entities.User
import com.example.cgo.data.database.entities.UserWithEvents
import com.example.cgo.ui.controllers.UsersViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.androidx.compose.koinViewModel

data class RankingsState(
    val users: List<UserWithEvents> = emptyList()
)

interface RankingsActions {
    fun changeUsersList(users: List<UserWithEvents>)
    @Composable
    fun LoadUsers()
}

class RankingsViewModel : ViewModel() {
    private val _state = MutableStateFlow(RankingsState())
    val state = _state.asStateFlow()

    val actions = object : RankingsActions {
        override fun changeUsersList(users: List<UserWithEvents>) = _state.update { it.copy(users = users) }

        @Composable
        override fun LoadUsers() {
            _state.update { it.copy(users = emptyList()) }
            val usersViewModel = koinViewModel<UsersViewModel>()
            val users = mutableListOf<UserWithEvents>()
            // Retrieves all users with events
            onQueryComplete(
                result = usersViewModel.getUsersWithEvents(),
                onComplete = {result ->
                    (result as List<*>).forEach { users.add(it as UserWithEvents) }
                    changeUsersList(users)
                },
                checkResult = {result ->
                    result is List<*> && result.all { it is UserWithEvents } && result.isNotEmpty()
                }
            )
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        fun onQueryComplete(result: Deferred<Any>, onComplete: (Any) -> Unit, checkResult: (Any) -> Boolean) {
            result.invokeOnCompletion {
                if (it == null) {
                    if (checkResult(result.getCompleted()))
                        onComplete(result.getCompleted())
                }
            }
        }
    }
}
