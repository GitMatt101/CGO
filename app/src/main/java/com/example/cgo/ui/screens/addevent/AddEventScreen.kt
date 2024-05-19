package com.example.cgo.ui.screens.addevent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cgo.data.database.entities.PrivacyType

@Composable
fun AddEventScreen(
    state: AddEventState,
    actions: AddEventActions,
    onSubmit: () -> Unit,
    navController: NavHostController
) {
    val snackbarHostState = remember { SnackbarHostState() }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(containerColor = MaterialTheme.colorScheme.primary, onClick = {
                if (!state.canSubmit) return@FloatingActionButton
                onSubmit()
                navController.navigateUp()
            }) {
                Icon(Icons.Outlined.Check, "Add Event")
            }
        },
    ) { contentPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(contentPadding)
                .padding(12.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = state.title,
                onValueChange = actions::setTitle,
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            OutlinedTextField(
                value = state.description,
                onValueChange = actions::setDescription,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default)
            )
            // TODO: Add date picker
            OutlinedTextField(
                value = state.date,
                onValueChange = actions::setDate,
                label = { Text("Date") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            // TODO: Add time picker
            OutlinedTextField(
                value = state.time,
                onValueChange = actions::setTime,
                label = { Text("Time") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            // TODO: Add location picker
            OutlinedTextField(
                value = state.location,
                onValueChange = actions::setLocation,
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            OutlinedTextField(
                value = if (state.maxParticipants == 0) "" else state.maxParticipants.toString(),
                onValueChange = { actions.setMaxParticipants(it.toIntOrNull() ?: 0) },
                label = { Text("Max Participants") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )
            PrivacyTypePicker(
                privacyType = state.privacyType,
                onPrivacyTypeChange = actions::setPrivacyType
            )
        }
    }
}

@Composable
fun PrivacyTypePicker(
    privacyType: PrivacyType,
    onPrivacyTypeChange: (PrivacyType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = privacyType.name,
        onValueChange = {},
        label = { Text("Privacy Type") },
        modifier = Modifier
            .fillMaxWidth(),
        readOnly = true,
        // Handle dropdown menu expansion on click
        interactionSource = remember { MutableInteractionSource() }
            .also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect { interaction ->
                        if (interaction is PressInteraction.Release) {
                            expanded = true
                        }
                    }
                }
            },
        trailingIcon = {
            IconButton(onClick = { expanded = true }) {
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = "Select Privacy Type"
                )
            }
        }
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier.fillMaxWidth()
    ) {
        PrivacyType.entries.forEach { type ->
            DropdownMenuItem(
                text = { Text(type.name) },
                onClick = {
                    onPrivacyTypeChange(type)
                    expanded = false
                })
        }
    }
}
