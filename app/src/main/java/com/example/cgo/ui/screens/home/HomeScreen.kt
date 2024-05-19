package com.example.cgo.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.cgo.data.database.entities.Event
import com.example.cgo.ui.OCGRoute
import com.example.cgo.ui.controllers.EventsState

@Composable
fun HomeScreen(
    state: EventsState,
    navController: NavHostController
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = { navController.navigate(OCGRoute.EventsMap.route) }
            ) {
                Icon(Icons.Outlined.LocationOn, "Event Map")
            }
        },
    )
    { contentPadding ->
        if (state.events.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.padding(contentPadding)
            ) {
                items(state.events) { event ->
                    EventItem(
                        event,
                        onClick = { navController.navigate(OCGRoute.EventDetails.route) }
                    )
                }
            }
        } else {
            NoItemsPlaceholder()
        }
    }
}

@Composable
fun EventItem(event: Event, onClick: () -> Unit) {
    // TODO: Migliorare la disposizione delle informazioni
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = { Text(text = event.title) },
        supportingContent = {
            Column {
                Text(text = "Dove: " + event.location)
                Text(text = "Quando: " + event.date)
            }
        },
        trailingContent = {
            Column {
                Text(text = "Ore: " + event.time)
                Text(text = "Posti: " + event.maxParticipants)
            }
        },
    )
    HorizontalDivider()
}

@Composable
fun NoItemsPlaceholder() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("No events yet")
        Text(
            "Tap the button to view the map of events.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
