package hu.bme.aut.android.sporttracker.ui.screens.map

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import hu.bme.aut.android.sporttracker.MainActivity
import hu.bme.aut.android.sporttracker.data.location.getLastKnownLocation
import hu.bme.aut.android.sporttracker.ui.permissions.requestLocationPermission
import kotlinx.coroutines.launch

@Composable
fun MapScreen(
              activity: MainActivity,
              fusedLocationClient: FusedLocationProviderClient,
              userLocation: MutableState<LatLng?>,
              locationPermissionGranted: MutableState<Boolean>
) {
    val defaultLocation = LatLng(47.497913, 19.040236) // Budapest
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation.value ?: defaultLocation, 15f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(zoomControlsEnabled = false),
            properties = MapProperties(isMyLocationEnabled = locationPermissionGranted.value)
        )

        FloatingActionButton(
            onClick = {
                activity.handleLocationPermission()
                // Update userLocation with the new location
                activity.lifecycleScope.launch {
                    val newLocation = getLastKnownLocation(activity, fusedLocationClient)
                    userLocation.value = newLocation
                    newLocation?.let {
                        Log.d("MapScreen", "Animating camera to new location: $it")
                        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 15f))
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color.White // Set the button color
        ) {
            Icon(Icons.Filled.Place, contentDescription = "Location Icon")
        }
    }
}