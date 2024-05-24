package com.example.cgo.ui.screens.rankings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.cgo.data.database.entities.User
import com.example.cgo.data.database.entities.UserWithEvents
import com.example.cgo.ui.OCGRoute
import com.example.cgo.ui.theme.Bronze
import com.example.cgo.ui.theme.Gold
import com.example.cgo.ui.theme.Silver

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
        var count = 1
        items(state.users) {userWithEvents: UserWithEvents ->
            ListItem(
                modifier = Modifier.clickable(onClick = {
                    navController.navigate(OCGRoute.Profile.buildRoute(userWithEvents.user.userId))
                }),
                headlineContent = {
                    fun Modifier.circleLayout() = layout { measurable, constraints ->
                        // Measure the composable
                        val placeable = measurable.measure(constraints)

                        //get the current max dimension to assign width=height
                        val currentHeight = placeable.height
                        val currentWidth = placeable.width
                        val newDiameter = maxOf(currentHeight, currentWidth)

                        //assign the dimension and the center position
                        layout(newDiameter, newDiameter) {
                            // Where the composable gets placed
                            placeable.placeRelative(
                                (newDiameter - currentWidth) / 2,
                                (newDiameter - currentHeight) / 2
                            )
                        }
                    }
                    var textSize = 0
                    if (count > 10)
                        textSize = 12
                    if (count > 100)
                        textSize = 9
                    Row {
                        when (count) {
                            1 -> Text(text = count.toString(), modifier = Modifier.background(color = Gold, shape = CircleShape).circleLayout(), color = Color.Black)
                            2 -> Text(text = count.toString(), modifier = Modifier.background(color = Silver, shape = CircleShape).circleLayout(), color = Color.Black)
                            3 -> Text(text = count.toString(), modifier = Modifier.background(color = Bronze, shape = CircleShape).circleLayout(), color = Color.Black)
                            else -> Text(
                                text = (count + 100).toString(),
                                modifier = Modifier
                                    .border(width = 2.dp, color = MaterialTheme.colorScheme.secondary, shape = CircleShape)
                                    .background(color = MaterialTheme.colorScheme.background, shape = CircleShape)
                                    .circleLayout(),
                                fontSize = textSize.sp
                            )
                        }
                        Text(text = userWithEvents.user.username, modifier = Modifier.padding(start = 10.dp))
                    }
                },
                trailingContent = { Text(text = "Events hosted: " + userWithEvents.events.size.toString()) }
            )
            count++
            HorizontalDivider()
        }
    }
}