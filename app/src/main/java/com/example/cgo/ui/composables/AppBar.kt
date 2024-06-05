package com.example.cgo.ui.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.cgo.R
import com.example.cgo.ui.CGORoute
import com.example.cgo.ui.controllers.AppViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    navController: NavHostController,
    currentRoute: CGORoute
) {
    val routesBlackList = listOf(CGORoute.Home, CGORoute.Search, CGORoute.AddEvent, CGORoute.Rankings)
    fun checkRoute() : Boolean = routesBlackList.map { it.route }.contains(currentRoute.route)

    val appViewModel = koinViewModel<AppViewModel>()
    val appState by appViewModel.state.collectAsStateWithLifecycle()
    CenterAlignedTopAppBar(
        title = {
            Text(
                currentRoute.title,
                fontWeight = FontWeight.Medium,
            )
        },
        navigationIcon = {
            if (navController.previousBackStackEntry != null && !checkRoute()) {
                if (currentRoute == CGORoute.Profile && navController.currentBackStackEntry?.arguments?.getInt("userId") == appState.userId)
                    return@CenterAlignedTopAppBar
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back Button"
                    )
                }
            }
        },
        actions = {
            // While in the profile screen the user can access the app's settings
            if (currentRoute.route == CGORoute.Profile.route && navController.currentBackStackEntry?.arguments?.getInt("userId") == appState.userId) {
                IconButton(onClick = { navController.navigate(CGORoute.Settings.route) }) {
                    Icon(Icons.Outlined.Settings, "Settings")
                }
            }
            if (currentRoute.route == CGORoute.Settings.route) {
                IconButton(onClick = {
                    appViewModel.changeUserId(null).invokeOnCompletion {
                        if (it == null) {
                            CGORoute.routes.forEach { route: CGORoute -> navController.popBackStack(route.route, true) }
                            navController.navigate(CGORoute.Login.route)
                        }
                    }
                }) {
                    Icon(painterResource(id = R.drawable.logout), "Logout")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}