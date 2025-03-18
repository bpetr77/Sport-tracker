package hu.bme.aut.android.sporttracker.ui.screens.Settings

//import android.os.Build.VERSION_CODES.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import hu.bme.aut.android.sporttracker.R
import hu.bme.aut.android.sporttracker.data.location.model.LocationPoint
import hu.bme.aut.android.sporttracker.ui.components.SpeedChart


@Composable
fun TourStartedSettingsScreen(
    stopLocationUpdates: () -> Unit,
    pauseLocationUpdates: () -> Unit,
    resumeLocationUpdates: () -> Unit,
    tourStartedSettingsViewModel: TourStartedSettingsViewModel,
    tourSettingsViewModel: TourSettingsViewModel
) {
    //val tourSettingsViewModel: TourSettingsViewModel = TourSettingsViewModel()
    val selectedTransportMode by tourSettingsViewModel.selectedTransportMode.collectAsState()
    //var isPaused by remember { mutableStateOf(false) }
    val isPaused by tourStartedSettingsViewModel.isPaused.collectAsState()
    val totalDistance by tourStartedSettingsViewModel.totalDistance.collectAsState()
    val currentSpeed by tourStartedSettingsViewModel.currentSpeed.collectAsState()
    //val locationHistory by tourStartedSettingsViewModel.locationHistory.collectAsState()

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
        Spacer(modifier = Modifier.height(60.dp))
        if(tourStartedSettingsViewModel.getSpeedHistory().isNotEmpty()) {
            SpeedChart(tourStartedSettingsViewModel)
        }
        Spacer(modifier = Modifier.height(60.dp))
        Text(text = "Total Distance: $totalDistance meters")
        Text(text = "Speed: $currentSpeed km/h")
        Spacer(modifier = Modifier.height(30.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            FloatingActionButton(
                onClick = { stopLocationUpdates()
                            tourStartedSettingsViewModel.stopTour()
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
//            CircularImageButton(
//                onClick = { stopLocationUpdates() },
//                imageResId = R.drawable.baseline_stop_24
//            )
            FloatingActionButton(
                onClick = {
                    if (isPaused) {
                        resumeLocationUpdates()
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
                    painter = painterResource(id = if (isPaused) R.drawable.baseline_start_24 else R.drawable.baseline_pause_24),
                    contentDescription = if (isPaused) "Start" else "Pause",
                    modifier = Modifier.size(37.dp)
                )
            }
        }

    }
}

@Composable
fun CircularImageButton(onClick: () -> Unit, imageResId: Int) {
    Image(
        painter = painterResource(imageResId),
        contentDescription = "Kör alakú gomb képpel",
        modifier = Modifier
            .size(50.dp)  // Teljes méret: 50 dp átmérő
            .background(Color.Gray, CircleShape)  // Szürke háttér, kör alak
            .clickable { onClick() }
    )
}