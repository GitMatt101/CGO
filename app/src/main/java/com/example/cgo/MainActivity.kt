package com.example.cgo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cgo.ui.OCGNavGraph
import com.example.cgo.ui.OCGRoute
import com.example.cgo.ui.composables.AppBar
import com.example.cgo.ui.composables.MenuBar
import com.example.cgo.ui.theme.CGOTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CGOTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val backStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute by remember {
                        derivedStateOf {
                            OCGRoute.routes.find {
                                it.route == backStackEntry?.destination?.route
                            } ?: OCGRoute.Login
                        }
                    }

                    if (currentRoute.route != OCGRoute.Login.route && currentRoute.route != OCGRoute.Registration.route) {
                        Scaffold(
                            modifier = Modifier.padding(0.dp),
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
                            modifier = Modifier.padding(0.dp),
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
}
