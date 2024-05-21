package com.example.cgo.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.cgo.ui.theme.Theme

@Composable
fun SettingsScreen(
    state: SettingsState,
    changeTheme: (Theme) -> Unit
) {
    Column (modifier = Modifier.selectableGroup()) {
        Theme.entries.forEach { theme: Theme ->  
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (theme == state.theme),
                        onClick = { changeTheme(theme) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (theme == state.theme),
                    onClick = null
                )
                Text(text = theme.toString(), modifier = Modifier.padding(start = 16.dp))
            }
        }
    }
}