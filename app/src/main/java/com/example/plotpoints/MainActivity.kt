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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.compose.PlotPointsTheme
import com.example.plotpoints.ui.screens.BookmarksScreen
import com.example.plotpoints.ui.screens.MapScreen
import kotlin.getValue


class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission())
    { isGranted ->
        if (isGranted) {
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
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current

            // Check if permission granted
            LaunchedEffect(Unit) {
                if (ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    Log.i("TESTING", "Permission previously granted, proceed...")
                } else {
                    Log.i("TESTING", "Permission not yet granted, launching permission request...")
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
            PlotPointsTheme {
                DisplayUI()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayUI() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var selectedItem by remember { mutableIntStateOf(0)
    }

    Scaffold(
        topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    title = {
                        Column {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination?.route
                            val title = when (currentDestination) {
                                "Map" -> "Map"
                                "Bookmarks" -> "Bookmarks"
                                else -> null
                            }
                            title?.let {
                                Text(
                                    it,
                                    fontSize = 32.sp
                                )
                            }
                        }
                    }
                )
        },
        bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    contentColor = MaterialTheme.colorScheme.primaryContainer
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
                            unselectedIconColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedTextColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedIndicatorColor = MaterialTheme.colorScheme.onSecondary,
                            disabledIconColor = Transparent,
                            disabledTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
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
                            unselectedIconColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedTextColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedIndicatorColor = MaterialTheme.colorScheme.onSecondary,
                            disabledIconColor = Transparent,
                            disabledTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
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
                MapScreen()
            }
            composable("Bookmarks")
            {
                BookmarksScreen()
            }
        }}
}
