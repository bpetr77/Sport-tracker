package hu.bme.aut.android.sporttracker.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.FusedLocationProviderClient
import hu.bme.aut.android.sporttracker.MainActivity
import hu.bme.aut.android.sporttracker.data.location.repository.LocationRepository
import hu.bme.aut.android.sporttracker.domain.usecase.TourUseCase
import hu.bme.aut.android.sporttracker.ui.navigation.Screen
import hu.bme.aut.android.sporttracker.ui.screens.map.MapScreen
import hu.bme.aut.android.sporttracker.ui.viewModels.TourSettingsViewModel
import hu.bme.aut.android.sporttracker.ui.viewModels.TourStartedSettingsViewModel
import hu.bme.aut.android.sporttracker.ui.viewModels.LocationViewmodel
import kotlinx.coroutines.launch

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MainScreen(
//    activity: MainActivity,
//    fusedLocationClient: FusedLocationProviderClient,
//    locationRepository: LocationRepository,
//    tourSettingsViewModel: TourSettingsViewModel,
//    tourStartedSettingsViewModel: TourStartedSettingsViewModel,
//    locationViewmodel: LocationViewmodel,
//    onMenuclick: () -> Unit,
//    tourUseCase: TourUseCase,
//    onToursClick: () -> Unit
//) {
//    val drawerState = rememberDrawerState(DrawerValue.Closed)
//    val scope = rememberCoroutineScope()
//    var selectedScreen by remember { mutableStateOf("Map") }
//
//    ModalNavigationDrawer(
//        drawerState = drawerState,
//        gesturesEnabled = true,
//        drawerContent = {
//            ModalDrawerSheet {
//                Text("Sport Tracker", modifier = Modifier.padding(16.dp))
//                HorizontalDivider()
//                NavigationDrawerItem(
//                    label = { Text(text = "Completed Tours") },
//                    selected = false,
//                    onClick = { scope.launch { onToursClick() } }
//                )
//                NavigationDrawerItem(
//                    label = { Text(text = "Settings") },
//                    selected = false,
//                    onClick = { scope.launch { onMenuclick() } /* Handle navigation to settings */ }
//                )
//                NavigationDrawerItem(
//                    label = { Text(text = "Map") },
//                    selected = false,
//                    onClick = {
//                        selectedScreen = "Map"
//                        scope.launch { drawerState.close() }
//                    }
//                )
//                // ...other drawer items
//            }
//        }
//    ) {
//        Box(modifier = Modifier.fillMaxSize()) {
//            MapScreen(
//                activity = activity,
//                fusedLocationClient = fusedLocationClient,
//                userLocation = remember { mutableStateOf(null) },
//                locationPermissionGranted = locationViewmodel.locationPermissionGranted,
//                locationRepository = locationRepository,
//                tourSettingsViewModel = tourSettingsViewModel,
//                tourStartedSettingsViewModel = tourStartedSettingsViewModel
//            )
//
//            IconButton(
//                onClick = {
//                    scope.launch {
//                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
//                    }
//                },
//                modifier = Modifier.padding(16.dp, 30.dp)
//            ) {
//                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.Black)
//            }
//
//            // 🔹 Ha más képernyőt választunk a menüben, az jelenik meg
////            when (selectedScreen) {
////                "Completed Tours" -> CompletedToursScreen()
////            }
//        }
//    }
//}

@Composable
fun MainScreen(
    activity: MainActivity,
    fusedLocationClient: FusedLocationProviderClient,
    locationRepository: LocationRepository,
    tourSettingsViewModel: TourSettingsViewModel,
    tourStartedSettingsViewModel: TourStartedSettingsViewModel,
    locationViewmodel: LocationViewmodel,
    drawerState: DrawerState,
    onMenuclick: () -> Unit,
    onToursClick: () -> Unit,
    onMapClick: () -> Unit
) {
    //val drawerState = rememberDrawerState(DrawerValue.Closed)

    MainLayout(drawerState = drawerState, onMenuClick = onMenuclick, onToursClick = onToursClick, onMapClick = onMapClick) {
        MapScreen(
            activity = activity,
            fusedLocationClient = fusedLocationClient,
            userLocation = remember { mutableStateOf(null) },
            locationPermissionGranted = locationViewmodel.locationPermissionGranted,
            locationRepository = locationRepository,
            tourSettingsViewModel = tourSettingsViewModel,
            tourStartedSettingsViewModel = tourStartedSettingsViewModel
        )
    }
}