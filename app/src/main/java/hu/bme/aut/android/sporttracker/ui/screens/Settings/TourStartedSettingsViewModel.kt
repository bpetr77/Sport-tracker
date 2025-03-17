package hu.bme.aut.android.sporttracker.ui.screens.Settings

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.*

class TourStartedSettingsViewModel : ViewModel() {

    private val _locationHistory = MutableStateFlow<List<Pair<LatLng, Long>>>(emptyList())
    val locationHistory = _locationHistory.asStateFlow()

    fun addLocationUpdate(location: LatLng, timestamp: Long) {
        _locationHistory.value = _locationHistory.value + (location to timestamp)
    }

    fun calculateTotalDistance(): Float {
        val locations = _locationHistory.value.map { it.first }
        var totalDistance = 0f
        for (i in 1 until locations.size) {
            totalDistance += haversineDistance(locations[i - 1], locations[i])
        }
        return totalDistance
    }

    fun calculateSpeed(): Float {
        val locations = _locationHistory.value
        if (locations.size < 2) return 0f

        val (firstLocation, firstTime) = locations.first()
        val (lastLocation, lastTime) = locations.last()

        val totalDistance = calculateTotalDistance()
        val totalTimeSeconds = (lastTime - firstTime) / 1000.0 // millis to seconds

        return if (totalTimeSeconds > 0) (totalDistance / totalTimeSeconds * 3.6).toFloat() else 0f // km/h
    }

    private fun haversineDistance(start: LatLng, end: LatLng): Float {
        val R = 6371e3 // Earth radius in meters
        val lat1 = Math.toRadians(start.latitude)
        val lat2 = Math.toRadians(end.latitude)
        val deltaLat = Math.toRadians(end.latitude - start.latitude)
        val deltaLon = Math.toRadians(end.longitude - start.longitude)

        val a = sin(deltaLat / 2) * sin(deltaLat / 2) +
                cos(lat1) * cos(lat2) *
                sin(deltaLon / 2) * sin(deltaLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return (R * c).toFloat() // Distance in meters
    }
}
