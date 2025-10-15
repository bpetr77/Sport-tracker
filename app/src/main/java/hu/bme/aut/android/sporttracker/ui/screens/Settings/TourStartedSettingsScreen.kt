package hu.bme.aut.android.sporttracker.ui.screens.Settings

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import hu.bme.aut.android.sporttracker.R
import hu.bme.aut.android.sporttracker.data.local.tour.database.DatabaseProvider
import hu.bme.aut.android.sporttracker.data.repository.location.TourRepository
import hu.bme.aut.android.sporttracker.ui.components.SpeedChart
import hu.bme.aut.android.sporttracker.ui.screens.tour.TourSummaryScreen
import hu.bme.aut.android.sporttracker.ui.viewModels.TourSettingsViewModel
import hu.bme.aut.android.sporttracker.ui.viewModels.TourStartedSettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun TourStartedSettingsScreen(
    stopLocationUpdates: () -> Unit,
    pauseLocationUpdates: () -> Unit,
    resumeLocationUpdates: () -> Unit,
    tourStartedSettingsViewModel: TourStartedSettingsViewModel,
    tourSettingsViewModel: TourSettingsViewModel
) {
    val selectedTransportMode by tourSettingsViewModel.selectedTransportMode.collectAsState()
    val isPaused by tourStartedSettingsViewModel.isPaused.collectAsState()
    val totalDistance by tourStartedSettingsViewModel.totalDistance.collectAsState()
    val currentSpeed by tourStartedSettingsViewModel.currentSpeed.collectAsState()
    var showTourSummaryScreen = remember { mutableStateOf(false) }

    // TODO: move this to a repository or a provider or somewhere else
    val context = LocalContext.current
    val database = DatabaseProvider.getDatabase(context)
    val tourRepository = TourRepository(database.tourDao())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        selectedTransportMode?.let { mode ->
            val imageRes = when (mode) {
                "Gyalog" -> R.drawable.baseline_hiking_24
                "Bicikli" -> R.drawable.baseline_directions_bike_24
                "Autó" -> R.drawable.baseline_directions_car_24
                else -> R.drawable.ic_launcher_foreground // Default image
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                val imageColor = if (isSystemInDarkTheme()) Color.White else Color.Black  // Sötét téma: kék, világos téma: fekete
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Selected transport mode: $mode",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(10.dp),
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(imageColor)
                )
            }
        }

        SpeedChart(tourStartedSettingsViewModel.getSpeedHistory())
        Log.w("Speed", "Speed: ${tourStartedSettingsViewModel.getSpeedHistory()}")

        SpeedChart(tourStartedSettingsViewModel.locationHistory.value.map { it.altitude })

        Spacer(modifier = Modifier.height(60.dp))

        //Text(text = "Total Distance: $totalDistance m")
        Text(text = "Távolság: ${String.format("%.2f", totalDistance / 1000)} km")
        Text(text = "Speed: $currentSpeed km/h")

        Spacer(modifier = Modifier.height(30.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            //TODO: maybe try to make the dialog screen appear from mapscreen otherwise it will be this page that will be shown under
            FloatingActionButton(
                onClick = {
                            // IS THIS NECESSARY?
//                            tourStartedSettingsViewModel.getTour(
//                                tourSettingsViewModel.selectedTransportMode.toString(),
//                                tourSettingsViewModel.weatherSelection.value,
//                                tourSettingsViewModel.commentInput.value
//                            )
                            //
                            pauseLocationUpdates()
                            showTourSummaryScreen.value = true
                            CoroutineScope(Dispatchers.IO).launch {
//                                tourRepository.addTour(tourStartedSettingsViewModel.getTour(
//                                    selectedTransportMode,
//                                    tourSettingsViewModel.weatherSelection.value,
//                                    tourSettingsViewModel.commentInput.value
//                                )
//                                )
                                //tourRepository.deleteAllTours()
                            }
                },
                modifier = Modifier
                    .padding(8.dp),
                containerColor = Color.White
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_stop_24),
                    contentDescription = "Stop",
                    modifier = Modifier.size(37.dp)
                )
            }

            FloatingActionButton(
                onClick = {
                    if (isPaused) {
                        resumeLocationUpdates()
                        tourStartedSettingsViewModel.resume()
                    } else {
                        pauseLocationUpdates()
                    }
                    tourStartedSettingsViewModel.toggleTourPaused()

                },
                modifier = Modifier
                    .padding(8.dp),
                containerColor = Color.White
            ) {
                Image(
                    painter = painterResource(id = if (isPaused) R.drawable.baseline_play_arrow_24 else R.drawable.baseline_pause_24),
                    contentDescription = if (isPaused) "Start" else "Pause",
                    modifier = Modifier.size(37.dp)
                )
            }
        }

    }
    if(showTourSummaryScreen.value) {
        TourSummaryScreen(
            viewModel = tourStartedSettingsViewModel,
            onDismiss = { showTourSummaryScreen.value = false },
            stopLocationUpdates = {stopLocationUpdates() },
            context = LocalContext.current,
            tourSettingsViewModel = tourSettingsViewModel
        )
    }
}
