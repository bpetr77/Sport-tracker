package hu.bme.aut.android.sporttracker.ui.screens.Settings

//import android.os.Build.VERSION_CODES.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import hu.bme.aut.android.sporttracker.R
import hu.bme.aut.android.sporttracker.data.location.model.LocationPoint


// TODO: ViewModel implemet over this class will be
@Composable
fun TourStartedSettingsScreen(
    stopLocationUpdates: () -> Unit,
    pauseLocationUpdates: () -> Unit,
    resumeLocationUpdates: () -> Unit,
    tourStartedSettingsViewModel: TourStartedSettingsViewModel,
    locations: List<LocationPoint>
) {
    val tourSettingsViewModel: TourSettingsViewModel = TourSettingsViewModel()
    val selectedTransportMode by tourSettingsViewModel.selectedTransportMode.collectAsState()
    var isPaused by remember { mutableStateOf(false) }
    //val locationHistory by tourStartedSettingsViewModel.locationHistory.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // TODO: not working image selection
        selectedTransportMode?.let { mode ->
            val imageRes = when (mode) {
                "Gyalog" -> R.drawable.baseline_hiking_24
                "Bicikli" -> R.drawable.baseline_directions_bike_24
                "Autó" -> R.drawable.baseline_directions_car_24
                else -> R.drawable.ic_launcher_foreground // Default image
            }
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Selected transport mode: $mode"
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { stopLocationUpdates() },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                modifier = Modifier.padding(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_stop_24),
                    contentDescription = "Stop",
                    modifier = Modifier.size(50.dp)
                )
            }

            Button(
                onClick = {
                    if (isPaused) {
                        resumeLocationUpdates()
                    } else {
                        pauseLocationUpdates()
                    }
                    isPaused = !isPaused
                },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                modifier = Modifier.padding(8.dp)
            ) {
                Image(
                    painter = painterResource(id = if (isPaused) R.drawable.baseline_start_24 else R.drawable.baseline_pause_24),
                    contentDescription = if (isPaused) "Start" else "Pause",
                    modifier = Modifier.size(50.dp)
                )
            }
        }
        Text(text = "Total Distance: ${tourStartedSettingsViewModel.calculateTotalDistance()} meters")

        Text(text = "Speed: ${tourStartedSettingsViewModel.getCurrentSpeedInKmH()} km/h")

    }
}
