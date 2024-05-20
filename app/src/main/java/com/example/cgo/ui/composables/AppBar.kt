package com.example.cgo.ui.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.example.cgo.R
import com.example.cgo.ui.OCGRoute
import com.example.cgo.utils.PreferencesManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    navController: NavHostController,
    currentRoute: OCGRoute
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                currentRoute.title,
                fontWeight = FontWeight.Medium,
            )
        },
        navigationIcon = {
            if (navController.previousBackStackEntry != null) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "Back Button"
                    )
                }
            }
        },
        actions = {
            // While in the profile screen the user can access the app's settings
            if (currentRoute.route == OCGRoute.Profile.route) {
                IconButton(onClick = { navController.navigate(OCGRoute.Settings.route) }) {
                    Icon(Icons.Outlined.Settings, "Settings")
                }
            }
            if (currentRoute.route == OCGRoute.Settings.route) {
                val context = LocalContext.current
                val preferencesManager = remember { PreferencesManager(context) }
                IconButton(onClick = {
                    preferencesManager.clearPreferences()
                    navController.popBackStack(OCGRoute.Profile.route, inclusive = true)
                    navController.navigate(OCGRoute.Login.route)
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