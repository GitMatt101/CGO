package com.example.cgo.ui.screens.rankings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.cgo.data.database.entities.User
import com.example.cgo.data.database.entities.UserWithEvents
import com.example.cgo.ui.OCGRoute
import com.example.cgo.ui.theme.Bronze
import com.example.cgo.ui.theme.Gold
import com.example.cgo.ui.theme.Silver
import kotlinx.coroutines.launch

@Composable
fun RankingsScreen(
    usersWithEvents: List<UserWithEvents>,
    navController: NavHostController
) {
    TabLayout(usersWithEvents = usersWithEvents, navController = navController)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabLayout(usersWithEvents: List<UserWithEvents>, navController: NavHostController) {
    val pagerState = rememberPagerState(pageCount = { 3 })

    Column {
        Tabs(pagerState = pagerState)
        TabsContent(pagerState = pagerState, usersWithEvents = usersWithEvents, navController = navController)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Tabs(pagerState: PagerState) {
    val list = listOf("Events played", "Events won", "Events hosted")
    val scope = rememberCoroutineScope()
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        indicator = { tabPositions ->
            SecondaryIndicator(Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]))
        }
    ) {
        list.forEachIndexed { index, _ ->
            Tab(
                text = {
                    Text(
                        list[index],
                        fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal
                    )
                },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabsContent(pagerState: PagerState, usersWithEvents: List<UserWithEvents>, navController: NavHostController) {
    HorizontalPager(state = pagerState) { page ->
        when (page) {
            0 -> TabContentScreen(
                navController = navController,
                users = usersWithEvents.map { Pair(it.user, it.events.size) }.sortedBy { -it.second },
                text = "Participations"
            )
            1 -> TabContentScreen(
                navController = navController,
                users = usersWithEvents.map { Pair(it.user, it.user.gamesWon) }.sortedBy { -it.second },
                text = "Events Won"
            )
            2 -> TabContentScreen(
                navController = navController,
                users = usersWithEvents.map { Pair(it.user, it.createdEvents.size) }.sortedBy { -it.second },
                text = "Events Hosted"
            )
        }
    }
}

@Composable
fun TabContentScreen(navController: NavHostController, users: List<Pair<User, Int>>, text: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        LazyColumn {
            var count = 1
            items(users) {userWithNumber ->
                val user: User = userWithNumber.first
                ListItem(
                    modifier = Modifier.clickable(onClick = {
                        navController.navigate(OCGRoute.Profile.buildRoute(user.userId))
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
                        var textSize = 15
                        if (count > 10)
                            textSize = 12
                        if (count > 100)
                            textSize = 9
                        Row {
                            when (count) {
                                1 -> Text(text = count.toString(), modifier = Modifier
                                    .background(color = Gold, shape = CircleShape)
                                    .circleLayout(), color = Color.Black)
                                2 -> Text(text = count.toString(), modifier = Modifier
                                    .background(color = Silver, shape = CircleShape)
                                    .circleLayout(), color = Color.Black)
                                3 -> Text(text = count.toString(), modifier = Modifier
                                    .background(color = Bronze, shape = CircleShape)
                                    .circleLayout(), color = Color.Black)
                                else -> Text(
                                    text = count.toString(),
                                    modifier = Modifier
                                        .border(
                                            width = 2.dp,
                                            color = MaterialTheme.colorScheme.secondary,
                                            shape = CircleShape
                                        )
                                        .background(
                                            color = MaterialTheme.colorScheme.background,
                                            shape = CircleShape
                                        )
                                        .circleLayout(),
                                    fontSize = textSize.sp
                                )
                            }
                            Text(text = user.username, modifier = Modifier.padding(start = 10.dp))
                        }
                    },
                    trailingContent = { Text(text = "$text: ${userWithNumber.second}") }
                )
                count++
                HorizontalDivider()
            }
        }
    }
}