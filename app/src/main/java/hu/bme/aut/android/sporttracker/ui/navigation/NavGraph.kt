package hu.bme.aut.android.sporttracker.ui.navigation

import android.app.Activity.RESULT_OK
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.location.FusedLocationProviderClient
import hu.bme.aut.android.sporttracker.MainActivity
import hu.bme.aut.android.sporttracker.data.repository.location.LocationRepository
import hu.bme.aut.android.sporttracker.data.local.tour.database.DatabaseProvider
import hu.bme.aut.android.sporttracker.data.repository.location.TourRepository
import hu.bme.aut.android.sporttracker.ui.screens.main.MainScreen
import hu.bme.aut.android.sporttracker.ui.screens.menu.MenuScreen
import hu.bme.aut.android.sporttracker.ui.screens.menu.TourMenuScreen
import hu.bme.aut.android.sporttracker.ui.screens.tour.AllToursScreen
import hu.bme.aut.android.sporttracker.ui.screens.tour.TourDetailsScreen
import hu.bme.aut.android.sporttracker.ui.sign_in.GoogleAuthUiClient
import hu.bme.aut.android.sporttracker.ui.sign_in.SignInScreen
import hu.bme.aut.android.sporttracker.ui.sign_in.SignInViewModel
import hu.bme.aut.android.sporttracker.ui.viewModels.LocationViewmodel
import hu.bme.aut.android.sporttracker.ui.viewModels.RoutePlannerViewModel
import hu.bme.aut.android.sporttracker.ui.viewModels.SettingsViewModel
import hu.bme.aut.android.sporttracker.ui.viewModels.TourSettingsViewModel
import hu.bme.aut.android.sporttracker.ui.viewModels.TourStartedSettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    activity: MainActivity,
    fusedLocationClient: FusedLocationProviderClient,
    locationRepository: LocationRepository,
    tourSettingsViewModel: TourSettingsViewModel,
    tourStartedSettingsViewModel: TourStartedSettingsViewModel,
    locationViewmodel: LocationViewmodel,
    signInViewModel: SignInViewModel,
    routePlannerViewModel: RoutePlannerViewModel,
    settingsViewModel: SettingsViewModel,
    googleAuthUiClient: GoogleAuthUiClient
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    NavHost(
        navController = navController,
        startDestination = Screen.SignIn.route
    ) {
        composable("sign_in") {
            val viewmodel = signInViewModel
            val state by viewmodel.state.collectAsStateWithLifecycle()

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult(),
                onResult = { result ->
                    if (result.resultCode == RESULT_OK) {
                        activity.lifecycleScope.launch {
                            val signInResult = googleAuthUiClient.signInWithIntent(
                                intent = result.data ?: return@launch
                            )
                            viewmodel.onSignInResult(signInResult)
                        }
                    }
                }
            )
//            LaunchedEffect(key1 = state.isSignInSuccessful) {
//                if (state.isSignInSuccessful) {
//                    Toast.makeText(
//                        activity.applicationContext,
//                        "Sign in successful",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//                if (googleAuthUiClient.getSignedInUser() != null) {
//                    navController.navigate("main")
//                }
//            }
            LaunchedEffect(key1 = state.isSignInSuccessful) {
                val signedInUser = googleAuthUiClient.getSignedInUser()
                if (state.isSignInSuccessful && signedInUser != null) {
                    Toast.makeText(
                        activity.applicationContext,
                        "Sign in successful",
                        Toast.LENGTH_LONG
                    ).show()
                    navController.navigate("main")
                }
            }
            SignInScreen(
                state = state,
                onSignInClick = {
                    activity.lifecycleScope.launch {
                        val signInIntentSender = googleAuthUiClient.signIn()
                        launcher.launch(
                            IntentSenderRequest.Builder(
                                signInIntentSender ?: return@launch
                            ).build()
                        )
                    }
                }
            )
        }
        composable("main") {
            MainScreen(
                activity = activity,
                fusedLocationClient = fusedLocationClient,
                locationRepository = locationRepository,
                tourSettingsViewModel = tourSettingsViewModel,
                tourStartedSettingsViewModel = tourStartedSettingsViewModel,
                locationViewmodel = locationViewmodel,
                routePlannerViewModel = routePlannerViewModel,
                drawerState = drawerState,
                onMenuclick = { navController.navigate(Screen.Menu.route) },
                onToursClick = { navController.navigate(Screen.Tours.route) },
                onMapClick = { navController.navigate(Screen.Main.route) },
                onAllToursClick = { navController.navigate(Screen.AllTours.route) },
                userData = googleAuthUiClient.getSignedInUser(),
                onSignOut = {
                    activity.lifecycleScope.launch {
                        googleAuthUiClient.signOut(context = activity)
                        signInViewModel.resetState()
                    }
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Menu.route) {
            MenuScreen(
                drawerState = drawerState,
                onMenuClick = { navController.navigate(Screen.Menu.route) },
                onToursClick = { navController.navigate(Screen.Tours.route) },
                onMapClick = { navController.navigate(Screen.Main.route) },
                onAllToursClick = { navController.navigate(Screen.AllTours.route) },
                viewModel = settingsViewModel,
                userData = googleAuthUiClient.getSignedInUser(),
                onSignOut = {
                    activity.lifecycleScope.launch {
                        googleAuthUiClient.signOut(context = activity)
                        signInViewModel.resetState()
                    }
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Tours.route) {
            TourMenuScreen(
                drawerState = drawerState,
                onMenuClick = { navController.navigate(Screen.Menu.route) },
                onToursClick = { navController.navigate(Screen.Tours.route) },
                onMapClick = { navController.navigate(Screen.Main.route) },
                onTourClick = { tourId ->
                    navController.navigate("tourDetails/$tourId")
                },
                onAllToursClick = { navController.navigate(Screen.AllTours.route) },
                userData = googleAuthUiClient.getSignedInUser(),
                onSignOut = {
                    activity.lifecycleScope.launch {
                        googleAuthUiClient.signOut(context = activity)
                        signInViewModel.resetState()
                    }
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                },
                user = signInViewModel.currentUser.value ?: "",
                tourViewModel = tourStartedSettingsViewModel
            )
        }

        // TODO: Move this to a repository or a provider or somewhere else
        val database = DatabaseProvider.getDatabase(activity)
        val tourRepository = TourRepository(database.tourDao())

        composable(
            route = Screen.TourDetails.route,
            arguments = listOf(navArgument("tourId") { type = NavType.LongType })
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getLong("tourId") ?: return@composable
            TourDetailsScreen(tourId = tourId,
                tourRepository = tourRepository,
                drawerState = drawerState,
                onMenuClick = { navController.navigate(Screen.Menu.route) },
                onToursClick = { navController.navigate(Screen.Tours.route) },
                onMapClick = { navController.navigate(Screen.Main.route) },
                onAllToursClick = { navController.navigate(Screen.AllTours.route) },
                userData = googleAuthUiClient.getSignedInUser(),
                onSignOut = {
                    activity.lifecycleScope.launch {
                        googleAuthUiClient.signOut(context = activity)
                        signInViewModel.resetState()
                    }
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.AllTours.route) {
            AllToursScreen(
                tourRepository = tourRepository,
                drawerState = drawerState,
                onMenuClick = { navController.navigate(Screen.Menu.route) },
                onToursClick = { navController.navigate(Screen.Tours.route) },
                onMapClick = { navController.navigate(Screen.Main.route) },
                onAllToursClick = { navController.navigate(Screen.AllTours.route) },
                userData = googleAuthUiClient.getSignedInUser(),
                onSignOut = {
                    activity.lifecycleScope.launch {
                        googleAuthUiClient.signOut(context = activity)
                        signInViewModel.resetState()
                    }
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

