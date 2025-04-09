package hu.bme.aut.android.sporttracker.ui.screens.tour

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.GoogleMap
import hu.bme.aut.android.sporttracker.data.phoneData.getScreenSize
import hu.bme.aut.android.sporttracker.data.service.MapsService
import hu.bme.aut.android.sporttracker.ui.components.SpeedChart
import hu.bme.aut.android.sporttracker.ui.viewModels.TourStartedSettingsViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import hu.bme.aut.android.sporttracker.R

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TourSummaryScreen(
    viewModel: TourStartedSettingsViewModel,
    onDismiss: () -> Unit,
    stopLocationUpdates: () -> Unit,
    context: Context
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val totalDistance by viewModel.totalDistance.collectAsState()
    val averageSpeed = viewModel.getAverageSpeed()
    val duration = viewModel.getDuration()

    val staticMapUrl by remember(viewModel.locationHistory.value) {
        derivedStateOf {
            val url = MapsService.getStaticMapUrl(viewModel.locationHistory.value.map { Pair(it.latitude, it.longitude) })
            println("Static Map URL: $url")
            url
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Túra összesítés", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(8.dp))

            Text(text= "⏳ Időtartam: ${duration / 60} perc")
            Text(text= "📏 Távolság: $totalDistance")
            Text(text= "🚴 Átlagsebesség: $averageSpeed")

            Image(
                painter = rememberAsyncImagePainter(
                    model = staticMapUrl,
                    error = painterResource(R.drawable.baseline_my_location_24),
                    placeholder = painterResource(R.drawable.ic_launcher_foreground)
                ),
                contentDescription = "Térkép az útvonalról",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )

            SpeedChart(viewModel.locationHistory.value.map { it.altitude.toDouble() }, (getScreenSize().first.dp - 50.dp).value / viewModel.locationHistory.value.size)

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
