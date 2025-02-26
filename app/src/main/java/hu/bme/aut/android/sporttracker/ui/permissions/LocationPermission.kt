package hu.bme.aut.android.sporttracker.ui.permissions

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import hu.bme.aut.android.sporttracker.data.location.getLastKnownLocation

//suspend fun requestLocationPermission(activity: ComponentActivity, fusedLocationClient: FusedLocationProviderClient, requestCode: Int): Boolean {
//    return if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requestCode)
//        false
//    } else {
//        getLastKnownLocation(activity, fusedLocationClient)
//        true
//    }
//}

fun requestLocationPermission(activity: ComponentActivity, callback: (Boolean) -> Unit) {
    val launcher = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        callback(isGranted)
    }
    launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
}

