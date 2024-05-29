package com.example.cgo.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.exclude
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
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cgo.data.database.entities.EventWithUsers
import com.example.cgo.data.database.entities.PrivacyType
import com.example.cgo.ui.OCGRoute
import com.example.cgo.ui.composables.NoItemPlaceholder
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
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(NavigationBarDefaults.windowInsets)
    )
    { contentPadding ->
        if (state.events.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.padding(
                    top = 0.dp,
                    bottom = 0.dp,
                    start = contentPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = contentPadding.calculateEndPadding(LayoutDirection.Rtl)
                )
            ) {
                items(state.eventsWithUsers) { eventWithUsers ->
                    if (eventWithUsers.event.privacyType == PrivacyType.PUBLIC) {
                        EventItem(
                            eventWithUsers,
                            onClick = {
                                navController.navigate(
                                    OCGRoute.EventDetails.buildRoute(
                                        eventWithUsers.event.eventId
                                    )
                                )
                            }
                        )
                    }
                }
            }
        } else {
            NoItemPlaceholder("No events found", "Tap the button to view the map of events")
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
                Text(text = "Time: " + eventWithUsers.event.time)
            }
        },
        trailingContent = {
            Column {
                Text(text = "Participants: " + eventWithUsers.participants.size + "/" + eventWithUsers.event.maxParticipants)
            }
        },
    )
    HorizontalDivider()
}