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