package hu.bme.aut.android.sporttracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import hu.bme.aut.android.sporttracker.MainActivity
import hu.bme.aut.android.sporttracker.data.location.repository.LocationRepository
import hu.bme.aut.android.sporttracker.data.tour.repository.TourRepository
import hu.bme.aut.android.sporttracker.domain.usecase.TourUseCase
import hu.bme.aut.android.sporttracker.ui.screens.main.MainScreen
import hu.bme.aut.android.sporttracker.ui.screens.menu.MenuScreen
import hu.bme.aut.android.sporttracker.ui.screens.menu.TourMenuScreen
import hu.bme.aut.android.sporttracker.ui.viewModels.LocationViewmodel
import hu.bme.aut.android.sporttracker.ui.viewModels.TourSettingsViewModel
import hu.bme.aut.android.sporttracker.ui.viewModels.TourStartedSettingsViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import hu.bme.aut.android.sporttracker.ui.screens.tour.TourDetailsScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    activity: MainActivity,
    fusedLocationClient: FusedLocationProviderClient,
    locationRepository: LocationRepository,
    tourSettingsViewModel: TourSettingsViewModel,
    tourStartedSettingsViewModel: TourStartedSettingsViewModel,
    locationViewmodel: LocationViewmodel,
    tourUseCase: TourUseCase
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)

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
                drawerState = drawerState,
                onMenuclick = { navController.navigate(Screen.Menu.route) },
                onToursClick = { navController.navigate(Screen.Tours.route) },
                onMapClick = { navController.navigate(Screen.Main.route) }
            )
        }
        composable(Screen.Menu.route){
            MenuScreen()
        }

        composable(Screen.Tours.route) {
            TourMenuScreen(
                drawerState = drawerState,
                onMenuClick = { navController.navigate(Screen.Menu.route) },
                onToursClick = { navController.navigate(Screen.Tours.route) },
                onMapClick = { navController.navigate(Screen.Main.route) },
                onTourClick = { tourId ->
                    navController.navigate("tourDetails/$tourId")
                }
            )
        }

        composable(
            route = Screen.TourDetails.route,
            arguments = listOf(navArgument("tourId") { type = NavType.LongType })
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getLong("tourId") ?: return@composable
            TourDetailsScreen(tourId = tourId)
        }
    }
}

