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

enum class Filter {
    EventsPlayed,
    EventsWon,
    EventsHosted
}

data class RankingsState(
    val filter: Filter = Filter.EventsHosted,
    val users: List<UserWithEvents> = emptyList()
)

interface RankingsActions {
    fun changeFilter(filter: Filter)
    fun changeUsersList(users: List<UserWithEvents>)
    @Composable
    fun LoadUsersByEventsHosted()
}

class RankingsViewModel : ViewModel() {
    private val _state = MutableStateFlow(RankingsState())
    val state = _state.asStateFlow()

    val actions = object : RankingsActions {
        override fun changeFilter(filter: Filter) = _state.update { it.copy(filter = filter) }
        override fun changeUsersList(users: List<UserWithEvents>) = _state.update { it.copy(users = users) }

        @Composable
        override fun LoadUsersByEventsHosted() {
            println("Inside load users")
            _state.update { it.copy(users = emptyList()) }
            val usersViewModel = koinViewModel<UsersViewModel>()
            val users = mutableListOf<UserWithEvents>()
            // Retrieves all users with events
            onQueryComplete(
                result = usersViewModel.getUsersHosts(),
                onComplete = {result ->
                    (result as List<*>).forEach { users.add(it as UserWithEvents) }
                    users.sortBy { -it.events.size }
                    changeUsersList(users)
                },
                checkResult = {result ->
                    println("RESULT: $result")
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
