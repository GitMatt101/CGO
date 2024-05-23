package com.example.cgo.ui.screens.addevent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cgo.data.database.entities.PrivacyType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AddEventScreen(
    state: AddEventState,
    actions: AddEventActions,
    onSubmit: () -> Unit,
    navController: NavHostController
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(containerColor = MaterialTheme.colorScheme.primary, onClick = {
                if (!state.canSubmit) {
                    when {
                        state.title.isBlank() -> {
                            snackbarMessage = "Title cannot be empty"
                            showSnackbar = true
                        }

                        state.date.isBlank() -> {
                            snackbarMessage = "Please select a date"
                            showSnackbar = true
                        }

                        state.time.isBlank() -> {
                            snackbarMessage = "Please select a time"
                            showSnackbar = true
                        }

                        state.location.isBlank() -> {
                            snackbarMessage = "Please enter a location"
                            showSnackbar = true
                        }

                        state.maxParticipants <= 0 -> {
                            snackbarMessage = "Participants must be more than 0"
                            showSnackbar = true
                        }

                        state.privacyType == PrivacyType.NONE -> {
                            snackbarMessage = "Privacy type cannot be NONE"
                            showSnackbar = true
                        }
                    }
                    return@FloatingActionButton
                }
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
                .verticalScroll(rememberScrollState())
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
            CustomDatePicker(
                date = state.date,
                actions = actions
            )
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
    if (showSnackbar) {
        LaunchedEffect(snackbarHostState) {
            snackbarHostState.showSnackbar(
                message = snackbarMessage,
                actionLabel = "Dismiss",
                duration = SnackbarDuration.Short
            )
            showSnackbar = false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePicker(
    date: String,
    actions: AddEventActions
) {
    val dateState = rememberDatePickerState(initialDisplayMode = DisplayMode.Picker)
    var isDatePickerVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = dateState.selectedDateMillis?.let {
            LocalDate.ofEpochDay(it / 86400000).format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
            )
        }
            ?: date,
        onValueChange = {},
        label = { Text("Date") },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { isDatePickerVisible = false },
        readOnly = true,
        interactionSource = remember { MutableInteractionSource() }
            .also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect { interaction ->
                        if (interaction is PressInteraction.Release) {
                            isDatePickerVisible = !isDatePickerVisible
                        }
                    }
                }
            },
    )
    if (isDatePickerVisible) {
        DatePickerDialog(
            onDismissRequest = { isDatePickerVisible = false },
            confirmButton = {
                IconButton(onClick = {
                    actions.setDate(
                        LocalDate.ofEpochDay(dateState.selectedDateMillis!! / 86400000)
                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    )
                    isDatePickerVisible = false
                }) {
                    Icon(Icons.Outlined.Check, "Confirm")
                }
            },
            content = {
                DatePicker(
                    state = dateState,
                )
            }
        )
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
