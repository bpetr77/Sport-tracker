package hu.bme.aut.android.sporttracker.data.location

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import android.Manifest
import android.util.Log
import kotlinx.coroutines.tasks.await

suspend fun getLastKnownLocation(activity: ComponentActivity, fusedLocationClient: FusedLocationProviderClient): LatLng? {
    if (ActivityCompat.checkSelfPermission(
            activity, Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(
            activity, Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        Log.w("Location", "Location permission not granted")
        return null
    }

    val location = fusedLocationClient.lastLocation.await()  // Await the asynchronous call
    return location?.let { LatLng(it.latitude, it.longitude) }
}
//class LocationViewModel : ViewModel() {
//    val userLocation = mutableStateOf<LatLng?>(null)

//    fun getLastKnownLocation(
//        activity: ComponentActivity,
//        fusedLocationClient: FusedLocationProviderClient
//    ) {
//        if (ActivityCompat.checkSelfPermission(
//                activity,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                activity,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            return
//        }
//        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
//            if (location != null) {
//                val userLocation = LatLng(location.latitude, location.longitude)
//                activity.setContent {
//                    MapScreen(activity, fusedLocationClient, userLocation)
//                }
//            } else {
//                activity.setContent {
//                    MapScreen(activity, fusedLocationClient)
//                }
//            }
//        }
//    }


//}