package com.example.cgo.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument

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
        val routes = setOf(Login, Registration, Home, Search, AddEvent, Rankings, Profile, Settings, EventsMap, EventDetails)
    }
}

@Composable
fun OCGNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    // TODO: Add koinViewModel and state

    NavHost(
        navController = navController,
        startDestination = OCGRoute.Login.route,
        modifier = modifier
    ) {
        with(OCGRoute.Login) {
            composable(route) {
                // TODO: Open login screen
            }
        }
        with(OCGRoute.Registration) {
            composable(route) {
                // TODO: Open registration screen
            }
        }
        with(OCGRoute.Home) {
            composable(route) {
                // TODO: Open home screen
            }
        }
        with(OCGRoute.Search) {
            composable(route) {
                // TODO: Open search screen
            }
        }
        with(OCGRoute.AddEvent) {
            composable(route) {
                // TODO: Open add event screen
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