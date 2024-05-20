package com.example.cgo.ui.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cgo.R
import com.example.cgo.ui.OCGRoute
import com.example.cgo.utils.PreferencesManager

@Composable
fun MenuBar(navController: NavHostController) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Row (modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.weight(1.0f, true))

            IconButton(onClick = {
                navController.popBackStack()
                navController.navigate(OCGRoute.Home.route)
            }) {
                MenuIcon(painterResource(id = R.drawable.home), "Home", 80)
            }

            Spacer(modifier = Modifier.weight(1.0f, true))
            IconButton(onClick = {
                navController.popBackStack()
                navController.navigate(OCGRoute.Search.route)
            }) {
                MenuIcon(painterResource(id = R.drawable.search), "Search", 80)
            }

            Spacer(modifier = Modifier.weight(1.0f, true))
            IconButton(onClick = {
                navController.popBackStack()
                navController.navigate(OCGRoute.AddEvent.route)
            }) {
                MenuIcon(painterResource(id = R.drawable.add), "Add Event", 80)
            }

            Spacer(modifier = Modifier.weight(1.0f, true))
            IconButton(onClick = {
                navController.popBackStack()
                navController.navigate(OCGRoute.Rankings.route)
            }) {
                MenuIcon(painterResource(id = R.drawable.trophy), "Rankings", 70)
            }

            Spacer(modifier = Modifier.weight(1.0f, true))
            val context = LocalContext.current
            val preferencesManager = remember { PreferencesManager(context) }
            IconButton(onClick = {
                navController.popBackStack()
                navController.navigate(OCGRoute.Profile.buildRoute(preferencesManager.getData("email", "")))
            }) {
                MenuIcon(painterResource(id = R.drawable.profile), "Profile", 80)
            }

            Spacer(modifier = Modifier.weight(1.0f, true))
        }
    }
}

@Composable
fun MenuIcon(painter: Painter, description: String, size: Int) {
    return Icon(painter = painter, contentDescription = description, modifier = Modifier.size(size.dp))
}