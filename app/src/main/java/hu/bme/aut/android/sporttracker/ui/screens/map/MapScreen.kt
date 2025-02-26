package hu.bme.aut.android.sporttracker.ui.screens.map

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
import hu.bme.aut.android.sporttracker.R
import hu.bme.aut.android.sporttracker.data.location.getLastKnownLocation
import hu.bme.aut.android.sporttracker.ui.permissions.requestLocationPermission
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                zoomGesturesEnabled = true,
                scrollGesturesEnabled = true,
                tiltGesturesEnabled = true,
                compassEnabled = true,
                myLocationButtonEnabled = false,
                mapToolbarEnabled = true
            ),
            properties = MapProperties(
                isMyLocationEnabled = locationPermissionGranted.value
            )
        )

        FloatingActionButton(
            onClick = {
                activity.handleLocationPermission()
                if (locationPermissionGranted.value) {
                    activity.lifecycleScope.launch {
                        val newLocation = getLastKnownLocation(activity, fusedLocationClient)
                        userLocation.value = newLocation
                        newLocation?.let {
                            Log.d("MapScreen", "Animating camera to new location: $it")
                            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 15f))
                        }
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp),
            containerColor = Color.White,
            shape = CircleShape
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_my_location_24),
                contentDescription = "Location icon",
                modifier = Modifier.size(26.dp)
            )
        }

        FloatingActionButton(
            onClick = {
                coroutineScope.launch {
                    showBottomSheet = true
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color.White
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_start_24),
                contentDescription = "Record start icon",
                modifier = Modifier.size(26.dp)
            )
        }
    }

    // **🔹 Alsó lap, amely csak akkor jelenik meg, ha `showBottomSheet == true`**
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            // **🔹 Itt szerkesztheted a túra beállításait**
            TourSettingsScreen(onStartTour = {
                showBottomSheet = false
                Log.d("MapScreen", "Túra indítása...")
            })
        }
    }
}
@Composable
fun TourSettingsScreen(onStartTour: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        //horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Túra beállításai", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        Row (
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ){
            Button(onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(red = 203, green = 184, blue = 161, alpha = 255)
                ),
                shape = RoundedCornerShape(13.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_hiking_24),
                    contentDescription = "Record start icon",
                    modifier = Modifier.size(50.dp)
                )
            }

            Button(onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(red = 206, green = 175, blue = 175, alpha = 173)
                ),
                shape = RoundedCornerShape(13.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_directions_bike_24),
                    contentDescription = "Record start icon",
                    modifier = Modifier.size(50.dp)
                )
            }

            Button(onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(red = 187, green = 178, blue = 178, alpha = 139)
                ),
                shape = RoundedCornerShape(13.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_directions_car_24),
                    contentDescription = "Record start icon",
                    modifier = Modifier.size(50.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(30.dp))

        Button(onClick = { onStartTour() },
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(red = 210, green = 210, blue = 210, alpha = 255)
            )
        ) {
            Text("Túra indítása", color = Color.Black)
        }
    }
}