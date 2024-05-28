package com.example.cgo.ui.screens.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import com.example.cgo.ui.OCGRoute
import com.example.cgo.ui.composables.ImageWithPlaceholder
import com.example.cgo.ui.composables.Size
import com.example.cgo.ui.controllers.EventsState
import com.example.cgo.ui.controllers.UsersState
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    eventsState: EventsState,
    usersState: UsersState,
    navController: NavHostController
) {
    TabLayout(usersState = usersState, eventsState = eventsState, navController = navController)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabLayout(usersState: UsersState, eventsState: EventsState, navController: NavHostController) {
    val pagerState = rememberPagerState(pageCount = { 2 })

    Column {
        Tabs(pagerState = pagerState)
        TabsContent(
            pagerState = pagerState,
            usersState = usersState,
            eventsState = eventsState,
            navController = navController
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Tabs(pagerState: PagerState) {
    val list = listOf("Events", "Users")
    val scope = rememberCoroutineScope()
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]))
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
fun TabsContent(
    pagerState: PagerState,
    usersState: UsersState,
    eventsState: EventsState,
    navController: NavHostController
) {
    HorizontalPager(state = pagerState) { page ->
        when (page) {
            0 -> TabContentEvents(
                navController = navController,
                eventsState = eventsState,
            )

            1 -> TabContentUsers(
                navController = navController,
                usersState = usersState,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabContentEvents(
    navController: NavHostController,
    eventsState: EventsState,
) {
    var text by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    Box(
        Modifier
            .fillMaxSize()
            .semantics { isTraversalGroup = true },
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = -1f }
                .padding(5.dp),
            windowInsets = SearchBarDefaults.windowInsets.exclude(SearchBarDefaults.windowInsets),
            query = text,
            onQueryChange = { text = it },
            onSearch = { expanded = false },
            active = expanded,
            onActiveChange = { expanded = it },
            placeholder = { Text("Search events") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        ) {
            if (expanded) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filteredEvents = eventsState.eventsWithUsers.filter { eventWithUsers ->
                        eventWithUsers.event.title.contains(text, ignoreCase = true)
                    }
                    items(filteredEvents) { eventWithUsers ->
                        ListItem(
                            modifier = Modifier.clickable {
                                navController.navigate(
                                    OCGRoute.EventDetails.buildRoute(
                                        eventWithUsers.event.eventId
                                    )
                                )
                            },
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
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabContentUsers(
    navController: NavHostController,
    usersState: UsersState,
)  {
    var text by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    Box(
        Modifier
            .fillMaxSize()
            .semantics { isTraversalGroup = true }
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = -1f }
                .padding(5.dp),
            windowInsets = SearchBarDefaults.windowInsets.exclude(SearchBarDefaults.windowInsets),
            query = text,
            onQueryChange = { text = it },
            onSearch = { expanded = false },
            active = expanded,
            onActiveChange = { expanded = it },
            placeholder = { Text("Search users") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        ) {
            if (expanded) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filteredUsers = usersState.users.filter { users ->
                        users.username.contains(text, ignoreCase = true)
                    }
                    items(filteredUsers) { users ->
                        ListItem(
                            modifier = Modifier.clickable {
                                navController.navigate(
                                    OCGRoute.Profile.buildRoute(
                                        users.userId
                                    )
                                )
                            },
                            headlineContent = { Text(text = users.username) },
                            leadingContent = { ImageWithPlaceholder(uri = users.profilePicture?.toUri(), size = Size.VerySmall)},
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}