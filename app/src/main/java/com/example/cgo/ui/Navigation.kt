package com.example.cgo.ui

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.cgo.data.database.entities.User
import com.example.cgo.ui.controllers.UsersViewModel
import com.example.cgo.ui.screens.login.LoginScreen
import com.example.cgo.ui.screens.login.LoginViewModel
import com.example.cgo.ui.screens.registration.RegistrationScreen
import com.example.cgo.ui.screens.registration.RegistrationViewModel
import com.example.cgo.utils.PreferencesManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.compose.koinViewModel
import com.example.cgo.ui.controllers.EventsViewModel
import com.example.cgo.ui.screens.addevent.AddEventScreen
import com.example.cgo.ui.screens.addevent.AddEventViewModel
import com.example.cgo.ui.screens.home.HomeScreen
import com.example.cgo.ui.screens.profile.ProfileScreen
import com.example.cgo.ui.screens.settings.SettingsScreen
import com.example.cgo.ui.screens.settings.SettingsViewModel
import com.example.cgo.ui.screens.settings.changeprofile.EditProfileScreen
import com.example.cgo.ui.screens.settings.changeprofile.EditProfileViewModel
import com.example.cgo.ui.theme.Theme
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
        "profile/{email}",
        "Profile",
        listOf(navArgument("email") { type = NavType.StringType })
    ) {
        fun buildRoute(email: String) = "profile/$email"
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
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val usersViewModel = koinViewModel<UsersViewModel>()
    val eventsVm = koinViewModel<EventsViewModel>()

    NavHost(
        navController = navController,
        startDestination = OCGRoute.Login.route,
        modifier = modifier
    ) {
        with(OCGRoute.Home) {
            composable(route) {
                val eventsState by eventsVm.state.collectAsStateWithLifecycle()
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
                if (checkLogin(preferencesManager)) {
                    navController.popBackStack()
                    navController.navigate(OCGRoute.Home.route)
                } else {
                    LoginScreen(
                        state = state,
                        actions = loginViewModel.actions,
                        onLogin = { email: String, password: String ->
                            onQueryComplete(
                                usersViewModel.checkLogin(email, password),
                                onComplete = {
                                    login(preferencesManager = preferencesManager, email = email, password = password)
                                    navController.popBackStack(OCGRoute.Login.route, inclusive = true)
                                    navController.navigate(OCGRoute.Home.route)
                                },
                                checkResult = {
                                    it is Boolean && it == true
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
                        login(preferencesManager= preferencesManager, email = state.email, password = state.password)
                        navController.popBackStack(OCGRoute.Registration.route, inclusive = true)
                        navController.navigate(OCGRoute.Home.route)
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
                    state,
                    addEventVm.actions,
                    { eventsVm.addEvent(state.toEvent()) },
                    navController
                )
            }
        }
        with(OCGRoute.Rankings) {
            composable(route) {
                // TODO: Open rankings screen
            }
        }
        with(OCGRoute.Profile) {
            composable(route, arguments) {backStackEntry: NavBackStackEntry ->
                // Create temporary user (also useful in case of error while fetching data from Database)
                val user = remember { mutableStateOf(User(userId = -1, username = "NONE", email = "", password = "", profilePicture = Uri.EMPTY.toString(), gamesWon = 0)) }
                // Variable used to check if the coroutine is finished
                var isCoroutineFinished by remember { mutableStateOf(false) }

                if (checkLogin(preferencesManager)) {
                    onQueryComplete(
                        usersViewModel.getUserInfo(backStackEntry.arguments?.getString("email").toString()),
                        onComplete = {result: Any ->
                            user.value = result as User
                            isCoroutineFinished = true
                        },
                        checkResult = {result: Any ->
                            result is User
                        }
                    )
                }
                if (isCoroutineFinished) {
                    // TODO: fetch events from database
                    val eventsState by eventsVm.state.collectAsStateWithLifecycle()
                    val events = eventsState.events
                    ProfileScreen(user = user.value, events = events, navController = navController)
                }
            }
        }
        with(OCGRoute.EventDetails) {
            composable(route, arguments) {
                // TODO: Open event details screen
            }
        }
        with(OCGRoute.Settings) {
            composable(route) {
                val settingsViewModel = koinViewModel<SettingsViewModel>()
                val state by settingsViewModel.state.collectAsStateWithLifecycle()
                SettingsScreen(
                    state = state,
                    navController = navController,
                    changeTheme = settingsViewModel::changeTheme
                )
            }
        }
        with(OCGRoute.EditProfile) {
            composable(route) {backStackEntry: NavBackStackEntry ->
                // Create temporary user (also useful in case of error while fetching data from Database)
                var user by remember { mutableStateOf(User(userId = -1, username = "NONE", email = "", password = "", profilePicture = Uri.EMPTY.toString(), gamesWon = 0)) }
                // Variable used to check if the coroutine is finished
                var isCoroutineFinished by remember { mutableStateOf(false) }

                onQueryComplete(
                    usersViewModel.getUserInfo(preferencesManager.getData("email", "")),
                    onComplete = {result: Any ->
                        user = result as User
                        isCoroutineFinished = true
                    },
                    checkResult = {result: Any ->
                        result is User
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

fun checkLogin(preferencesManager: PreferencesManager) : Boolean {
    return preferencesManager.containsKey("email") && preferencesManager.containsKey("password")
}

fun login(preferencesManager: PreferencesManager, email: String, password: String) {
    preferencesManager.saveData("email", email)
    preferencesManager.saveData("password", password)
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