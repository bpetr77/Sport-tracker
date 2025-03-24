package hu.bme.aut.android.sporttracker.ui.screens.Settings

//import android.os.Build.VERSION_CODES.R
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import hu.bme.aut.android.sporttracker.R
import hu.bme.aut.android.sporttracker.data.location.repository.LocationRepository
import hu.bme.aut.android.sporttracker.ui.components.RadioButtonSingleSelection
import hu.bme.aut.android.sporttracker.ui.components.TransportButton
import hu.bme.aut.android.sporttracker.ui.components.UserInputTextField
import hu.bme.aut.android.sporttracker.ui.viewModels.TourSettingsViewModel

@Composable
fun TourSettingsScreen(
    locationRepository: LocationRepository,
    tourSettingsViewModel: TourSettingsViewModel,
    onStartTour: (String) -> Unit,
    onStopTour: () -> Unit
) {
    val selectedTransportMode by tourSettingsViewModel.selectedTransportMode.collectAsState()
    val isTourStarted by tourSettingsViewModel.isTourStarted.collectAsState()

    //var selectedTransportMode by remember { mutableStateOf<String?>(null) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "Túra beállításai", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            TransportButton(
                transportType = "Gyalog",
                iconRes = R.drawable.baseline_hiking_24,
                selectedTransportMode = selectedTransportMode,
                onTransportSelected = { tourSettingsViewModel.selectTransportMode(it) } //onTransportSelected = { selectedTransportMode = it }
            )

            TransportButton(
                transportType = "Bicikli",
                iconRes = R.drawable.baseline_directions_bike_24,
                selectedTransportMode = selectedTransportMode,
                onTransportSelected = { tourSettingsViewModel.selectTransportMode(it) }
            )

            TransportButton(
                transportType = "Autó",
                iconRes = R.drawable.baseline_directions_car_24,
                selectedTransportMode = selectedTransportMode,
                onTransportSelected = { tourSettingsViewModel.selectTransportMode(it) }
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        RadioButtonSingleSelection(Modifier)

        UserInputTextField()

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                if (isTourStarted) {
                    locationRepository.stopLocationUpdates()
                    onStopTour()
                } else {
                    if (selectedTransportMode != null) {
                        locationRepository.startLocationUpdates()
                        onStartTour(selectedTransportMode!!)
                    } else {
                        Log.w("TourSettingsScreen", "Nem választottál közlekedési módot!")
                    }
                }
                tourSettingsViewModel.toggleTourState()
            },
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (!isTourStarted) Color(210, 210, 210, 255) else Color(210, 50, 50, 255)
            )
        ) {
            Text(
                text = if (isTourStarted) "Túra leállítása" else "Túra indítása",
                color = Color.Black
            )
        }
    }
}
