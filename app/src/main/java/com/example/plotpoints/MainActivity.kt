package com.example.plotpoints

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.compose.PlotPointsTheme
import com.example.plotpoints.ui.screens.BookmarksScreen
import com.example.plotpoints.ui.screens.MapScreen
import com.mapbox.common.location.Location
import com.mapbox.geojson.Point
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import com.mapbox.navigation.core.lifecycle.MapboxNavigationObserver
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineApiOptions
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineViewOptions
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.getValue

lateinit var routeLineApi: MapboxRouteLineApi
lateinit var routeLineView: MapboxRouteLineView
lateinit var routeLineApiOptions: MapboxRouteLineApiOptions
lateinit var routeLineViewOptions: MapboxRouteLineViewOptions



object navigationObserver: MapboxNavigationObserver {

    private val _locationFlow = MutableStateFlow<Location?>(null)
    val location: Flow<Location?> get() = _locationFlow

    private val _locationPoint = MutableStateFlow<Point?>(null)
    val locationPoint: Flow<Point?> get() = _locationPoint

    private var lastKnownLocation: Location? = null

    private val locationObserver = object : LocationObserver {
        override fun onNewRawLocation(rawLocation: Location) {}

        override fun onNewLocationMatcherResult(locationMatcherResult: LocationMatcherResult) {
            lastKnownLocation = locationMatcherResult.enhancedLocation

            _locationFlow.value = lastKnownLocation

            lastKnownLocation?.let { loc ->
                _locationPoint.value = Point.fromLngLat(loc.longitude, loc.latitude)
            }
        }
    }

    fun getLastKnownLocation() = lastKnownLocation

    override fun onAttached(mapboxNavigation: MapboxNavigation) {
        mapboxNavigation.registerLocationObserver(locationObserver)
    }

    override fun onDetached(mapboxNavigation: MapboxNavigation) {
        mapboxNavigation.unregisterLocationObserver(locationObserver)
    }
}


class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission())
    { isGranted ->
        if (isGranted) {
            startMapboxNavigation()
            Log.i("TESTING", "New permission granted by user, proceed...")
        } else {
            Log.i("TESTING", "Permission DENIED by user! Display toast...")

            Toast.makeText(
                this,
                "Please enable location permission in Settings to use this feature.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()

        // Initialize MapboxNavigation singleton
        if (!MapboxNavigationApp.isSetup()) {
            MapboxNavigationApp.setup {
                NavigationOptions.Builder(this)
                    .build()
            }
        }

        routeLineApiOptions = MapboxRouteLineApiOptions.Builder().build()

        routeLineViewOptions = MapboxRouteLineViewOptions.Builder(this)
            .build()
        routeLineApi = MapboxRouteLineApi(routeLineApiOptions)
        routeLineView = MapboxRouteLineView(routeLineViewOptions)
        // Check location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startMapboxNavigation()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        setContent {
            PlotPointsTheme {
                DisplayUI(mainViewModel)
            }
        }
    }
    private fun startMapboxNavigation() {
        MapboxNavigationApp.attach(this)
        MapboxNavigationApp.registerObserver(navigationObserver)
        MapboxNavigationApp.current()?.startTripSession()

        Log.i("MainActivity", "Mapbox Navigation started")
    }
    override fun onDestroy() {
        super.onDestroy()
        MapboxNavigationApp.current()?.stopTripSession()
        MapboxNavigationApp.unregisterObserver(navigationObserver)
        // Detach when activity is destroyed
        MapboxNavigationApp.detach(this)
        routeLineApi.cancel()
        routeLineView.cancel()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayUI(mainViewModel: MainViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var selectedItem by remember { mutableIntStateOf(0) }
    var searchActive by remember {mutableStateOf(false)}
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<PlaceAutocompleteSuggestion>>(emptyList()) }

    LaunchedEffect(mainViewModel.searchResults) {
        mainViewModel.searchResults.observeForever { results ->
            searchResults = results
        }
    }

    LaunchedEffect(mainViewModel.selectedPlace) {
        mainViewModel.selectedPlace.observeForever { place ->
            place?.let {
                // Navigate to MapScreen or update map marker
                Log.d("MainActivity", "Selected place: ${it.coordinate}")
            }
        }
    }

    val fallbackPoint = Point.fromLngLat(-63.5923, 44.6509)
    val userPoint by navigationObserver.locationPoint.collectAsState(initial = fallbackPoint)
    Scaffold(
        topBar = {
            if (currentRoute == "Map") {
                if (searchActive) {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        title = {
                            TextField(
                                value = searchQuery,
                                onValueChange = { newQuery ->
                                    searchQuery = newQuery
                                    if (newQuery.isNotBlank()) {
                                        mainViewModel.search(newQuery)
                                    } else {
                                        searchResults = emptyList()
                                    } },
                                placeholder = { Text("Search...") },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                )
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                if(searchQuery.isNotBlank()){
                                    mainViewModel.searchResults
                                }
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.search_arrow),
                                    contentDescription = "Back",
                                    modifier = Modifier
                                        .size(30.dp),
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                searchResults.firstOrNull()?.let { suggestion ->
                                    mainViewModel.selectSuggestion(suggestion)
                                    searchQuery = suggestion.name
                                    searchResults = emptyList()
                                    searchActive = false
                                }
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.search_magnify),
                                    contentDescription = "Search",
                                    modifier = Modifier
                                        .size(30.dp)
                                )
                            }
                        }
                    )
            } else {
                // NORMAL (COLLAPSED) MODE
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    title = {
                        Text("Map", fontSize = 32.sp)
                    },
                    actions = {
                        IconButton(onClick = {
                            searchActive = true
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.search_magnify),
                                contentDescription = "Search",
                                modifier = Modifier
                                    .size(30.dp)
                            )
                        }
                    }
                )
            }

            } else {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    title = {
                        Text("Bookmarks", fontSize = 32.sp)
                    }
                )
            }
                 },
        bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    NavigationBarItem(
                        label = {
                            Text(
                                "Map",
                                fontSize = 18.sp
                            )
                        },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.nav_map),
                                contentDescription = "Map Icon",
                                modifier = Modifier.size(40.dp)
                            )
                        },
                        selected = selectedItem == 0,
                        onClick = {
                            selectedItem = 0
                            navController.navigate("Map")
                        },
                        colors = NavigationBarItemColors(
                            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            unselectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedIndicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                            disabledIconColor = MaterialTheme.colorScheme.onPrimary,
                            disabledTextColor = MaterialTheme.colorScheme.onPrimary,
                        )
                    )
                    NavigationBarItem(
                        label = {
                            Text(
                                "Bookmarks",
                                fontSize = 18.sp
                            )
                        },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.nav_book),
                                contentDescription = "Pile of Books icon",
                                modifier = Modifier.size(40.dp)
                            )
                        },
                        selected = selectedItem == 1,
                        onClick = {
                            selectedItem = 1
                            navController.navigate("Bookmarks")
                        },
                        colors = NavigationBarItemColors(
                            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            unselectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedIndicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                            disabledIconColor = MaterialTheme.colorScheme.onPrimary,
                            disabledTextColor = MaterialTheme.colorScheme.onPrimary,
                        )
                    )
                }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "Map",
            modifier = Modifier.padding(innerPadding)
        )
        {
            composable("Map")
            {
                DisposableEffect(Unit) {
                    onDispose {
                        mainViewModel.clearSearch()
                        searchQuery = ""
                        searchActive = false
                    }
                }

                MapScreen(
                    mainViewModel = mainViewModel,
                    routeLineApi = routeLineApi,
                    routeLineView = routeLineView,
                    selectedPlace = mainViewModel.selectedPlace.observeAsState().value,
                    userLocation = userPoint
                )
                if (searchResults.isNotEmpty() && searchActive) {
                    androidx.compose.foundation.lazy
                        .LazyColumn (
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .heightIn(max = 250.dp)
                                .zIndex(1f)
                        ){
                        items(searchResults) { suggestion ->
                            Text(
                                text = "${suggestion.name} - ${suggestion.formattedAddress}",
                                modifier = Modifier
                                    .padding(8.dp)
                                    .clickable {
                                        mainViewModel.selectSuggestion(suggestion)
                                        searchQuery = suggestion.name
                                        searchResults = emptyList()
                                        searchActive = false
                                    }

                            )
                        }
                    }
                }
            }

            composable("Bookmarks")
            {
                BookmarksScreen()
            }
        }
    }
}
