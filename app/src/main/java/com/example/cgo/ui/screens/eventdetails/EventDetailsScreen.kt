package com.example.cgo.ui.screens.eventdetails

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import com.example.cgo.R
import com.example.cgo.data.database.entities.Event
import com.example.cgo.data.database.entities.EventWithUsers
import com.example.cgo.data.database.entities.User
import com.example.cgo.ui.OCGRoute
import com.example.cgo.ui.composables.ImageWithPlaceholder
import com.example.cgo.ui.composables.Size

@Composable
fun EventDetailsScreen(
    eventWithUsers: EventWithUsers,
    eventCreator: User,
    loggedUserId: Int,
    navController: NavHostController,
    onSubscription: (Int) -> Unit,
    onSubscriptionCanceled: (Int) -> Unit,
    onWinnerSelection: (User, User?) -> Unit,
    loadParticipants: () -> List<User>
) {
    val context = LocalContext.current

    @SuppressLint("QueryPermissionsNeeded")
    fun shareEventDetails() {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                "Partecipa all'evento: "
                        + eventWithUsers.event.title
                        + " inserendo il codice "
                        + eventWithUsers.event.eventId
                        + " su OCG!")
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share event")
        if (shareIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(shareIntent)
        }
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = ::shareEventDetails
            ) {
                Icon(Icons.Outlined.Share, "Share Event")
            }
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(NavigationBarDefaults.windowInsets)
    ) { contentPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(contentPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                eventWithUsers.event.title,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                eventWithUsers.event.description,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                eventWithUsers.event.date,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                eventWithUsers.event.address,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                eventWithUsers.event.city,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodySmall
            )
            var participants by remember { mutableStateOf(eventWithUsers.participants) }
            var isParticipant by remember { mutableStateOf(eventWithUsers.participants.find { participant: User -> participant.userId == loggedUserId } != null) }
            if (isParticipant) {
                Button(onClick = {
                    onSubscriptionCanceled(eventWithUsers.event.eventId)
                    isParticipant = false
                    participants = loadParticipants()
                }) {
                    Text(text = "Cancel Participation")
                }
            } else if (eventWithUsers.event.maxParticipants > eventWithUsers.participants.size) {
                Button (
                    onClick = {
                        onSubscription(eventWithUsers.event.eventId)
                        isParticipant = true
                        participants = loadParticipants()
                    }
                ) {
                    Text(text = "Participate")
                }
            }
            ListItem(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 60.dp),
                headlineContent = { Text(text = "[OWNER]") },
                trailingContent = {
                    Row {
                        ImageWithPlaceholder(uri = eventCreator.profilePicture?.toUri(), size = Size.VerySmall)
                        Text(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .align(Alignment.CenterVertically),
                            text = eventCreator.username
                        )
                    }
                }
            )
            HorizontalDivider()
            ParticipantsList (
                event = eventWithUsers.event,
                participants = participants,
                loggedUserId = loggedUserId,
                navController = navController,
                onWinnerSelection = onWinnerSelection
            )
        }
    }
}

@Composable
fun ParticipantsList (
    event: Event,
    participants: List<User>,
    loggedUserId: Int,
    navController: NavHostController,
    onWinnerSelection: (User, User?) -> Unit
) {
    Column (
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .padding(bottom = 10.dp)
    ) {
        Text(text = "Participants (${participants.size}/${event.maxParticipants})", fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.size(10.dp))
        if (participants.isNotEmpty()) {
            Column (
                modifier = Modifier.border(2.dp, Color.Gray)
            ) {
                var winnerId by remember { mutableStateOf(event.winnerId) }
                participants.forEach { user: User ->
                    ListItem(
                        modifier = Modifier.clickable(onClick = {
                            navController.navigate(
                                OCGRoute.Profile.buildRoute(
                                    user.userId
                                )
                            )
                        }),
                        headlineContent = {
                            Row {
                                ImageWithPlaceholder(
                                    uri = user.profilePicture?.toUri(),
                                    size = Size.VerySmall
                                )
                                Text(
                                    modifier = Modifier
                                        .padding(start = 10.dp)
                                        .align(Alignment.CenterVertically),
                                    text = user.username
                                )
                            }
                        },
                        trailingContent = {
                            if (user.userId != winnerId && loggedUserId == event.eventCreatorId) {
                                Button(
                                    modifier = Modifier.align(alignment = Alignment.End),
                                    onClick = {
                                        val previousWinner = participants.find { it.userId == event.winnerId }
                                        onWinnerSelection(user, previousWinner)
                                        winnerId = user.userId
                                    }
                                ) {
                                    Text(text = "Select Winner", fontSize = 15.sp)
                                }
                            } else if (user.userId == winnerId) {
                                Icon(
                                    painter = painterResource(id = R.drawable.winner),
                                    contentDescription = "Winner"
                                )
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
