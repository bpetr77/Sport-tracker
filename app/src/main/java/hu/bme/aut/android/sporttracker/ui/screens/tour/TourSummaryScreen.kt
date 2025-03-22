package hu.bme.aut.android.sporttracker.ui.screens.tour

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import hu.bme.aut.android.sporttracker.ui.components.SpeedChart
import kotlinx.coroutines.launch
import hu.bme.aut.android.sporttracker.ui.screens.Settings.TourStartedSettingsViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TourSummaryScreen(
    viewModel: TourStartedSettingsViewModel,
    onDismiss: () -> Unit,
    stopLocationUpdates: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val totalDistance by viewModel.totalDistance.collectAsState()
    val averageSpeed = viewModel.getAverageSpeed()
    val duration = viewModel.getDuration()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Tour Summary", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(8.dp))


            Text(text= "⏳ Időtartam: $duration")
            Text(text= "📏 Távolság: $totalDistance")
            Text(text= "🚴 Átlagsebesség: $averageSpeed")

            Spacer(modifier = Modifier.height(8.dp))

            // Magasság adatok
            SpeedChart(viewModel.locationHistory.value.map { it.altitude.toDouble() })

            Spacer(modifier = Modifier.height(16.dp))

            // Ide jönne a térkép vagy kép az útvonalról


            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { stopLocationUpdates()
                            viewModel.stopTour()
                          },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Bezárás")
            }
        }
    }
}
