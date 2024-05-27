package com.example.cgo.ui

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.cgo.data.database.entities.Event
import com.example.cgo.data.database.entities.EventWithUsers
import com.example.cgo.data.database.entities.Participation
import com.example.cgo.data.database.entities.PrivacyType
import com.example.cgo.data.database.entities.User
import com.example.cgo.data.database.entities.UserWithEvents
import com.example.cgo.ui.controllers.AppViewModel
import com.example.cgo.ui.controllers.UsersViewModel
import com.example.cgo.ui.screens.login.LoginScreen
import com.example.cgo.ui.screens.login.LoginViewModel
import com.example.cgo.ui.screens.registration.RegistrationScreen
import com.example.cgo.ui.screens.registration.RegistrationViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.compose.koinViewModel
import com.example.cgo.ui.controllers.EventsViewModel
import com.example.cgo.ui.controllers.ParticipationsViewModel
import com.example.cgo.ui.screens.addevent.AddEventScreen
import com.example.cgo.ui.screens.addevent.AddEventViewModel
import com.example.cgo.ui.screens.eventdetails.EventDetailsScreen
import com.example.cgo.ui.screens.home.HomeScreen
import com.example.cgo.ui.screens.profile.ProfileScreen
import com.example.cgo.ui.screens.rankings.RankingsScreen
import com.example.cgo.ui.screens.rankings.RankingsViewModel
import com.example.cgo.ui.screens.settings.SettingsScreen
import com.example.cgo.ui.screens.settings.changeprofile.EditProfileScreen
import com.example.cgo.ui.screens.settings.changeprofile.EditProfileViewModel
import kotlinx.coroutines.Deferred

sealed class OCGRoute(
    val route: String,
    val title: String,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    // Login routes
    data object Login : OCGRoute("login", "Login")
    data object Registration : OCGRoute("registration", "Registration")

    // Menu routes
    data object Home : OCGRoute("events", "Events")
    data object Search : OCGRoute("search", "Search")
    data object AddEvent : OCGRoute("add", "Create Event")
    data object Rankings : OCGRoute("rankings", "Rankings")
    data object Profile : OCGRoute(
        "profile/{userId}",
        "Profile",
        listOf(navArgument("userId") { type = NavType.IntType })
    ) {
        fun buildRoute(userId: Int) = "profile/$userId"
    }

    // Other routes
    data object Settings : OCGRoute("settings", "Settings")
    data object EditProfile : OCGRoute("edit-profile", "Edit Profile")
    data object EventsMap : OCGRoute("map", "Events Map")
    data object EventDetails : OCGRoute(
        "events/{eventID}",
        "Event Details",
        listOf(navArgument("eventID") { type = NavType.IntType })
    ) {
        fun buildRoute(eventID: Int) = "events/$eventID"
    }

    companion object {
        val routes = setOf(
            Login,
            Registration,
            Home,
            Search,
            AddEvent,
            Rankings,
            Profile,
            Settings,
            EditProfile,
            EventsMap,
            EventDetails
        )
    }
}

@Composable
fun OCGNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val usersViewModel = koinViewModel<UsersViewModel>()
    val eventsVm = koinViewModel<EventsViewModel>()
    val eventsState by eventsVm.state.collectAsStateWithLifecycle()
    val participationsViewModel = koinViewModel<ParticipationsViewModel>()
    val appViewModel = koinViewModel<AppViewModel>()
    val appState by appViewModel.state.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = OCGRoute.Login.route,
        modifier = modifier
    ) {
        with(OCGRoute.Home) {
            composable(route) {
                HomeScreen(
                    eventsState,
                    navController
                )
            }
        }
        with(OCGRoute.Login) {
            composable(route) {
                val loginViewModel = koinViewModel<LoginViewModel>()
                val state by loginViewModel.state.collectAsStateWithLifecycle()
                if (appState.userId != -1) {
                    navController.popBackStack()
                    navController.navigate(OCGRoute.Home.route)
                } else {
                    LoginScreen(
                        state = state,
                        actions = loginViewModel.actions,
                        onLogin = { email: String, password: String ->
                            onQueryComplete(
                                usersViewModel.getUserOnLogin(email = email, password = password),
                                onComplete = {result: Any ->
                                    appViewModel.changeUserId((result as User).userId).invokeOnCompletion {
                                        if (it == null) {
                                            navController.popBackStack(OCGRoute.Login.route, inclusive = true)
                                            navController.navigate(OCGRoute.Home.route)
                                        }
                                    }
                                },
                                checkResult = {
                                    it is User && it.userId != -1
                                }
                            )
                        },
                        navController = navController
                    )
                }
            }
        }
        with(OCGRoute.Registration) {
            composable(route) {
                val registrationViewModel = koinViewModel<RegistrationViewModel>()
                val state by registrationViewModel.state.collectAsStateWithLifecycle()
                RegistrationScreen(
                    state = state,
                    actions = registrationViewModel.actions,
                    onSubmit = {
                        usersViewModel.addUser(state.createUser())
                        onQueryComplete(
                            usersViewModel.getUserOnLogin(email = state.email, password = state.password),
                            onComplete = {result: Any ->
                                appViewModel.changeUserId((result as User).userId).invokeOnCompletion {
                                    if (it == null) {
                                        navController.popBackStack(OCGRoute.Login.route, inclusive = true)
                                        navController.navigate(OCGRoute.Home.route)
                                    }
                                }
                            },
                            checkResult = {
                                it is User && it.userId != -1
                            }
                        )
                    }
                )
            }
        }
        with(OCGRoute.Search) {
            composable(route) {
                // TODO: Open search screen
            }
        }
        with(OCGRoute.AddEvent) {
            composable(route) {
                val addEventVm = koinViewModel<AddEventViewModel>()
                val state by addEventVm.state.collectAsStateWithLifecycle()
                AddEventScreen(
                    state = state,
                    actions = addEventVm.actions,
                    onSubmit = { eventsVm.addEvent(state.toEvent(appState.userId)) },
                    navController = navController
                )
            }
        }
        with(OCGRoute.Rankings) {
            composable(route) {
                val rankingsViewModel = koinViewModel<RankingsViewModel>()
                val state by rankingsViewModel.state.collectAsStateWithLifecycle()
                RankingsScreen(
                    state = state,
                    actions = rankingsViewModel.actions,
                    navController = navController
                )
            }
        }
        with(OCGRoute.Profile) {
            composable(route, arguments) {backStackEntry: NavBackStackEntry ->
                // Create temporary user (also useful in case of error while fetching data from Database)
                var userWithEvents by remember { mutableStateOf(UserWithEvents(
                        user = User(
                            userId = -1,
                            username = "",
                            email = "",
                            password = "",
                            profilePicture = null,
                            gamesWon = -1
                        ),
                        events = emptyList(),
                        wonEvents = emptyList(),
                        createdEvents = emptyList()
                    ))
                }
                // Variable used to check if the coroutine is finished
                var isCoroutineFinished by remember { mutableStateOf(false) }

                if (backStackEntry.arguments?.getInt("userId") != -1) {
                    onQueryComplete(
                        result = usersViewModel.getUserWithEventsById(backStackEntry.arguments?.getInt("userId") ?: -1),
                        onComplete = {result: Any ->
                            userWithEvents = result as UserWithEvents
                            isCoroutineFinished = true
                        },
                        checkResult = {result: Any? ->
                            result != null && result is UserWithEvents
                        }
                    )
                    if (isCoroutineFinished) {
                        ProfileScreen(user = userWithEvents.user, events = userWithEvents.events, navController = navController)
                    }
                }
            }
        }
        with(OCGRoute.EventDetails) {
            composable(route, arguments) { backStackEntry ->
                var eventWithUsers by remember { mutableStateOf(EventWithUsers(
                    event = Event(
                        eventId = -1,
                        title = "",
                        description = "",
                        date = "",
                        time = "",
                        location = "",
                        maxParticipants = -1,
                        privacyType = PrivacyType.NONE,
                        eventCreatorId = -1,
                        winnerId = null
                    ),
                    participants = emptyList()
                ))
                }
                var isCoroutineFinished by remember { mutableStateOf(false) }
                onQueryComplete(
                    result = eventsVm.getEventWithUsersById(backStackEntry.arguments?.getInt("eventID") ?: -1),
                    onComplete = {result: Any ->
                        eventWithUsers = result as EventWithUsers
                        isCoroutineFinished = true
                    },
                    checkResult = {result: Any? ->
                        result != null && result is EventWithUsers
                    }
                )
                EventDetailsScreen(
                    eventWithUsers,
                    navController = navController,
                    loggedUserId = appState.userId,
                    onSubscription = { eventId: Int ->
                        // TODO: Check if the event is already full
                        participationsViewModel.addParticipation(Participation(appState.userId, eventId))
                    },
                    onWinnerSelection = {userId: Int ->
                        eventsVm.updateEvent(Event(
                            eventId = eventWithUsers.event.eventId,
                            title = eventWithUsers.event.title,
                            description = eventWithUsers.event.description,
                            date = eventWithUsers.event.date,
                            time = eventWithUsers.event.time,
                            location = eventWithUsers.event.location,
                            maxParticipants = eventWithUsers.event.maxParticipants,
                            privacyType = eventWithUsers.event.privacyType,
                            eventCreatorId = eventWithUsers.event.eventCreatorId,
                            winnerId = userId
                        ))
                    }
                )
            }
        }
        with(OCGRoute.Settings) {
            composable(route) {
                SettingsScreen(
                    state = appState,
                    navController = navController,
                    changeTheme = appViewModel::changeTheme
                )
            }
        }
        with(OCGRoute.EditProfile) {
            composable(route) {
                // Create temporary user (also useful in case of error while fetching data from Database)
                var user by remember { mutableStateOf(User(userId = -1, username = "NONE", email = "", password = "", profilePicture = Uri.EMPTY.toString(), gamesWon = 0)) }
                // Variable used to check if the coroutine is finished
                var isCoroutineFinished by remember { mutableStateOf(false) }

                onQueryComplete(
                    usersViewModel.getUserInfo(appState.userId),
                    onComplete = { result: Any ->
                        user = result as User
                        isCoroutineFinished = true
                    },
                    checkResult = { result: Any? ->
                        result != null && result is User
                    }
                )
                if (appState.userId != -1 && isCoroutineFinished) {
                    val editProfileViewModel = koinViewModel<EditProfileViewModel>()
                    val state by editProfileViewModel.state.collectAsStateWithLifecycle()
                    EditProfileScreen(
                        username = user.username,
                        profilePicture = user.profilePicture,
                        state = state,
                        actions = editProfileViewModel.actions,
                        onSubmit = {newUsername: String, newProfilePicture: Uri ->
                            val updatedUser = User(
                                user.userId,
                                username = newUsername,
                                email = user.email,
                                password = user.password,
                                gamesWon = user.gamesWon,
                                profilePicture = newProfilePicture.toString()
                            )
                            usersViewModel.updateUser(updatedUser)
                            navController.navigateUp()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun onQueryComplete(result: Deferred<Any?>, onComplete: (Any) -> Unit, checkResult: (Any?) -> Boolean) {
    result.invokeOnCompletion {
        if (it == null) {
            if (checkResult(result.getCompleted()))
                onComplete(result.getCompleted()!!)
        }
    }
}