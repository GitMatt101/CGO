package com.example.cgo.ui.screens.rankings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.cgo.data.database.entities.User
import com.example.cgo.data.database.entities.UserWithEvents
import com.example.cgo.ui.OCGRoute

@Composable
fun RankingsScreen(
    state: RankingsState,
    actions: RankingsActions,
    navController: NavHostController
) {
    when (state.filter) {
        Filter.EventsPlayed -> emptyList<User>()
        Filter.EventsWon -> emptyList<User>()
        Filter.EventsHosted -> actions.LoadUsersByEventsHosted()
    }
    LazyColumn (
        modifier = Modifier
    ) {
        items(state.users) {userWithEvents: UserWithEvents ->
            ListItem(
                modifier = Modifier.clickable(onClick = {
                    navController.navigate(OCGRoute.Profile.buildRoute(userWithEvents.user.userId))
                }),
                headlineContent = { Text(text =  userWithEvents.user.username) },
                trailingContent = { Text(text = userWithEvents.events.size.toString()) }
            )
            HorizontalDivider()
        }
    }
}