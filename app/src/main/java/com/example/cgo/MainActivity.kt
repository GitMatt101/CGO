package com.example.cgo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cgo.ui.OCGNavGraph
import com.example.cgo.ui.CGORoute
import com.example.cgo.ui.composables.AppBar
import com.example.cgo.ui.composables.MenuBar
import com.example.cgo.ui.controllers.AppViewModel
import com.example.cgo.ui.theme.CGOTheme
import com.example.cgo.ui.theme.Theme
import com.example.cgo.utils.LocationService
import org.koin.android.ext.android.get
import org.koin.androidx.compose.koinViewModel

@SuppressLint("StaticFieldLeak")
private lateinit var locationService: LocationService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        locationService = get<LocationService>()
        setContent {
            val settingsViewModel = koinViewModel<AppViewModel>()
            val state by settingsViewModel.state.collectAsStateWithLifecycle()

            CGOTheme(
                darkTheme = when (state.theme) {
                    Theme.Light -> false
                    Theme.Dark -> true
                    Theme.System -> isSystemInDarkTheme()
                }
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val backStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute by remember {
                        derivedStateOf {
                            CGORoute.routes.find {
                                it.route == backStackEntry?.destination?.route
                            } ?: CGORoute.Login
                        }
                    }

                    if (currentRoute.route != CGORoute.Login.route && currentRoute.route != CGORoute.Registration.route) {
                        Scaffold(
                            topBar = { AppBar(navController, currentRoute) },
                            bottomBar = { MenuBar(navController) }
                        ) { contentPadding ->
                            OCGNavGraph(
                                navController,
                                modifier = Modifier.padding(contentPadding)
                            )
                        }
                    } else {
                        Scaffold(
                            topBar = { AppBar(navController, currentRoute) }
                        ) { contentPadding ->
                            OCGNavGraph(
                                navController,
                                modifier = Modifier.padding(contentPadding)
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        locationService.pauseLocationRequest()
    }

    override fun onResume() {
        super.onResume()
        locationService.resumeLocationRequest()
    }
}
