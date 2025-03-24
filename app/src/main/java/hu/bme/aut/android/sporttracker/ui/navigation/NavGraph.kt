package hu.bme.aut.android.sporttracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import hu.bme.aut.android.sporttracker.MainActivity
import hu.bme.aut.android.sporttracker.data.location.repository.LocationRepository
import hu.bme.aut.android.sporttracker.ui.screens.main.MainScreen
import hu.bme.aut.android.sporttracker.ui.screens.menu.MenuScreen
import hu.bme.aut.android.sporttracker.ui.viewModels.LocationViewmodel
import hu.bme.aut.android.sporttracker.ui.viewModels.TourSettingsViewModel
import hu.bme.aut.android.sporttracker.ui.viewModels.TourStartedSettingsViewModel

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    activity: MainActivity,
    fusedLocationClient: FusedLocationProviderClient,
    locationRepository: LocationRepository,
    tourSettingsViewModel: TourSettingsViewModel,
    tourStartedSettingsViewModel: TourStartedSettingsViewModel,
    locationViewmodel: LocationViewmodel
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
                tourStartedSettingsViewModel = tourStartedSettingsViewModel,
                locationViewmodel = locationViewmodel,
                onMenuclick = { navController.navigate(Screen.Menu.route) }
            )
        }
        composable(Screen.Menu.route){
            MenuScreen()
        }
    }
}

