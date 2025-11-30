package com.example.plotpoints.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.plotpoints.bookmarksDB.BookmarkPlace
import com.example.plotpoints.bookmarksDB.DBProvider
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.material3.ButtonDefaults

fun PlaceAutocompleteResult.toBookmarkPlace() = BookmarkPlace(
    mapboxID = this.id,
    name = this.name,
    address = this.address?.formattedAddress,
    latitude = this.coordinate.latitude(),
    longitude = this.coordinate.longitude(),
    makiIcon = this.makiIcon,
    distanceMeters = this.distanceMeters,
    etaMinutes = this.etaMinutes,
)

@Composable
fun MapScreen(selectedPlace: PlaceAutocompleteResult?,
              onFavoriteToggle: (PlaceAutocompleteResult) -> Unit = {},
              onNavigateClick: (PlaceAutocompleteResult) -> Unit = {}
) {


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
                    .center(place.coordinate)
                    .zoom(17.0)
                    .build()
            )
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        
        MapboxMap(
            modifier = Modifier.fillMaxSize(),
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


        val dao = DBProvider.getDatabase(LocalContext.current).bookmarkPlaceDao()
        val isBookmarked = remember { mutableStateOf(false) }

        LaunchedEffect(selectedPlace?.mapboxId) {
            selectedPlace?.let {
                isBookmarked.value = dao.isBookmarked(it.mapboxId)
            }
        }

        if (selectedPlace != null) {
            PlaceCard(
                place = selectedPlace,
                isBookmarked = isBookmarked.value,
                onFavoriteToggle = { CoroutineScope(Dispatchers.IO).launch {
                    selectedPlace?.let { place ->
                        if (isBookmarked.value) {
                            dao.removeBookmark(place.toBookmarkPlace())
                        } else {
                            dao.addBookmark(place.toBookmarkPlace())
                        }
                        isBookmarked.value = !isBookmarked.value
                    }
                } },
//                onNavigateClick = { onNavigateClick(selectedPlace) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun PlaceCard(
    place: PlaceAutocompleteResult,
    isBookmarked: Boolean,
    onFavoriteToggle: () -> Unit,
//    onNavigateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Text(
                text = place.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(6.dp))
            place.address?.formattedAddress?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onFavoriteToggle,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isBookmarked) MaterialTheme.colorScheme.secondary
                                            else MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary)
                    ) {
                    Text(
                        if (isBookmarked) "Remove from Bookmarks" else "Add to Bookmarks")
                }

//                OutlinedButton(onClick = onNavigateClick) {
//                    Text("Show me the Way!")
//                }
            }
        }
    }
}

