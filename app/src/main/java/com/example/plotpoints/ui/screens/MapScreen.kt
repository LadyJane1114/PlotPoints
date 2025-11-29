package com.example.plotpoints.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.annotation.Marker
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.search.autocomplete.PlaceAutocompleteResult


@Composable
fun MapScreen(selectedPlace: PlaceAutocompleteResult?) {


    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(7.0)
            center(Point.fromLngLat(-63.35, 44.65))
            pitch(0.0)
            bearing(0.0)
        }
    }

    LaunchedEffect(selectedPlace) {
        selectedPlace?.let { place ->
            mapViewportState.easeTo(
                cameraOptions = CameraOptions.Builder()
                    .center(place.coordinate) // <-- pass the Point, not the whole result
                    .zoom(15.0)
                    .build()
            )
        }
    }
    MapboxMap(
        Modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
    ) {
        selectedPlace?.let { place ->
        Marker(
            point = place.coordinate,
            color = MaterialTheme.colorScheme.tertiary,
            stroke = MaterialTheme.colorScheme.onTertiary,
            innerColor = MaterialTheme.colorScheme.background
        )
    }
        MapEffect(Unit) { mapView ->
            mapView.location.updateSettings {
                locationPuck = createDefault2DPuck(withBearing = true)
                enabled = true
                puckBearing = PuckBearing.COURSE
                puckBearingEnabled = true
            }
            mapViewportState.transitionToFollowPuckState()
        }
    }
}
