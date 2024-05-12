package com.example.cgo.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.example.cgo.ui.OCGRoute

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
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
    if (currentRoute.route == OCGRoute.Login.route) {
        Column(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                navController.popBackStack()
                navController.navigate(OCGRoute.Home.route)
            })
            { Text("Login") }
        }
    }
}