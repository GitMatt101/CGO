package com.example.cgo.ui.screens.profile

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import com.example.cgo.data.database.entities.Event
import com.example.cgo.data.database.entities.User
import com.example.cgo.ui.OCGRoute
import com.example.cgo.ui.composables.ImageWithPlaceholder
import com.example.cgo.ui.composables.Size
import com.example.cgo.ui.screens.home.NoItemsPlaceholder

@Composable
fun ProfileScreen(
    user: User,
    events: List<Event>,
    navController: NavHostController
) {
    Column (
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.size(10.dp))
        ImageWithPlaceholder(uri = user.profilePicture?.toUri(), size = Size.Large)
        Spacer(Modifier.size(10.dp))
        Text(text = user.username)
        Spacer(Modifier.size(10.dp))
        // TODO: Fix games won
        Text(text = "Games won: " + user.gamesWon)
        HorizontalDivider()
        MatchHistory(events = events, navController = navController)
    }
}

@Composable
fun MatchHistory(
    events: List<Event>,
    navController: NavHostController
) {
    Column (
        modifier = Modifier.padding(horizontal = 10.dp).padding(bottom = 10.dp)
    ) {
        Text(text = "Match History", fontWeight = FontWeight.Bold)
        Spacer(Modifier.size(10.dp))
        if (events.isNotEmpty()) {
            LazyColumn (
                modifier = Modifier.border(width = 2.dp, color = Color.DarkGray)
            ) {
                items(events) { event ->
                    EventItem(
                        event,
                        onClick = { navController.navigate(OCGRoute.EventDetails.buildRoute(event.eventId)) }
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
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = { Text(text = event.title) },
        supportingContent = {
            Text(text = event.date)
        },
        trailingContent = {
            // TODO: add "Win" or "Loss" if the player won or lost
            Text(text = "Win")
        },
    )
    HorizontalDivider()
}