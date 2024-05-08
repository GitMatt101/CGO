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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cgo.R
import com.example.cgo.ui.OCGRoute

@Composable
fun MenuBar(navController: NavHostController, currentRoute: OCGRoute) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Row (modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.weight(1.0f, true))

            IconButton(onClick = {
                if (currentRoute.route != OCGRoute.Home.route)
                    navController.navigate(OCGRoute.Home.route)
            }) {
                MenuIcon(painterResource(id = R.drawable.home), "Home", 80)
            }

            Spacer(modifier = Modifier.weight(1.0f, true))
            IconButton(onClick = {
                if (currentRoute.route != OCGRoute.Search.route)
                    navController.navigate(OCGRoute.Search.route)
            }) {
                MenuIcon(painterResource(id = R.drawable.search), "Search", 80)
            }

            Spacer(modifier = Modifier.weight(1.0f, true))
            IconButton(onClick = {
                if (currentRoute.route != OCGRoute.AddEvent.route)
                    navController.navigate(OCGRoute.AddEvent.route)
            }) {
                MenuIcon(painterResource(id = R.drawable.add), "Add Event", 80)
            }

            Spacer(modifier = Modifier.weight(1.0f, true))
            IconButton(onClick = {
                if (currentRoute.route != OCGRoute.Rankings.route)
                    navController.navigate(OCGRoute.Rankings.route)
            }) {
                MenuIcon(painterResource(id = R.drawable.trophy), "Rankings", 70)
            }

            Spacer(modifier = Modifier.weight(1.0f, true))
            // TODO: Change onClick() to: navController.navigate(OCGRoute.Profile.buildRoute(user.id.toString()))
            IconButton(onClick = {
                if (currentRoute.route != OCGRoute.Profile.route)
                    navController.navigate(OCGRoute.Profile.route)
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