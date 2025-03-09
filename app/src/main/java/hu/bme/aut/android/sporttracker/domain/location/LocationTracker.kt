package hu.bme.aut.android.sporttracker.domain.location

import com.google.android.gms.maps.model.LatLng
import hu.bme.aut.android.sporttracker.data.location.model.LocationPoint
import kotlinx.coroutines.flow.StateFlow

interface LocationTracker {
    val locations: StateFlow<List<LocationPoint>>
    fun startLocationUpdates()
    fun stopLocationUpdates()
    fun pauseLocationUpdates()
}