package hu.bme.aut.android.sporttracker.domain.repository

import hu.bme.aut.android.sporttracker.domain.model.tour.LocationPoint
import kotlinx.coroutines.flow.StateFlow

interface LocationRepository {
    val locations: StateFlow<List<LocationPoint>>
    fun startLocationUpdates()
    fun stopLocationUpdates()
    fun pauseLocationUpdates()
    fun resumeLocationUpdates()
}