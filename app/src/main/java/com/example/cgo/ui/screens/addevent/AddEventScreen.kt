package com.example.cgo.ui.screens.addevent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.cgo.data.database.entities.PrivacyType
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun AddEventScreen(
    state: AddEventState,
    actions: AddEventActions,
    onSubmit: () -> Unit
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

                        LocalDate.parse(
                            state.date,
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        ) < LocalDate.now() -> {
                            snackbarMessage = "Date cannot be in the past"
                            showSnackbar = true
                        }

                        state.time.isBlank() -> {
                            snackbarMessage = "Please select a time"
                            showSnackbar = true
                        }

                        (LocalDate.parse(
                            state.date,
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        ) == LocalDate.now()) && (LocalTime.parse(state.time) <= LocalTime.now()) -> {
                            snackbarMessage = "Time cannot be in the past"
                            showSnackbar = true
                        }

                        state.address.isBlank() -> {
                            snackbarMessage = "Please enter an address"
                            showSnackbar = true
                        }

                        state.city.isBlank() -> {
                            snackbarMessage = "Please enter a city"
                            showSnackbar = true
                        }

                        state.maxParticipants <= 1 -> {
                            snackbarMessage = "Participants must be more than 1"
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
            }) {
                Icon(Icons.Outlined.Check, "Add Event")
            }
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(NavigationBarDefaults.windowInsets)
    ) { contentPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(
                    top = 0.dp,
                    bottom = 0.dp,
                    start = contentPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = contentPadding.calculateEndPadding(LayoutDirection.Rtl)
                )
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CustomDatePicker(
                    date = state.date,
                    actions = actions,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                )
                CustomTimePicker(
                    time = state.time,
                    actions = actions,
                    modifier = Modifier
                        .weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = state.address,
                    onValueChange = actions::setAddress,
                    label = { Text("Address") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                OutlinedTextField(
                    value = state.city,
                    onValueChange = actions::setCity,
                    label = { Text("City") },
                    modifier = Modifier
                        .weight(1f),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
            }
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
fun CustomTimePicker(
    time: String,
    actions: AddEventActions,
    modifier: Modifier = Modifier
) {
    val timeState = rememberTimePickerState()
    var isTimePickerVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = time,
        onValueChange = {},
        label = { Text("Time") },
        modifier = modifier
            .clickable { isTimePickerVisible = false },
        readOnly = true,
        interactionSource = remember { MutableInteractionSource() }
            .also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect { interaction ->
                        if (interaction is PressInteraction.Release) {
                            isTimePickerVisible = !isTimePickerVisible
                        }
                    }
                }
            },
    )
    if (isTimePickerVisible) {
        TimePickerDialog(
            onConfirm = {
                actions.setTime(
                    timeState.hour.toString().padStart(2, '0') +
                            ":" +
                            timeState.minute.toString().padStart(2, '0')
                )
                isTimePickerVisible = false
            },
            onCancel = { isTimePickerVisible = false },
        ) {
            TimePicker(
                state = timeState,
            )
        }
    }
}

@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    toggle()
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = onCancel
                    ) { Text("Cancel") }
                    TextButton(
                        onClick = onConfirm
                    ) { Text("Confirm") }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePicker(
    date: String,
    actions: AddEventActions,
    modifier: Modifier = Modifier
) {
    val dateState = rememberDatePickerState(initialDisplayMode = DisplayMode.Picker)
    var isDatePickerVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = date,
        onValueChange = {},
        label = { Text("Date") },
        modifier = modifier
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
                TextButton(onClick = { isDatePickerVisible = false }) {
                    Text("Cancel")
                }
                TextButton(onClick = {
                    if (dateState.selectedDateMillis == null)
                        return@TextButton
                    actions.setDate(
                        LocalDate.ofEpochDay(dateState.selectedDateMillis!! / 86400000)
                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    )
                    isDatePickerVisible = false
                }) {
                    Text("Confirm")
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
    Box {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
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
}