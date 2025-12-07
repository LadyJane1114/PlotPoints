package com.example.plotpoints.ui.screens


import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.plotpoints.MainViewModel
import com.example.plotpoints.navigationObserver
import com.mapbox.maps.extension.compose.style.BooleanValue
import com.mapbox.maps.extension.compose.style.standard.LightPresetValue
import com.mapbox.maps.extension.compose.style.standard.MapboxStandardStyle
import com.mapbox.maps.extension.compose.style.standard.rememberStandardStyleState
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.common.location.Location
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.navigation.base.extensions.coordinates
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.base.route.NavigationRouterCallback
import com.mapbox.navigation.base.route.RouterFailure
import com.mapbox.navigation.base.route.RouterOrigin


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
fun MapScreen(mainViewModel:MainViewModel,
              routeLineApi: MapboxRouteLineApi,
              routeLineView: MapboxRouteLineView,
              selectedPlace: PlaceAutocompleteResult?,
              userLocation: Point?
) {
    val context = LocalContext.current

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(12.0)
            center(Point.fromLngLat(userLocation!!.longitude(), userLocation!!.latitude()))
            pitch(0.0)
            bearing(0.0)
        }
    }
    val mapboxNavigation = MapboxNavigationApp.current()
    val currentLocation = navigationObserver.location
    var mapViewRef: MapView? by remember { mutableStateOf(null) }

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

    fun fetchAndDrawRoute(origin:Point, destination:Point){
        val mapboxNavigation = MapboxNavigationApp.current() ?: return
        val routeOptions = RouteOptions.builder()
            .applyDefaultNavigationOptions()
            .coordinates(origin = origin, destination = destination)
            .alternatives(true)
            .build()

        mapboxNavigation.requestRoutes(routeOptions, object : NavigationRouterCallback {
            override fun onRoutesReady(routes: List<NavigationRoute>, @RouterOrigin routerOrigin: String) {
                routeLineApi.setNavigationRoutes(routes, emptyList()) { drawData ->
                    mapViewRef?.getMapboxMap()?.getStyle()?.apply {
                        routeLineView.renderRouteDrawData(this, drawData)
                    }
                }
                val route = routes.firstOrNull() ?: return
                val polyline = route.directionsRoute.geometry() ?: return
                val routePoints = com.mapbox.geojson.LineString
                    .fromPolyline(polyline, 6)
                    .coordinates()

                val mapboxMap = mapViewRef?.getMapboxMap() ?: return


                val cameraForRoute = mapboxMap.cameraForCoordinates(
                    coordinates = routePoints,
                    camera = CameraOptions.Builder()
                        .padding(com.mapbox.maps.EdgeInsets(100.0, 100.0, 100.0, 100.0))
                        .build(),
                    coordinatesPadding = null,
                    maxZoom = null,
                    offset = null
                )

                // Animate camera to that position
                mapViewportState.easeTo(
                    cameraOptions = cameraForRoute
                )
            }

            override fun onFailure(reasons: List<RouterFailure>, routeOptions: RouteOptions) {
                Log.e("MapScreen", "Route request failed: $reasons")
            }

            override fun onCanceled(routeOptions: RouteOptions, @RouterOrigin routerOrigin: String) {
                Log.w("MapScreen", "Route request canceled")
            }
        })
    }
    Box(modifier = Modifier.fillMaxSize()) {

        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapViewportState = mapViewportState,
            style = {
                MapboxStandardStyle(
                    standardStyleState = rememberStandardStyleState {
                        configurationsState.apply {
                            lightPreset = LightPresetValue.DAWN
                            showPointOfInterestLabels = BooleanValue(true)
                        }
                    }
                )
            }
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
                mapViewRef = mapView
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
                onFavoriteToggle = {
                    CoroutineScope(Dispatchers.IO).launch {
                        selectedPlace?.let { place ->
                            if (isBookmarked.value) {
                                dao.removeBookmark(place.toBookmarkPlace())
                            } else {
                                dao.addBookmark(place.toBookmarkPlace())
                            }
                            isBookmarked.value = !isBookmarked.value
                        }
                    }
                },
                fetchRoute = { origin, destination ->
                    fetchAndDrawRoute(origin, destination)
                },
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
    fetchRoute: (Point, Point) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val currentLoc by navigationObserver.location.collectAsState(
        initial = navigationObserver.getLastKnownLocation()
    )
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(8.dp),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { isExpanded = !isExpanded },
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Text(
                text = place.name,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            place.address?.formattedAddress?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            if (!isExpanded) {
                Text(
                    text = "Tap for details",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Distance: ${place.distanceMeters}m",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(6.dp))
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
                    OutlinedButton(onClick = {
                        coroutineScope.launch {
                            currentLoc?.let { loc ->
                                val origin = Point.fromLngLat(loc.longitude, loc.latitude)
                                val destination = place.coordinate
                                fetchRoute(origin, destination)
                            }
                        }
                    })
                    {
                        Text(if (currentLoc != null) "Show me the Way!" else "Waiting for GPS…")
                    }

                }
            }
        }
    }
}
