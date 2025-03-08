package hu.bme.aut.android.sporttracker.domain.location

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.StateFlow

interface LocationTracker {
    val locations: StateFlow<List<LatLng>>
    fun startTracking()
    fun stopTracking()
}