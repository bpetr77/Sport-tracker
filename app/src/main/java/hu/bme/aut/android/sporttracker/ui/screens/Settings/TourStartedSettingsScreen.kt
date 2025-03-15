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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import hu.bme.aut.android.sporttracker.R


@Composable
fun TourStartedSettingsScreen() {
    val tourSettingsViewModel: TourSettingsViewModel = TourSettingsViewModel()
    val selectedTransportMode by tourSettingsViewModel.selectedTransportMode.collectAsState()

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
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Selected transport mode: $mode"
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { /* Handle click */ },
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
                onClick = { /* Handle click */ },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                modifier = Modifier.padding(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_pause_24),
                    contentDescription = "Pause",
                    modifier = Modifier.size(50.dp)
                )
            }
        }
    }
}
