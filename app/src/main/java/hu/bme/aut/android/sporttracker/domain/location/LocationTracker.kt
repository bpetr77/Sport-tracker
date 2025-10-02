package hu.bme.aut.android.sporttracker.domain.location

import hu.bme.aut.android.sporttracker.data.model.LocationPoint
import kotlinx.coroutines.flow.StateFlow

interface LocationTracker {
    val locations: StateFlow<List<LocationPoint>>
    fun startLocationUpdates()
    fun stopLocationUpdates()
    fun pauseLocationUpdates()
    fun resumeLocationUpdates()
}