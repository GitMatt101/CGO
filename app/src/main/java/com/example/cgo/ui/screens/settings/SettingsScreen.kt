package com.example.cgo.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cgo.ui.CGORoute
import com.example.cgo.ui.controllers.AppState
import com.example.cgo.ui.theme.Theme

@Composable
fun SettingsScreen(
    state: AppState,
    navController: NavHostController,
    changeTheme: (Theme) -> Unit
) {
    Column (modifier = Modifier.selectableGroup()) {
        DropDown(state = state, navController = navController, changeTheme = changeTheme)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDown(
    state: AppState,
    navController: NavHostController,
    changeTheme: (Theme) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "App Theme")
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = !isExpanded }
        ) {
            TextField(
                modifier = Modifier.menuAnchor(),
                value = state.theme.toString(),
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) }
            )
            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {
                Theme.entries.forEach { theme: Theme ->
                    DropdownMenuItem(
                        text = { Text(text = theme.toString()) },
                        onClick = {
                            changeTheme(theme)
                            isExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(Modifier.size(10.dp))
        Button(onClick = { navController.navigate(CGORoute.EditProfile.route) }) {
            Text(text = "Edit Profile")
        }
    }
}