package hu.bme.aut.android.sporttracker.ui.screens.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import hu.bme.aut.android.sporttracker.MainActivity
import hu.bme.aut.android.sporttracker.data.location.getLastKnownLocation
import hu.bme.aut.android.sporttracker.ui.permissions.requestLocationPermission
import kotlinx.coroutines.launch

@Composable
fun MapScreen(activity: ComponentActivity, fusedLocationClient: FusedLocationProviderClient, location: LatLng? = null) {
    val userLocation = remember { mutableStateOf<LatLng?>(null) }
    val defaultLocation = LatLng(47.497913, 19.040236) // Budapest
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location ?: defaultLocation, 15f)
    }

    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.weight(1f),
            cameraPositionState = cameraPositionState
        )

        Button(
            onClick = {
                coroutineScope.launch {
                    (activity as MainActivity).handleLocationPermission()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Kérj helymeghatározási engedélyt")
        }
    }
}
