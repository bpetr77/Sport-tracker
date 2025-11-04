package hu.bme.aut.android.sporttracker.domain.model.tour

data class LocationPoint(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val timestamp: Long
)