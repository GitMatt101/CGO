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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.cgo.R
import com.example.cgo.ui.CGORoute
import com.example.cgo.ui.controllers.AppViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MenuBar(navController: NavHostController) {
    val routesBlackList = listOf(CGORoute.Home, CGORoute.Search, CGORoute.AddEvent, CGORoute.Rankings)
    fun deleteDuplicates(route: String) {
        navController.popBackStack(route, true)
        CGORoute.routes.filter { !routesBlackList.contains(it) }.forEach { navController.popBackStack(it.route, true) }
    }
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Row (modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.weight(1.0f, true))

            IconButton(onClick = {
                deleteDuplicates(CGORoute.Home.route)
                navController.navigate(CGORoute.Home.route)
            }) {
                MenuIcon(painterResource(id = R.drawable.home), "Home", 80)
            }

            Spacer(modifier = Modifier.weight(1.0f, true))
            IconButton(onClick = {
                deleteDuplicates(CGORoute.Search.route)
                navController.navigate(CGORoute.Search.route)
            }) {
                MenuIcon(painterResource(id = R.drawable.search), "Search", 80)
            }

            Spacer(modifier = Modifier.weight(1.0f, true))
            IconButton(onClick = {
                deleteDuplicates(CGORoute.AddEvent.route)
                navController.navigate(CGORoute.AddEvent.route)
            }) {
                MenuIcon(painterResource(id = R.drawable.add), "Add Event", 80)
            }

            Spacer(modifier = Modifier.weight(1.0f, true))
            IconButton(onClick = {
                deleteDuplicates(CGORoute.Rankings.route)
                navController.navigate(CGORoute.Rankings.route)
            }) {
                MenuIcon(painterResource(id = R.drawable.trophy), "Rankings", 70)
            }

            Spacer(modifier = Modifier.weight(1.0f, true))
            val appViewModel = koinViewModel<AppViewModel>()
            val appState by appViewModel.state.collectAsStateWithLifecycle()
            IconButton(onClick = {
                deleteDuplicates(CGORoute.Profile.route)
                navController.navigate(appState.userId?.let { CGORoute.Profile.buildRoute(it) } ?: run { CGORoute.Profile.buildRoute(-1) })
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