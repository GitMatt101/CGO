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
import com.example.cgo.ui.screens.eventmap.EventMapScreen
import com.example.cgo.ui.screens.addevent.AddEventScreen
import com.example.cgo.ui.screens.addevent.AddEventViewModel
import com.example.cgo.ui.screens.eventdetails.EventDetailsScreen
import com.example.cgo.ui.screens.home.HomeScreen
import com.example.cgo.ui.screens.profile.ProfileScreen
import com.example.cgo.ui.screens.rankings.RankingsScreen
import com.example.cgo.ui.screens.search.SearchScreen
import com.example.cgo.ui.screens.settings.SettingsScreen
import com.example.cgo.ui.screens.settings.changeprofile.EditProfileScreen
import com.example.cgo.ui.screens.settings.changeprofile.EditProfileViewModel
import kotlinx.coroutines.Deferred

sealed class CGORoute(
    val route: String,
    val title: String,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    // Login routes
    data object Login : CGORoute("login", "Login")
    data object Registration : CGORoute("registration", "Registration")

    // Menu routes
    data object Home : CGORoute("events", "Events")
    data object Search : CGORoute("search", "Search")
    data object AddEvent : CGORoute("add", "Create Event")
    data object Rankings : CGORoute("rankings", "Rankings")
    data object Profile : CGORoute(
        "profile/{userId}",
        "Profile",
        listOf(navArgument("userId") { type = NavType.IntType })
    ) {
        fun buildRoute(userId: Int) = "profile/$userId"
    }

    // Other routes
    data object Settings : CGORoute("settings", "Settings")
    data object EditProfile : CGORoute("edit-profile", "Edit Profile")
    data object EventsMap : CGORoute("map", "Events Map")
    data object EventDetails : CGORoute(
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
    val usersState by usersViewModel.state.collectAsStateWithLifecycle()
    val eventsVm = koinViewModel<EventsViewModel>()
    val eventsState by eventsVm.state.collectAsStateWithLifecycle()
    val participationsViewModel = koinViewModel<ParticipationsViewModel>()
    val appViewModel = koinViewModel<AppViewModel>()
    val appState by appViewModel.state.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = CGORoute.Login.route,
        modifier = modifier
    ) {
        with(CGORoute.Home) {
            composable(route) {
                if (appState.userId == null) {
                    navigateAndClearBackstack(route, CGORoute.Login.route, navController)
                    return@composable
                }
                val createdEvents =
                    eventsState.eventsWithUsers.filter { it.event.eventCreatorId == appState.userId }
                val publicEvents =
                    eventsState.eventsWithUsers.filter { it.event.privacyType == PrivacyType.PUBLIC }
                HomeScreen(
                    publicEvents = publicEvents,
                    createdEvents = createdEvents,
                    navController = navController,
                )
            }
        }
        with(CGORoute.Login) {
            composable(route) {
                val loginViewModel = koinViewModel<LoginViewModel>()
                val state by loginViewModel.state.collectAsStateWithLifecycle()
                if (appState.userId != null) {
                    navController.popBackStack()
                    navController.navigate(CGORoute.Home.route)
                } else {
                    LoginScreen(
                        state = state,
                        actions = loginViewModel.actions,
                        onLogin = { email: String, password: String, errorMessage: () -> Unit ->
                            onQueryComplete(
                                usersViewModel.getUserOnLogin(email = email, password = password),
                                onComplete = { result: Any ->
                                    appViewModel.changeUserId((result as User).userId)
                                        .invokeOnCompletion {
                                            if (it == null)
                                                navigateAndClearBackstack(route, CGORoute.Home.route, navController)
                                        }
                                },
                                checkResult = { result: Any? ->
                                    val check = result != null && result is User
                                    if (!check) {
                                        errorMessage()
                                    }
                                    return@onQueryComplete check
                                }
                            )
                        },
                        navController = navController
                    )
                }
            }
        }
        with(CGORoute.Registration) {
            composable(route) {
                val registrationViewModel = koinViewModel<RegistrationViewModel>()
                val state by registrationViewModel.state.collectAsStateWithLifecycle()
                RegistrationScreen(
                    usersState = usersState,
                    state = state,
                    actions = registrationViewModel.actions,
                    onSubmit = {
                        usersViewModel.addUser(state.createUser()).invokeOnCompletion {
                            onQueryComplete(
                                usersViewModel.getUserOnLogin(
                                    email = state.email,
                                    password = state.password
                                ),
                                onComplete = { result: Any ->
                                    appViewModel.changeUserId((result as User).userId)
                                        .invokeOnCompletion {
                                            if (it == null)
                                                navigateAndClearBackstack(route, CGORoute.Home.route, navController)
                                        }
                                },
                                checkResult = { result: Any? ->
                                    result != null && result is User
                                }
                            )
                        }
                    },
                    navController = navController
                )
            }
        }
        with(CGORoute.Search) {
            composable(route) {
                if (appState.userId == null) {
                    navigateAndClearBackstack(route, CGORoute.Login.route, navController)
                    return@composable
                }
                SearchScreen(
                    eventsState = eventsState,
                    usersState = usersState,
                    navController = navController
                )
            }
        }
        with(CGORoute.AddEvent) {
            composable(route) {
                if (appState.userId == null) {
                    navigateAndClearBackstack(route, CGORoute.Login.route, navController)
                    return@composable
                }
                val addEventVm = koinViewModel<AddEventViewModel>()
                val state by addEventVm.state.collectAsStateWithLifecycle()
                AddEventScreen(
                    state = state,
                    actions = addEventVm.actions,
                    onSubmit = {
                        eventsVm.addEvent(state.toEvent(appState.userId!!))
                        navController.navigateUp()
                    }
                )
            }
        }
        with(CGORoute.Rankings) {
            composable(route) {
                if (appState.userId == null) {
                    navigateAndClearBackstack(route, CGORoute.Login.route, navController)
                    return@composable
                }
                RankingsScreen(
                    usersWithEvents = usersState.usersWithEvents,
                    navController = navController
                )
            }
        }
        with(CGORoute.Profile) {
            composable(route, arguments) { backStackEntry: NavBackStackEntry ->
                if (appState.userId == null) {
                    navigateAndClearBackstack(route, CGORoute.Login.route, navController)
                    return@composable
                }
                // Create temporary user (also useful in case of error while fetching data from Database)
                var userWithEvents by remember {
                    mutableStateOf(
                        UserWithEvents(
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
                        )
                    )
                }
                // Variable used to check if the coroutine is finished
                var isCoroutineFinished by remember { mutableStateOf(false) }

                onQueryComplete(
                    result = usersViewModel.getUserWithEventsById(
                        backStackEntry.arguments?.getInt(
                            "userId"
                        ) ?: -1
                    ),
                    onComplete = { result: Any ->
                        userWithEvents = result as UserWithEvents
                        isCoroutineFinished = true
                    },
                    checkResult = { result: Any? ->
                        result != null && result is UserWithEvents
                    }
                )
                if (isCoroutineFinished) {
                    ProfileScreen(
                        user = userWithEvents.user,
                        events = userWithEvents.events,
                        navController = navController
                    )
                }
            }
        }
        with(CGORoute.EventDetails) {
            composable(route, arguments) { backStackEntry ->
                if (appState.userId == null) {
                    navigateAndClearBackstack(route, CGORoute.Login.route, navController)
                    return@composable
                }
                var eventWithUsers by remember {
                    mutableStateOf(
                        EventWithUsers(
                            event = Event(
                                eventId = -1,
                                title = "",
                                description = "",
                                date = "",
                                time = "",
                                address = "",
                                city = "",
                                maxParticipants = -1,
                                privacyType = PrivacyType.NONE,
                                eventCreatorId = -1,
                                winnerId = null
                            ),
                            participants = emptyList()
                        )
                    )
                }
                var user by remember {
                    mutableStateOf(
                        User(
                            userId = -1,
                            username = "",
                            email = "",
                            password = "",
                            profilePicture = null,
                            gamesWon = -1
                        )
                    )
                }
                var isEventCoroutineFinished by remember { mutableStateOf(false) }
                var isUserCoroutineFinished by remember { mutableStateOf(false) }
                onQueryComplete(
                    result = eventsVm.getEventWithUsersById(
                        backStackEntry.arguments?.getInt("eventID") ?: -1
                    ),
                    onComplete = { result: Any ->
                        eventWithUsers = result as EventWithUsers
                        isEventCoroutineFinished = true
                    },
                    checkResult = { result: Any? ->
                        result != null && result is EventWithUsers
                    }
                )
                onQueryComplete(
                    result = usersViewModel.getUserInfo(eventWithUsers.event.eventCreatorId),
                    onComplete = { result: Any ->
                        user = result as User
                        isUserCoroutineFinished = true
                    },
                    checkResult = { result: Any? ->
                        result != null && result is User
                    }
                )
                if (isEventCoroutineFinished && isUserCoroutineFinished) {
                    EventDetailsScreen(
                        eventWithUsers,
                        eventCreator = user,
                        navController = navController,
                        loggedUserId = appState.userId!!,
                        onSubscription = { eventId: Int ->
                            participationsViewModel.addParticipation(
                                Participation(
                                    appState.userId!!,
                                    eventId
                                )
                            )
                        },
                        onSubscriptionCanceled = { eventId: Int ->
                            participationsViewModel.deleteParticipation(
                                Participation(
                                    appState.userId!!,
                                    eventId
                                )
                            )
                            if (eventWithUsers.event.winnerId == appState.userId) {
                                eventsVm.updateEvent(
                                    Event(
                                        eventId = eventWithUsers.event.eventId,
                                        title = eventWithUsers.event.title,
                                        description = eventWithUsers.event.description,
                                        date = eventWithUsers.event.date,
                                        time = eventWithUsers.event.time,
                                        address = eventWithUsers.event.address,
                                        city = eventWithUsers.event.city,
                                        maxParticipants = eventWithUsers.event.maxParticipants,
                                        privacyType = eventWithUsers.event.privacyType,
                                        eventCreatorId = eventWithUsers.event.eventCreatorId,
                                        winnerId = null
                                    )
                                )
                            }
                        },
                        onWinnerSelection = { winnerId: Int ->
                            eventsVm.updateEvent(
                                Event(
                                    eventId = eventWithUsers.event.eventId,
                                    title = eventWithUsers.event.title,
                                    description = eventWithUsers.event.description,
                                    date = eventWithUsers.event.date,
                                    time = eventWithUsers.event.time,
                                    address = eventWithUsers.event.address,
                                    city = eventWithUsers.event.city,
                                    maxParticipants = eventWithUsers.event.maxParticipants,
                                    privacyType = eventWithUsers.event.privacyType,
                                    eventCreatorId = eventWithUsers.event.eventCreatorId,
                                    winnerId = winnerId
                                )
                            )
                        },
                        onDelete = {
                            eventsVm.deleteEvent(eventWithUsers.event)
                            navController.navigateUp()
                        },
                        loadParticipants = {
                            isEventCoroutineFinished = false
                            onQueryComplete(
                                result = eventsVm.getEventWithUsersById(
                                    backStackEntry.arguments?.getInt(
                                        "eventID"
                                    ) ?: -1
                                ),
                                onComplete = { result: Any ->
                                    eventWithUsers = result as EventWithUsers
                                    isEventCoroutineFinished = true
                                },
                                checkResult = { result: Any? ->
                                    result != null && result is EventWithUsers
                                }
                            )
                            return@EventDetailsScreen eventWithUsers.participants
                        },
                        onWinnerDeselected = {
                            eventsVm.updateEvent(
                                Event(
                                    eventId = eventWithUsers.event.eventId,
                                    title = eventWithUsers.event.title,
                                    description = eventWithUsers.event.description,
                                    date = eventWithUsers.event.date,
                                    time = eventWithUsers.event.time,
                                    address = eventWithUsers.event.address,
                                    city = eventWithUsers.event.city,
                                    maxParticipants = eventWithUsers.event.maxParticipants,
                                    privacyType = eventWithUsers.event.privacyType,
                                    eventCreatorId = eventWithUsers.event.eventCreatorId,
                                    winnerId = null
                                )
                            )
                        }
                    )
                }
            }
        }
        with(CGORoute.Settings) {
            composable(route) {
                if (appState.userId == null) {
                    navigateAndClearBackstack(route, CGORoute.Login.route, navController)
                    return@composable
                }
                SettingsScreen(
                    state = appState,
                    navController = navController,
                    changeTheme = appViewModel::changeTheme
                )
            }
        }
        with(CGORoute.EditProfile) {
            composable(route) {
                if (appState.userId == null) {
                    navigateAndClearBackstack(route, CGORoute.Login.route, navController)
                    return@composable
                }
                // Create temporary user (also useful in case of error while fetching data from Database)
                var user by remember {
                    mutableStateOf(
                        User(
                            userId = -1,
                            username = "NONE",
                            email = "",
                            password = "",
                            profilePicture = Uri.EMPTY.toString(),
                            gamesWon = 0
                        )
                    )
                }
                // Variable used to check if the coroutine is finished
                var isCoroutineFinished by remember { mutableStateOf(false) }

                onQueryComplete(
                    usersViewModel.getUserInfo(appState.userId!!),
                    onComplete = { result: Any ->
                        user = result as User
                        isCoroutineFinished = true
                    },
                    checkResult = { result: Any? ->
                        result != null && result is User
                    }
                )
                if (isCoroutineFinished) {
                    val editProfileViewModel = koinViewModel<EditProfileViewModel>()
                    val state by editProfileViewModel.state.collectAsStateWithLifecycle()
                    EditProfileScreen(
                        username = user.username,
                        profilePicture = user.profilePicture,
                        state = state,
                        actions = editProfileViewModel.actions,
                        onSubmit = { newUsername: String, newProfilePicture: Uri ->
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
        with(CGORoute.EventsMap) {
            composable(route) {
                if (appState.userId == null) {
                    navigateAndClearBackstack(route, CGORoute.Login.route, navController)
                    return@composable
                }
                EventMapScreen(
                    eventsState = eventsState,
                    navController = navController
                )
            }
        }
    }
}

fun navigateAndClearBackstack(
    currentRoute: String,
    destination: String,
    navController: NavHostController
) {
    navController.popBackStack(
        route = currentRoute,
        inclusive = true
    )
    navController.navigate(destination)
}

@OptIn(ExperimentalCoroutinesApi::class)
fun onQueryComplete(
    result: Deferred<Any?>,
    onComplete: (Any) -> Unit,
    checkResult: (Any?) -> Boolean
) {
    result.invokeOnCompletion {
        if (it == null) {
            if (checkResult(result.getCompleted()))
                onComplete(result.getCompleted()!!)
        }
    }
}