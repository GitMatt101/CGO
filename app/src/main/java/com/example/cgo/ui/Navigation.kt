package com.example.cgo.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
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
import org.koin.androidx.compose.koinViewModel

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
        "profile/{profileID}",
        "Profile",
        listOf(navArgument("profileID") { type = NavType.IntType })
    ) {
        fun buildRoute(profileID: Int) = "profile/$profileID"
    }

    // Other routes
    data object Settings : OCGRoute("settings", "Settings")
    data object EventsMap : OCGRoute("map", "Events Map")
    data object EventDetails : OCGRoute(
        "events/{eventID}",
        "Event Details",
        listOf(navArgument("eventID") { type = NavType.IntType })
    ) {
        fun buildRoute(eventID: String) = "events/$eventID"
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
            EventsMap,
            EventDetails
        )
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun OCGNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val usersViewModel = koinViewModel<UsersViewModel>()
    val preferencesManager = remember { PreferencesManager(context) }
    val eventsVm = koinViewModel<EventsViewModel>()
    val eventsState by eventsVm.state.collectAsStateWithLifecycle()

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
                if (preferencesManager.containsKey("email") && preferencesManager.containsKey("password")) {
                    navController.popBackStack()
                    navController.navigate(OCGRoute.Home.route)
                } else {
                    LoginScreen(
                        state = state,
                        actions = loginViewModel.actions,
                        onLogin = { email: String, password: String ->
                            val result = usersViewModel.checkUserExists(email, password)
                            result.invokeOnCompletion {
                                try {
                                    if (it == null && result.getCompleted()) {
                                        preferencesManager.saveData("email", state.email)
                                        preferencesManager.saveData("password", state.password)
                                        navController.navigate(OCGRoute.Home.route)
                                        navController.popBackStack()
                                    }
                                } catch (_: IllegalStateException) {}
                            }
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
                        preferencesManager.saveData("email", state.email)
                        preferencesManager.saveData("password", state.password)
                        navController.navigate(OCGRoute.Home.route)
                        navController.popBackStack()
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
            composable(route, arguments) {
                // TODO: Open profile screen
            }
        }
        with(OCGRoute.EventDetails) {
            composable(route, arguments) {
                // TODO: Open event details screen
            }
        }
        with(OCGRoute.Settings) {
            composable(route) {
                // TODO: Open settings screen
            }
        }
    }
}