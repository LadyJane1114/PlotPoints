package com.example.plotpoints

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.plotpoints.ui.screens.BookmarksScreen
import com.example.plotpoints.ui.screens.MapScreen
import com.example.plotpoints.ui.theme.BookGreenLight

import com.example.plotpoints.ui.theme.BookGreenPage
import com.example.plotpoints.ui.theme.CompassFrameMed
import com.example.plotpoints.ui.theme.GreenText

import com.example.plotpoints.ui.theme.PlotPointsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
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
    var selectedItem by remember { mutableIntStateOf(0)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CompassFrameMed,
                    titleContentColor = GreenText
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
                        title?.let{
                            Text(it,
                                fontSize = 32.sp)
                        }
//                        Text ( LOCATION GOES HERE??,
//                            fontSize = 22.sp,
//                            modifier = Modifier.padding(5.dp))
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = CompassFrameMed,
                contentColor = GreenText
            ) {
                NavigationBarItem(
                    label = {
                        Text("Map",
                            fontSize = 18.sp)
                    },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.nav_map),
                            contentDescription = "Map Icon",
                            modifier = Modifier.size(40.dp)
                        )
                    },
                    selected = selectedItem == 1,
                    onClick = {
                        selectedItem = 1
                        navController.navigate("Map")
                    },
                    colors = NavigationBarItemColors(
                        selectedIconColor = GreenText,
                        unselectedIconColor = GreenText,
                        selectedTextColor = GreenText,
                        unselectedTextColor = GreenText,
                        selectedIndicatorColor = BookGreenPage,
                        disabledIconColor = Transparent,
                        disabledTextColor = BookGreenLight,
                    )
                )
                NavigationBarItem(
                    label = {
                        Text("Bookmarks",
                            fontSize = 18.sp)
                    },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.nav_book),
                            contentDescription = "Pile of Books icon",
                            modifier = Modifier.size(40.dp)
                        )
                    },
                    selected = selectedItem == 0,
                    onClick = {
                        selectedItem = 0
                        navController.navigate("Bookmarks")
                    },
                    colors = NavigationBarItemColors(
                        selectedIconColor = GreenText,
                        unselectedIconColor = GreenText,
                        selectedTextColor = GreenText,
                        unselectedTextColor = GreenText,
                        selectedIndicatorColor = BookGreenPage,
                        disabledIconColor = Transparent,
                        disabledTextColor = BookGreenLight,
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
