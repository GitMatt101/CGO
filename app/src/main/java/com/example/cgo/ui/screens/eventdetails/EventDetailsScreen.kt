package com.example.cgo.ui.screens.eventdetails

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import com.example.cgo.data.database.entities.EventWithUsers
import com.example.cgo.data.database.entities.User
import com.example.cgo.ui.OCGRoute
import com.example.cgo.ui.composables.ImageWithPlaceholder
import com.example.cgo.ui.composables.Size

@Composable
fun EventDetailsScreen(
    eventWithUsers: EventWithUsers,
    navController: NavHostController,
    onSubscription: (Int) -> Unit
) {
    val context = LocalContext.current

    @SuppressLint("QueryPermissionsNeeded")
    fun shareEventDetails() {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, eventWithUsers.event.title)
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
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(it)
                .padding(12.dp)
                .fillMaxSize()
        ) {
            Spacer(Modifier.size(16.dp))
            // TODO: Aggiungere la pfp dello user usando UserWithEvents
//            val imageUri = Uri.parse(user.profilePicture)
//            ImageWithPlaceholder(imageUri, Size.Large)
//            Spacer(Modifier.size(16.dp))
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
            Spacer(Modifier.size(8.dp))
            ParticipantsList(users = eventWithUsers.participants, navController = navController)
            Spacer(Modifier.size(8.dp))
            // TODO: Aggiungere la mappa con la location dell'evento
            Button(onClick = { onSubscription(eventWithUsers.event.eventId) }) {
                Text(text = "Participate")
            }
        }
    }
}

@Composable
fun ParticipantsList(users: List<User>, navController: NavHostController) {
    Column (
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .padding(bottom = 10.dp)
    ) {
        Text(text = "Participants", fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.size(10.dp))
        if (users.isNotEmpty()) {
            LazyColumn (
                modifier = Modifier.border(width = 2.dp, color = Color.DarkGray)
            ) {
                items(users) {user: User ->
                    ListItem(
                        modifier = Modifier.clickable(onClick = { navController.navigate(OCGRoute.Profile.buildRoute(user.userId)) }),
                        headlineContent = {
                            Row {
                                ImageWithPlaceholder(uri = user.profilePicture?.toUri(), size = Size.VerySmall)
                                Text(
                                    modifier = Modifier
                                        .padding(start = 10.dp)
                                        .align(Alignment.CenterVertically),
                                    text = user.username
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
