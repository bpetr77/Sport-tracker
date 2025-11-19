package hu.bme.aut.android.sporttracker.domain.util.location

import android.content.Context
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

fun getAddressFromLatLng(context: Context, latLng: LatLng?): String {
    if (latLng == null){return ""}
    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            val street = address.thoroughfare ?: ""
            val number = address.subThoroughfare ?: ""
            val city = address.locality ?: ""
            listOf(street, number, city)
                .filter { it.isNotBlank() }
                .joinToString(", ")
        } else {
            "${latLng.latitude}, ${latLng.longitude}"
        }
    } catch (e: Exception) {
        Log.e("Geocoder", "Reverse geocoding failed", e)
        "${latLng.latitude}, ${latLng.longitude}"
    }
}