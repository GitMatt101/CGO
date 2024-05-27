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
import com.example.cgo.data.database.entities.EventWithUsers
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
                items(state.eventsWithUsers) { eventWithUsers ->
                    EventItem(
                        eventWithUsers,
                        onClick = { navController.navigate(OCGRoute.EventDetails.buildRoute(eventWithUsers.event.eventId)) }
                    )
                }
            }
        } else {
            NoItemsPlaceholder()
        }
    }
}

@Composable
fun EventItem(eventWithUsers: EventWithUsers, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = { Text(text = eventWithUsers.event.title) },
        supportingContent = {
            Column {
                Text(text = "Location: " + eventWithUsers.event.address + ", " + eventWithUsers.event.city)
                Text(text = "Date: " + eventWithUsers.event.date)
            }
        },
        trailingContent = {
            Column {
                Text(text = "Time: " + eventWithUsers.event.time)
                Text(text = "Participants: " + eventWithUsers.participants.size + "/" + eventWithUsers.event.maxParticipants)
            }
        },
    )
    HorizontalDivider()
}

// TODO: Aggiungere un parametro per la lista di eventi così da usare la funzione NoItemsPlaceholder in altri contesti
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
