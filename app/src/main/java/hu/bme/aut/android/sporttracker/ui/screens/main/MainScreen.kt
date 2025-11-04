package hu.bme.aut.android.sporttracker.ui.screens.main

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.google.android.gms.location.FusedLocationProviderClient
import hu.bme.aut.android.sporttracker.MainActivity
import hu.bme.aut.android.sporttracker.data.repository.impl.LocationRepositoryImpl
import hu.bme.aut.android.sporttracker.ui.screens.map.MapScreen
import hu.bme.aut.android.sporttracker.ui.sign_in.UserData
import hu.bme.aut.android.sporttracker.ui.viewModels.TourSettingsViewModel
import hu.bme.aut.android.sporttracker.ui.viewModels.TourStartedSettingsViewModel
import hu.bme.aut.android.sporttracker.ui.viewModels.LocationViewmodel
import hu.bme.aut.android.sporttracker.ui.viewModels.RoutePlannerViewModel


@Composable
fun MainScreen(
    activity: MainActivity,
    fusedLocationClient: FusedLocationProviderClient,
    locationRepositoryImpl: LocationRepositoryImpl,
    tourSettingsViewModel: TourSettingsViewModel,
    tourStartedSettingsViewModel: TourStartedSettingsViewModel,
    locationViewmodel: LocationViewmodel,
    routePlannerViewModel: RoutePlannerViewModel,
    drawerState: DrawerState,
    onMenuclick: () -> Unit,
    onToursClick: () -> Unit,
    onMapClick: () -> Unit,
    onAllToursClick: () -> Unit,
    userData: UserData?,
    onSignOut: () -> Unit
) {
    MainLayout(
        iconTint = Color.Black,
        drawerState = drawerState,
        onMenuClick = onMenuclick,
        onToursClick = onToursClick,
        onMapClick = onMapClick,
        onAllToursClick = onAllToursClick,
        userData = userData,
        onSignOut = onSignOut
    ) {
        MapScreen(
            activity = activity,
            fusedLocationClient = fusedLocationClient,
            userLocation = remember { mutableStateOf(null) },
            locationPermissionGranted = locationViewmodel.locationPermissionGranted,
            locationRepositoryImpl = locationRepositoryImpl,
            tourSettingsViewModel = tourSettingsViewModel,
            tourStartedSettingsViewModel = tourStartedSettingsViewModel,
            routePlannerViewModel = routePlannerViewModel
        )
    }
}