package hu.bme.aut.android.sporttracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import hu.bme.aut.android.sporttracker.MainActivity
import hu.bme.aut.android.sporttracker.data.location.repository.LocationRepository
import hu.bme.aut.android.sporttracker.ui.screens.main.MainScreen
import hu.bme.aut.android.sporttracker.ui.navigation.Screen
import hu.bme.aut.android.sporttracker.ui.screens.Settings.TourSettingsViewModel
import hu.bme.aut.android.sporttracker.ui.screens.Settings.TourStartedSettingsViewModel

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    activity: MainActivity,
    fusedLocationClient: FusedLocationProviderClient,
    locationRepository: LocationRepository,
    tourSettingsViewModel: TourSettingsViewModel,
    tourStartedSettingsViewModel: TourStartedSettingsViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable("main") {
            MainScreen(
                activity = activity,
                fusedLocationClient = fusedLocationClient,
                locationRepository = locationRepository,
                tourSettingsViewModel = tourSettingsViewModel,
                tourStartedSettingsViewModel = tourStartedSettingsViewModel
            )
        }
    }
}

