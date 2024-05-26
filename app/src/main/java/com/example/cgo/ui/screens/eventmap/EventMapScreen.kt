package com.example.cgo.ui.screens.eventmap

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.cgo.ui.controllers.EventsState
import com.example.cgo.utils.LocationService
import org.koin.compose.koinInject
import org.osmdroid.config.Configuration
import org.osmdroid.config.IConfigurationProvider
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun EventMapScreen(
    eventsState: EventsState,
) {
    val context = LocalContext.current
    val locationService = koinInject<LocationService>()

    var lat by remember { mutableDoubleStateOf(41.0) }
    var lon by remember { mutableDoubleStateOf(12.0) }
    var mapView by remember { mutableStateOf<MapView?>(null) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            locationService.requestCurrentLocation()
        } else {
            // Handle the case where the user denies the permission request

        }
    }

    LaunchedEffect(Unit) {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                locationService.requestCurrentLocation()
            }

            else -> {
                // You can directly ask for the permission.
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    LaunchedEffect(locationService.coordinates, locationService.isLocationEnabled) {
        if (locationService.isLocationEnabled == true) {
            locationService.coordinates?.let {
                lat = it.latitude
                lon = it.longitude
                mapView?.controller?.setCenter(GeoPoint(lat, lon))

                val myPosition = Marker(mapView).apply {
                    position = GeoPoint(lat, lon)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "Your position"
                }
                mapView?.overlays?.add(myPosition)
            }
        } else {
            mapView?.overlays?.removeIf { it is Marker }
        }
        // TODO: Add markers for events
//        eventsState.events.forEach { event ->
//            val eventMarker = Marker(mapView).apply {
//                position = GeoPoint(event.latitude, event.longitude)
//                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
//                title = event.title
//            }
//        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AndroidView(
            factory = {
                // Set the user agent
                val osmConf: IConfigurationProvider = Configuration.getInstance()
                osmConf.userAgentValue = context.packageName

                MapView(context).apply {
                    setMultiTouchControls(true)
                    controller.setZoom(15.0)
                    mapView = this
                }
            },
            update = { view ->
                view.controller.setCenter(GeoPoint(lat, lon))
            }
        )
        FloatingActionButton(
            onClick = {
                mapView?.controller?.animateTo(GeoPoint(lat, lon))
                mapView?.controller?.setZoom(15.0)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.LocationOn, contentDescription = "Center Map on User Location")
        }
    }
}