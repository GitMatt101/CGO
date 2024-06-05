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
import com.example.cgo.ui.OCGRoute
import com.example.cgo.ui.controllers.AppViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MenuBar(navController: NavHostController) {
    val routesBlackList = listOf(OCGRoute.Home, OCGRoute.Search, OCGRoute.AddEvent, OCGRoute.Rankings)
    fun deleteDuplicates(route: String) {
        navController.popBackStack(route, true)
        OCGRoute.routes.filter { !routesBlackList.contains(it) }.forEach { navController.popBackStack(it.route, true) }
    }
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Row (modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.weight(1.0f, true))

            IconButton(onClick = {
                deleteDuplicates(OCGRoute.Home.route)
                navController.navigate(OCGRoute.Home.route)
            }) {
                MenuIcon(painterResource(id = R.drawable.home), "Home", 80)
            }

            Spacer(modifier = Modifier.weight(1.0f, true))
            IconButton(onClick = {
                deleteDuplicates(OCGRoute.Search.route)
                navController.navigate(OCGRoute.Search.route)
            }) {
                MenuIcon(painterResource(id = R.drawable.search), "Search", 80)
            }

            Spacer(modifier = Modifier.weight(1.0f, true))
            IconButton(onClick = {
                deleteDuplicates(OCGRoute.AddEvent.route)
                navController.navigate(OCGRoute.AddEvent.route)
            }) {
                MenuIcon(painterResource(id = R.drawable.add), "Add Event", 80)
            }

            Spacer(modifier = Modifier.weight(1.0f, true))
            IconButton(onClick = {
                deleteDuplicates(OCGRoute.Rankings.route)
                navController.navigate(OCGRoute.Rankings.route)
            }) {
                MenuIcon(painterResource(id = R.drawable.trophy), "Rankings", 70)
            }

            Spacer(modifier = Modifier.weight(1.0f, true))
            val appViewModel = koinViewModel<AppViewModel>()
            val appState by appViewModel.state.collectAsStateWithLifecycle()
            IconButton(onClick = {
                deleteDuplicates(OCGRoute.Profile.route)
                navController.navigate(appState.userId?.let { OCGRoute.Profile.buildRoute(it) } ?: run { OCGRoute.Profile.buildRoute(-1) })
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