package hu.bme.aut.android.sporttracker.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import hu.bme.aut.android.sporttracker.MainActivity
import hu.bme.aut.android.sporttracker.R
import hu.bme.aut.android.sporttracker.data.location.repository.LocationRepository
import hu.bme.aut.android.sporttracker.ui.screens.map.MapScreen
import hu.bme.aut.android.sporttracker.ui.screens.Settings.TourSettingsViewModel
import hu.bme.aut.android.sporttracker.ui.screens.Settings.TourStartedSettingsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    activity: MainActivity,
    fusedLocationClient: FusedLocationProviderClient,
    locationRepository: LocationRepository,
    tourSettingsViewModel: TourSettingsViewModel,
    tourStartedSettingsViewModel: TourStartedSettingsViewModel
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedScreen by remember { mutableStateOf("Map") }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = false,
        drawerContent = {
            ModalDrawerSheet {
                Text("Drawer title", modifier = Modifier.padding(16.dp))
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(text = "Completed Tours") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() }/* Handle navigation to completed tours */ }
                )
                // ...other drawer items
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 🔹 A térkép mindig a háttérben marad
            MapScreen(
                activity = activity,
                fusedLocationClient = fusedLocationClient,
                userLocation = remember { mutableStateOf(null) },
                locationPermissionGranted = remember { mutableStateOf(false) },
                locationRepository = locationRepository,
                tourSettingsViewModel = tourSettingsViewModel,
                tourStartedSettingsViewModel = tourStartedSettingsViewModel
            )

            // 🔹 TopBar, ahol a hamburger ikon van
            IconButton(
                onClick = {
                    scope.launch {
                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
                    }
                },
                modifier = Modifier.padding(16.dp, 30.dp)
            ) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.Black)
            }

            // 🔹 Ha más képernyőt választunk a menüben, az jelenik meg
//            when (selectedScreen) {
//                "Completed Tours" -> CompletedToursScreen()
//            }
        }
    }
}