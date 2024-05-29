package com.example.cgo.ui.screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cgo.data.database.entities.EventWithUsers
import com.example.cgo.ui.OCGRoute
import com.example.cgo.ui.composables.NoItemPlaceholder
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    publicEvents: List<EventWithUsers>,
    createdEvents: List<EventWithUsers>,
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
    ) { contentPadding ->
        TabLayout(
            contentPadding = contentPadding,
            publicEvents = publicEvents,
            createdEvents = createdEvents,
            navController = navController,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabLayout(
    contentPadding: PaddingValues,
    publicEvents: List<EventWithUsers>,
    createdEvents: List<EventWithUsers>,
    navController: NavHostController,
) {
    val pagerState = rememberPagerState(pageCount = { 2 })

    Column(
        modifier = Modifier
            .padding(
                top = 0.dp,
                bottom = 0.dp,
                start = contentPadding.calculateStartPadding(LayoutDirection.Ltr),
                end = contentPadding.calculateEndPadding(LayoutDirection.Rtl)
            )
            .fillMaxSize()
    ) {
        Tabs(pagerState = pagerState)
        TabsContent(
            contentPadding = contentPadding,
            pagerState = pagerState,
            publicEvents = publicEvents,
            createdEvents = createdEvents,
            navController = navController,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Tabs(pagerState: PagerState) {
    val list = listOf("Public events", "Your events")
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
    publicEvents: List<EventWithUsers>,
    createdEvents: List<EventWithUsers>,
    navController: NavHostController,
    contentPadding: PaddingValues,
) {
    HorizontalPager(state = pagerState) { page ->
        when (page) {
            0 -> TabContent(
                navController = navController,
                events = publicEvents,
                contentPadding = contentPadding,
            )

            1 -> TabContent(
                navController = navController,
                events = createdEvents,
                contentPadding = contentPadding,
            )
        }
    }
}

@Composable
fun TabContent(
    navController: NavHostController,
    events: List<EventWithUsers>,
    contentPadding: PaddingValues,
) {
    Box(
        Modifier
            .fillMaxSize()
            .semantics { isTraversalGroup = true },
    ) {
        if (events.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.padding(
                    top = 0.dp,
                    bottom = 0.dp,
                    start = contentPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = contentPadding.calculateEndPadding(LayoutDirection.Rtl)
                )
            ) {
                items(events) { event ->
                    EventItem(
                        event,
                        onClick = {
                            navController.navigate(
                                OCGRoute.EventDetails.buildRoute(
                                    event.event.eventId
                                )
                            )
                        }
                    )
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